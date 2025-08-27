package com.gaea.asset.manager.device.service;

import static com.gaea.asset.manager.util.DeviceFieldUtil.appendIfPresent;
import static com.gaea.asset.manager.util.DeviceFieldUtil.isEqual;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gaea.asset.manager.code.service.CodeMapper;
import com.gaea.asset.manager.code.vo.CodeVO;
import com.gaea.asset.manager.common.constants.CodeConstants;
import com.gaea.asset.manager.common.constants.Constants;
import com.gaea.asset.manager.device.vo.DeviceHistoryVO;
import com.gaea.asset.manager.device.vo.DeviceVO;
import com.gaea.asset.manager.login.vo.UserInfoVO;
import com.gaea.asset.manager.message.service.MessageService;
import com.gaea.asset.manager.user.service.UserMapper;
import com.gaea.asset.manager.user.vo.UserVO;
import com.gaea.asset.manager.util.AuthUtil;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Pagination;
import com.gaea.asset.manager.util.Search;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceMapper deviceMapper;
    private final UserMapper userMapper;
    private final CodeMapper codeMapper;
    private final MessageService messageService;

    /**
	 * 전산 장비 목록 조회
	 * @param currentPage
	 * @param pageSize
	 * @param search
	 * @return
	 */
	public Header<List<DeviceVO>> getDeviceList(int currentPage, int pageSize, Search search) {
		UserInfoVO userInfo = AuthUtil.getLoginUserInfo();
		HashMap<String, Object> paramMap = new HashMap<>();

		if (setParamsByUserRole(userInfo, paramMap)) return Header.ERROR("403", "조회 권한이 없습니다.");
		paramMap.put("page", (currentPage - 1) * pageSize);
		paramMap.put("size", pageSize);
		paramMap.put("searchColumn", search.getSearchColumn());
		paramMap.put("searchKeyword", search.getSearchKeyword());

		List<DeviceVO> deviceList = deviceMapper.getDeviceList(paramMap);
		Pagination pagination = new Pagination(
				deviceMapper.getDeviceTotalCount(paramMap),
				currentPage,
				pageSize,
				10);

		return Header.OK(deviceList, pagination);
	}

	/**
	 * 전산 장비 상세 조회
	 * @param deviceNum
	 * @return
	 */
	public Header<DeviceVO> getDevice(Integer deviceNum) {
		UserInfoVO userInfo = AuthUtil.getLoginUserInfo();
		HashMap<String, Object> paramMap = new HashMap<>();

		paramMap.put("deviceNum", deviceNum);
		if (setParamsByUserRole(userInfo, paramMap)) return Header.ERROR("403", "조회 권한이 없습니다.");

		return Header.OK(deviceMapper.getDevice(paramMap));
	}

	/**
	 * 전산 장비 변경 정보 조회
	 * @param deviceNum
	 * @return
	 */
	public Header<DeviceVO> getDeviceTemp(Integer deviceNum) {
		return Header.OK(deviceMapper.getDeviceTemp(deviceNum));
	}

	/**
	 * 전산 장비 등록
	 * @param deviceVO
	 * @return
	 */
	@Transactional
	public Header<DeviceVO> insertDevice(DeviceVO deviceVO) {
		UserInfoVO userInfo = AuthUtil.getLoginUserInfo();

		// 일반사용자, 부서장은 등록 권한 없음
		if(StringUtils.isEmpty(userInfo.getRoleCode())
				|| CodeConstants.ROLE_USER.equals(userInfo.getRoleCode())
				|| CodeConstants.ROLE_TEAM_MANAGER.equals(userInfo.getRoleCode())) {

			return Header.ERROR("403", "등록 권한이 없습니다.");
		}

		deviceVO.setCreateUser(userInfo.getEmpNum());
		if (deviceMapper.insertDevice(deviceVO) > 0) {
			insertDeviceHistory(deviceVO, null, userInfo.getEmpNum(), Constants.REGISTER);
			try {
				messageService.sendToDeviceOwner(CodeConstants.MESSAGE_DEVICE_ASSIGNED, deviceVO.getDeviceNum());
			} catch (MessagingException e) {
				log.error(String.valueOf(e));
			}
			return Header.OK();
		} else {
			return Header.ERROR("500", "ERROR");
		}
	}

	/**
	 * 전산 장비 수정
	 * @param deviceVO
	 * @return
	 */
    @Transactional
    public Header<DeviceVO> updateDevice(DeviceVO deviceVO) {
    	UserInfoVO userInfo = AuthUtil.getLoginUserInfo();
    	HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("deviceNum", deviceVO.getDeviceNum());

    	// 일반사용자, 부서장은 본인 장비만 수정 가능
    	if(CodeConstants.ROLE_USER.equals(userInfo.getRoleCode())
    			|| CodeConstants.ROLE_TEAM_MANAGER.equals(userInfo.getRoleCode())) {
    		paramMap.put("loginEmpNum", userInfo.getEmpNum());
    	}

        DeviceVO originDevice = deviceMapper.getDevice(paramMap);
        if(originDevice == null || StringUtils.isEmpty(userInfo.getRoleCode())) {
    		return Header.ERROR("403", "수정 권한이 없습니다.");
    	}

        String currentStatus = originDevice.getApprovalStatusCode();
        deviceVO.setUpdateUser(userInfo.getEmpNum());

        // 결재 대기 상태(부서장 승인대기, 관리자 승인대기)면 수정 불가
        if (CodeConstants.APPROVAL_STATUS_TEAM_MANAGER_PENDING.equals(currentStatus)
        		|| CodeConstants.APPROVAL_STATUS_ADMIN_PENDING.equals(currentStatus)) {
            return Header.ERROR("403", "승인 대기 중인 장비는 수정할 수 없습니다.");
        }

        switch (userInfo.getRoleCode()) {
            case CodeConstants.ROLE_USER: // 일반 사용자
				// 부서장이 없는 조직일 경우, 관리자 승인 대기로 설정
				String approvalStatusCode = (userMapper.getTeamManagerCount(userInfo.getOrgId()) > 0) ? CodeConstants.APPROVAL_STATUS_TEAM_MANAGER_PENDING : CodeConstants.APPROVAL_STATUS_ADMIN_PENDING;
                originDevice.setApprovalStatusCode(approvalStatusCode);

                if (deviceMapper.insertDeviceTemp(deviceVO) > 0) {
                    deviceMapper.updateApprovalStatusCode(originDevice);
					insertDeviceHistory(originDevice, deviceVO, userInfo.getEmpNum(), Constants.UPDATE);
					try {
						messageService.sendToManager(CodeConstants.MESSAGE_STATUS_CHANGE_REQUESTED, "01");
						messageService.sendToManager(CodeConstants.MESSAGE_STATUS_CHANGE_REQUESTED, "02");
					} catch (MessagingException e) {
						log.error(String.valueOf(e));
					}
					return Header.OK();
                }
                break;
            case CodeConstants.ROLE_TEAM_MANAGER: // 부서장
                originDevice.setApprovalStatusCode(CodeConstants.APPROVAL_STATUS_ADMIN_PENDING);
                if (deviceMapper.insertDeviceTemp(deviceVO) > 0) {
                    deviceMapper.updateApprovalStatusCode(originDevice);
					insertDeviceHistory(originDevice, deviceVO, userInfo.getEmpNum(), Constants.UPDATE);
					try {
						messageService.sendToManager(CodeConstants.MESSAGE_STATUS_CHANGE_REQUESTED, "02");
					} catch (MessagingException e) {
						log.error(String.valueOf(e));
					}
					return Header.OK();
                }
                break;
            case CodeConstants.ROLE_ASSET_MANAGER: // 관리자
            case CodeConstants.ROLE_SYSTEM_MANAGER: // 시스템 관리자
				originDevice.setApprovalStatusCode(null); // 장비 결재 상태 초기화
				deviceVO.setApprovalStatusCode(null); // 장비 이력 결재 상태 초기화
                if (deviceMapper.updateDevice(deviceVO) > 0) {
					insertDeviceHistory(originDevice, deviceVO, userInfo.getEmpNum(), Constants.UPDATE);
                    return Header.OK();
                }
                break;
            default:
                return Header.ERROR("403", "수정 권한이 없습니다.");
        }
        return Header.ERROR("500", "ERROR");
    }

    /**
     * 전산 장비 삭제
     * @param deviceNum
     * @return
     */
	public Header<String> deleteDevice(Integer deviceNum) {
		UserInfoVO userInfo = AuthUtil.getLoginUserInfo();

		// 일반사용자, 부서장은 삭제 권한 없음
		if(StringUtils.isEmpty(userInfo.getRoleCode())
				|| CodeConstants.ROLE_USER.equals(userInfo.getRoleCode())
				|| CodeConstants.ROLE_TEAM_MANAGER.equals(userInfo.getRoleCode())) {

			return Header.ERROR("403", "삭제 권한이 없습니다.");
		}

		if (deviceMapper.deleteDevice(deviceNum) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("500", "ERROR");
		}
	}

	/**
	 * 전산 장비 승인
	 * @param deviceVO
	 * @return
	 */
    @Transactional
    public Header<DeviceVO> approveDeviceUpdate(DeviceVO deviceVO) {
    	HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("deviceNum", deviceVO.getDeviceNum());

        DeviceVO originDevice = deviceMapper.getDevice(paramMap);
        String currentStatus = originDevice.getApprovalStatusCode();
        UserInfoVO userInfo = AuthUtil.getLoginUserInfo();
        String nextStatus;

        // 승인 권한 확인 및 다음 결재 상태 세팅
        switch (userInfo.getRoleCode()) {
            case CodeConstants.ROLE_TEAM_MANAGER: // 부서장
                if (!CodeConstants.APPROVAL_STATUS_TEAM_MANAGER_PENDING.equals(currentStatus)) {
                    return Header.ERROR("403", "승인 권한이 없습니다.");
                }
                nextStatus = CodeConstants.APPROVAL_STATUS_ADMIN_PENDING;
                break;
            case CodeConstants.ROLE_ASSET_MANAGER: // 관리자
                if (!CodeConstants.APPROVAL_STATUS_ADMIN_PENDING.equals(currentStatus)) {
                    return Header.ERROR("403", "승인 권한이 없습니다.");
                }
                nextStatus = CodeConstants.APPROVAL_STATUS_APPROVED;
                break;
            case CodeConstants.ROLE_SYSTEM_MANAGER: // 시스템 관리자
                nextStatus = CodeConstants.APPROVAL_STATUS_APPROVED;
                break;
            default:
                return Header.ERROR("403", "승인 권한이 없습니다.");
        }

        // 결재 상태 업데이트
        originDevice.setApprovalStatusCode(nextStatus);
        deviceMapper.updateApprovalStatusCode(originDevice);

        // 최종 승인일 때만 device 정보 업데이트 + temp 삭제
        if (CodeConstants.APPROVAL_STATUS_APPROVED.equals(nextStatus)) {
            deviceVO.setApprovalStatusCode(nextStatus);
            if (deviceMapper.updateDevice(deviceVO) > 0) {
                deviceMapper.deleteDeviceTemp(deviceVO.getDeviceNum());
				insertDeviceHistory(originDevice, deviceVO, userInfo.getEmpNum(), Constants.APPROVE);
				try {
					messageService.sendToDeviceOwner(CodeConstants.MESSAGE_STATUS_CHANGE_APPROVED, deviceVO.getDeviceNum());
				} catch (MessagingException e) {
					log.error(String.valueOf(e));
				}
				return Header.OK();
            }
        }

		insertDeviceHistory(originDevice, deviceVO, userInfo.getEmpNum(), Constants.APPROVE);
		try {
			messageService.sendToDeviceOwner(CodeConstants.MESSAGE_STATUS_CHANGE_APPROVED, deviceVO.getDeviceNum());
		} catch (MessagingException e) {
			log.error(String.valueOf(e));
		}
        return Header.OK();
    }

    /**
     * 전산 장비 반려
     * @param deviceVO
     * @return
     */
    @Transactional
    public Header<DeviceVO> rejectDeviceUpdate(DeviceVO deviceVO) {
    	HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("deviceNum", deviceVO.getDeviceNum());

        DeviceVO originDevice = deviceMapper.getDevice(paramMap);
        String currentStatus = originDevice.getApprovalStatusCode();
        UserInfoVO userInfo = AuthUtil.getLoginUserInfo();

        // 권한 확인
        if (userInfo.getRoleCode() == null || CodeConstants.ROLE_USER.equals(userInfo.getRoleCode())) {
            return Header.ERROR("403", "반려 권한이 없습니다.");
        }
        if (CodeConstants.ROLE_TEAM_MANAGER.equals(userInfo.getRoleCode())
        		&& !CodeConstants.APPROVAL_STATUS_TEAM_MANAGER_PENDING.equals(currentStatus)) {
            return Header.ERROR("403", "반려 권한이 없습니다.");
        }
        if (CodeConstants.ROLE_ASSET_MANAGER.equals(userInfo.getRoleCode())
        		&& !CodeConstants.APPROVAL_STATUS_ADMIN_PENDING.equals(currentStatus)) {
            return Header.ERROR("403", "반려 권한이 없습니다.");
        }

		String nextStatus = CodeConstants.APPROVAL_STATUS_REJECTED;
        originDevice.setApprovalStatusCode(nextStatus);
		originDevice.setRejectReason(deviceVO.getRejectReason());
        if (deviceMapper.updateApprovalStatusCode(originDevice) > 0) {
            deviceMapper.deleteDeviceTemp(deviceVO.getDeviceNum());
			insertDeviceHistory(originDevice, null, userInfo.getEmpNum(), Constants.REJECT);
			try {
				messageService.sendToDeviceOwner(CodeConstants.MESSAGE_STATUS_CHANGE_REJECTED, deviceVO.getDeviceNum());
			} catch (MessagingException e) {
				log.error(String.valueOf(e));
			}
			return Header.OK();
        }

		return Header.ERROR("500", "ERROR");
	}

	// DeviceHistory 관련 메서드
    /**
     * 전산 장비 이력 목록 조회
     * @param currentPage
     * @param pageSize
     * @param search
     * @return
     */
	@Transactional
	public Header<List<DeviceHistoryVO>> getDeviceHistoryList(int currentPage, int pageSize, Search search) {
		UserInfoVO userInfo = AuthUtil.getLoginUserInfo();
		HashMap<String, Object> paramMap = new HashMap<>();

		// 페이징
		paramMap.put("page", (currentPage - 1) * pageSize);
		paramMap.put("size", pageSize);

		// 검색
		paramMap.put("searchColumn", search.getSearchColumn());
		paramMap.put("searchKeyword", search.getSearchKeyword());

		if (setParamsByUserRole(userInfo, paramMap)) return Header.ERROR("403", "조회 권한이 없습니다.");

	    // 데이터 조회
	    List<DeviceHistoryVO> historyList = deviceMapper.getDeviceHistoryList(paramMap);

	    // 총 건수로 Pagination 생성
	    Pagination pagination = new Pagination(
	            deviceMapper.getDeviceHistoryTotalCount(paramMap),
	            currentPage,
	            pageSize,
	            10);

	    return Header.OK(historyList, pagination);
	}

	/**
	 * 관리자에게 요청된 승인 목록 조회
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public Header<List<DeviceVO>> getDevicePendingList(int currentPage, int pageSize) {
		UserInfoVO userInfo = AuthUtil.getLoginUserInfo();
		HashMap<String, Object> paramMap = new HashMap<>();

		// 페이징
		paramMap.put("page", (currentPage - 1) * pageSize);
		paramMap.put("size", pageSize);

		// 조회 조건 세팅
		String userRoleCode = AuthUtil.getLoginUserInfo().getRoleCode();
		if (CodeConstants.ROLE_TEAM_MANAGER.equals(userRoleCode)) {
			paramMap.put("approvalStatusCode", CodeConstants.APPROVAL_STATUS_TEAM_MANAGER_PENDING);
		} else if (CodeConstants.ROLE_ASSET_MANAGER.equals(userRoleCode) || CodeConstants.ROLE_SYSTEM_MANAGER.equals(userRoleCode)) {
			paramMap.put("approvalStatusCode", CodeConstants.APPROVAL_STATUS_ADMIN_PENDING);
		}
		if (setParamsByUserRole(userInfo, paramMap)) return Header.ERROR("403", "조회 권한이 없습니다.");

		List<DeviceVO> historyList = deviceMapper.getDevicePendingList(paramMap);

		return Header.OK(historyList);
	}

	/**
	 * 전산 장비 이력 상세 조회
	 * @param deviceNum
	 * @return
	 */
    public Header<List<DeviceHistoryVO>> getDeviceHistory(Integer deviceNum) {
        List<DeviceHistoryVO> historyList = deviceMapper.getDeviceHistory(deviceNum);
        return Header.OK(historyList);
    }

	/**
	 * 기능별 장비 이력 추가 (REGISTER, UPDATE, APPROVE, REJECT)
	 * @param origin
	 * @param updated
	 * @param empNum
	 * @param type
	 */
	public void insertDeviceHistory(DeviceVO origin, DeviceVO updated, int empNum, String type) {
		DeviceHistoryVO history = null;

		switch (type) {
			case Constants.REGISTER: // 등록 장비 정보 요약
				history = setRegisterHistory(origin);
				break;
			case Constants.UPDATE: // 변경 사항 요약
				history = setUpdateHistory(origin, updated);
				break;
			case Constants.APPROVE: // 장비/결재 상태 저장
				history = new DeviceHistoryVO();
				history.setDeviceStatus(updated.getDeviceStatusCode());
				history.setApprovalStatus(origin.getApprovalStatusCode());
				break;
			case Constants.REJECT: // 장비/결재 상태 저장
				history = new DeviceHistoryVO();
				history.setDeviceStatus(origin.getDeviceStatusCode());
				history.setApprovalStatus(CodeConstants.APPROVAL_STATUS_REJECTED);
				history.setReason(origin.getRejectReason());
				break;
		}

		if (history != null) {
			history.setDeviceNum(origin.getDeviceNum());
			history.setCreateUser(empNum);
			history.setEmpNum(origin.getEmpNum()); // 최종 승인 시 변경된 장비 담당자 반영
		}
		deviceMapper.insertDeviceHistory(history);
	}

	/**
	 * 장비 리스트 엑셀 다운로드
	 * @param response
	 */
	public void downloadDeviceExcel(HttpServletResponse response) {
		UserInfoVO userInfo = AuthUtil.getLoginUserInfo();
		HashMap<String, Object> paramMap = new HashMap<>();
		switch (userInfo.getRoleCode()) {
			case CodeConstants.ROLE_USER:
				paramMap.put("loginEmpNum", userInfo.getEmpNum());
				break;
			case CodeConstants.ROLE_TEAM_MANAGER:
				paramMap.put("loginOrgId", userInfo.getOrgId());
				break;
			case CodeConstants.ROLE_ASSET_MANAGER:
			case CodeConstants.ROLE_SYSTEM_MANAGER:
				break;
			default:
				return;
		}

		ClassPathResource template = new ClassPathResource("excel/DEVICE.xlsx");
		try (InputStream is = template.getInputStream();
			 Workbook wb = new XSSFWorkbook(is)) {

			// 다운로드 날짜 정보
			LocalDate today = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
			String downloadDate = "다운로드 일: " + today.format(formatter);
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);
				Row row = sheet.getRow(0);
				Cell cell = row.getCell(0);
				cell.setCellValue(downloadDate);
			}

			// 디바이스 조회
			List<DeviceVO> deviceList = deviceMapper.getDeviceExcelList(paramMap);

			// 시트별 리스트 추가
			setDeviceSheet(wb.getSheetAt(0), deviceList, CodeConstants.DEVICE_TYPE_COMPUTER);
			setDeviceSheet(wb.getSheetAt(1), deviceList, CodeConstants.DEVICE_TYPE_MONITOR);
			setDeviceSheet(wb.getSheetAt(2), deviceList, CodeConstants.DEVICE_TYPE_PHONE);
			setDeviceSheet(wb.getSheetAt(3), deviceList, CodeConstants.DEVICE_TYPE_ETC);

			DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
			String fileName = "DeviceList_" + today.format(formatter2) + ".xlsx";
			response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			wb.write(response.getOutputStream());
		} catch (IOException e) {
			log.error("엑셀 다운로드 중 오류 발생", e);
		}
	}

	private DeviceHistoryVO setRegisterHistory(DeviceVO origin) {
		DeviceHistoryVO history = new DeviceHistoryVO();
		StringBuilder sb = new StringBuilder();
		appendIfPresent(sb, "장비담당자", origin.getUserName());
		appendIfPresent(sb, "모델명", origin.getModelName());
		appendIfPresent(sb, "용도구분", origin.getUsageDivision());
		appendIfPresent(sb, "제조사", origin.getManufacturer());
		appendIfPresent(sb, "제조년도", origin.getManufactureDate());
		appendIfPresent(sb, "CPU", origin.getCpuSpec());
		appendIfPresent(sb, "메모리", origin.getMemorySize());
		appendIfPresent(sb, "SSD/HDD", origin.getStorageInfo());
		appendIfPresent(sb, "OS", origin.getOperatingSystem());
		appendIfPresent(sb, "인치", origin.getScreenSize());
		appendIfPresent(sb, "GPU", origin.getGpuSpec());
		appendIfPresent(sb, "구매일자", origin.getPurchaseDate());
		appendIfPresent(sb, "반납일자", origin.getReturnDate());
		appendIfPresent(sb, "비고", origin.getRemarks());
		history.setChangeContents(sb.toString());
		history.setDeviceStatus(origin.getDeviceStatusCode());
		history.setApprovalStatus(origin.getApprovalStatusCode());

		return history;
	}

	private DeviceHistoryVO setUpdateHistory(DeviceVO origin, DeviceVO updated) {
		DeviceHistoryVO history = new DeviceHistoryVO();
		StringBuilder sb = new StringBuilder();
		if (!isEqual(origin.getUserName(), updated.getUserName())) {
			sb.append("장비담당자: \"").append(updated.getUserName()).append("\" || ");
		}
		if (!isEqual(origin.getUsagePurpose(), updated.getUsagePurpose())) {
			sb.append("사용용도: \"").append(updated.getUsagePurpose()).append("\" || ");
		}
		if (!isEqual(origin.getArchiveLocation(), updated.getArchiveLocation())) {
			sb.append("사용/보관 위치: \"").append(updated.getArchiveLocation()).append("\" || ");
		}
		if (!isEqual(origin.getUsageDivisionCode(), updated.getUsageDivisionCode()) || !isEqual(origin.getUsageDivision(), updated.getUsageDivision())) {
			sb.append("용도구분: \"").append(updated.getUsageDivision()).append("\" || ");
		}
		if (!isEqual(origin.getOldDeviceId(), updated.getOldDeviceId())) {
			sb.append("기존 장비관리번호: \"").append(updated.getOldDeviceId()).append("\" || ");
		}
		if (!isEqual(origin.getManufacturer(), updated.getManufacturer())) {
			sb.append("제조사: \"").append(updated.getManufacturer()).append("\" || ");
		}
		if (!isEqual(origin.getModelName(), updated.getModelName())) {
			sb.append("모델명: \"").append(updated.getModelName()).append("\" || ");
		}
		if (!isEqual(origin.getManufactureDate(), updated.getManufactureDate())) {
			sb.append("제조년도: \"").append(updated.getManufactureDate()).append("\" || ");
		}
		if (!isEqual(origin.getCpuSpec(), updated.getCpuSpec())) {
			sb.append("CPU: \"").append(updated.getCpuSpec()).append("\" || ");
		}
		if (!isEqual(origin.getMemorySize(), updated.getMemorySize())) {
			sb.append("메모리: \"").append(updated.getMemorySize()).append("\" || ");
		}
		if (!isEqual(origin.getStorageInfo(), updated.getStorageInfo())) {
			sb.append("SSD/HDD: \"").append(updated.getStorageInfo()).append("\" || ");
		}
		if (!isEqual(origin.getOperatingSystem(), updated.getOperatingSystem())) {
			sb.append("OS: \"").append(updated.getOperatingSystem()).append("\" || ");
		}
		if (!isEqual(origin.getScreenSize(), updated.getScreenSize())) {
			sb.append("인치: \"").append(updated.getScreenSize()).append("\" || ");
		}
		if (!isEqual(origin.getGpuSpec(), updated.getGpuSpec())) {
			sb.append("GPU: \"").append(updated.getGpuSpec()).append("\" || ");
		}
		if (!isEqual(origin.getPurchaseDate(), updated.getPurchaseDate())) {
			sb.append("구매일자: \"").append(updated.getPurchaseDate()).append("\" || ");
		}
		if (!isEqual(origin.getReturnDate(), updated.getReturnDate())) {
			sb.append("반납일자: \"").append(updated.getReturnDate()).append("\" || ");
		}
		if (!isEqual(origin.getRemarks(), updated.getRemarks())) {
			sb.append("비고: \"").append(updated.getRemarks()).append("\" || ");
		}
		String summary = sb.toString();
		if (summary.endsWith(" || ")) {
			summary = summary.substring(0, summary.length() - 4);
		}
		history.setChangeContents(summary);
		history.setDeviceStatus(updated.getDeviceStatusCode());
		history.setApprovalStatus(origin.getApprovalStatusCode());
		history.setReason(updated.getChangeReason());

		return history;
	}

	/**
	 * 장비 타입 별 엑셀 시트 세팅
	 */
	private void setDeviceSheet(Sheet sheet, List<DeviceVO> deviceList, String deviceType) {
		if (sheet == null) return;
		int rowNum = 2;
		for (DeviceVO d : deviceList) {
			if (!deviceType.equals(d.getDeviceTypeCode())) continue;
			Row row = sheet.createRow(rowNum++);
			int listIndex = rowNum - 2;

			if (CodeConstants.DEVICE_TYPE_COMPUTER.equals(deviceType)) {
				// "PC" 시트
				row.createCell(0).setCellValue(listIndex); // 구분(순번)
				row.createCell(1).setCellValue(d.getDeviceType());
				row.createCell(2).setCellValue(d.getOrgName());
				row.createCell(3).setCellValue(d.getUserName());
				row.createCell(4).setCellValue(d.getUsageDivision());
				row.createCell(5).setCellValue(d.getUsagePurpose());
				row.createCell(6).setCellValue(d.getArchiveLocation());
				row.createCell(7).setCellValue(d.getOldDeviceId());
				row.createCell(8).setCellValue(d.getManufacturer());
				row.createCell(9).setCellValue(d.getModelName());
				row.createCell(10).setCellValue(d.getManufactureDate());
				row.createCell(11).setCellValue(d.getCpuSpec());
				row.createCell(12).setCellValue(d.getMemorySize());
				row.createCell(13).setCellValue(d.getStorageInfo());
				row.createCell(14).setCellValue(d.getOperatingSystem());
				row.createCell(15).setCellValue(d.getScreenSize());
				row.createCell(16).setCellValue(d.getGpuSpec());
				row.createCell(17).setCellValue(d.getPurchaseDate());
				row.createCell(18).setCellValue(d.getReturnDate());
				row.createCell(19).setCellValue(d.getDeviceStatus());
				row.createCell(20).setCellValue(d.getRemarks());
			} else if (CodeConstants.DEVICE_TYPE_MONITOR.equals(deviceType)) {
				// "모니터" 시트
				row.createCell(0).setCellValue(listIndex);
				row.createCell(1).setCellValue(d.getDeviceType());
				row.createCell(2).setCellValue(d.getOrgName());
				row.createCell(3).setCellValue(d.getUserName());
				row.createCell(4).setCellValue(d.getUsageDivision());
				row.createCell(5).setCellValue(d.getUsagePurpose());
				row.createCell(6).setCellValue(d.getArchiveLocation());
				row.createCell(7).setCellValue(d.getOldDeviceId());
				row.createCell(8).setCellValue(d.getManufacturer());
				row.createCell(9).setCellValue(d.getModelName());
				row.createCell(10).setCellValue(d.getManufactureDate());
				row.createCell(11).setCellValue(d.getScreenSize());
				row.createCell(12).setCellValue(d.getPurchaseDate());
				row.createCell(13).setCellValue(d.getReturnDate());
				row.createCell(14).setCellValue(d.getDeviceStatus());
				row.createCell(15).setCellValue(d.getRemarks());
			} else if (CodeConstants.DEVICE_TYPE_PHONE.equals(deviceType)) {
				// "핸드폰" 시트
				row.createCell(0).setCellValue(listIndex);
				row.createCell(1).setCellValue(d.getDeviceType());
				row.createCell(2).setCellValue(d.getOrgName());
				row.createCell(3).setCellValue(d.getUserName());
				row.createCell(4).setCellValue(d.getUsageDivision());
				row.createCell(5).setCellValue(d.getUsagePurpose());
				row.createCell(6).setCellValue(d.getArchiveLocation());
				row.createCell(7).setCellValue(d.getOldDeviceId());
				row.createCell(8).setCellValue(d.getManufacturer());
				row.createCell(9).setCellValue(d.getModelName());
				row.createCell(10).setCellValue(d.getManufactureDate());
				row.createCell(11).setCellValue(d.getOperatingSystem());
				row.createCell(12).setCellValue(d.getPurchaseDate());
				row.createCell(13).setCellValue(d.getReturnDate());
				row.createCell(14).setCellValue(d.getDeviceStatus());
				row.createCell(15).setCellValue(d.getRemarks());
			} else if (CodeConstants.DEVICE_TYPE_ETC.equals(deviceType)) {
				// "기타" 시트
				row.createCell(0).setCellValue(listIndex);
				row.createCell(1).setCellValue(d.getDeviceType());
				row.createCell(2).setCellValue(d.getOrgName());
				row.createCell(3).setCellValue(d.getUserName());
				row.createCell(4).setCellValue(d.getUsageDivision());
				row.createCell(5).setCellValue(d.getUsagePurpose());
				row.createCell(6).setCellValue(d.getArchiveLocation());
				row.createCell(7).setCellValue(d.getOldDeviceId());
				row.createCell(8).setCellValue(d.getManufacturer());
				row.createCell(9).setCellValue(d.getModelName());
				row.createCell(10).setCellValue(d.getManufactureDate());
				row.createCell(11).setCellValue(d.getPurchaseDate());
				row.createCell(12).setCellValue(d.getReturnDate());
				row.createCell(13).setCellValue(d.getDeviceStatus());
				row.createCell(14).setCellValue(d.getRemarks());
			}
		}
	}

	/**
     * 전산 장비 엑셀 업로드
     * @param excelFile
     * @return
     * @throws IOException
     * @throws EncryptedDocumentException
     */
	public Header<DeviceVO> uploadDeviceExcel(MultipartFile file) throws Exception {
		log.info("upload excelFilename : {}", file.getOriginalFilename());
		UserInfoVO userInfo = AuthUtil.getLoginUserInfo();

		// 파일 확장자 검증
		String filename = file.getOriginalFilename();
        if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
            return Header.ERROR("400", "Excel 파일만 업로드 가능합니다.");
        }

        // 전체 사용자 목록 조회
        List<UserVO> userList = userMapper.getAllUserList();
        // 코드 목록 조회
        List<CodeVO> codeList = codeMapper.getCodeListByCodes(Arrays.asList(
        		CodeConstants.CATEGORY_DEVICE_TYPE,
        		CodeConstants.CATEGORY_DEVICE_STATUS,
        		CodeConstants.CATEGORY_USAGE_DIVISION));
        // 장비 유형 코드 목록
        List<CodeVO> deviceTypeList = codeList.stream()
        		.filter(code -> code.getCategory().equals(CodeConstants.CATEGORY_DEVICE_TYPE))
        		.collect(Collectors.toList());
        // 장비 상태 코드 목록
        List<CodeVO> deviceStatusList = codeList.stream()
        		.filter(code -> code.getCategory().equals(CodeConstants.CATEGORY_DEVICE_STATUS))
        		.collect(Collectors.toList());
        // 사용 용도 코드 목록
        List<CodeVO> usageDivisionList = codeList.stream()
        		.filter(code -> code.getCategory().equals(CodeConstants.CATEGORY_USAGE_DIVISION))
        		.collect(Collectors.toList());

        // Excel 파일 처리
        int startRow = 2;
        Workbook workbook = null;
        DataFormatter formatter = new DataFormatter();
		try {
			workbook = WorkbookFactory.create(file.getInputStream());

			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);

				List<DeviceVO> deviceList = new ArrayList<DeviceVO>();
				for (Row row : sheet) {
					if (row.getRowNum() < startRow) continue;

					DeviceVO deviceVO = new DeviceVO();

		        	String userName = formatter.formatCellValue(row.getCell(2));			// 사용자
		        	deviceVO.setEmpNum(userList.stream()
		            		.filter(user -> user.getUserName().equals(userName))
		            		.map(user -> user.getEmpNum())
		            		.findFirst()
		            		.orElse(null));
		        	String usageDivision = formatter.formatCellValue(row.getCell(3));					// 용도구분
		        	deviceVO.setUsageDivisionCode(usageDivisionList.stream()
		            		.filter(code -> code.getCodeName().equals(usageDivision))
		            		.map(code -> code.getCode())
		            		.findFirst()
		            		.orElse(null));
		        	deviceVO.setUsagePurpose(formatter.formatCellValue(row.getCell(4)));			// 사용용도
		        	deviceVO.setArchiveLocation(formatter.formatCellValue(row.getCell(5)));		// 보관위치
		        	deviceVO.setOldDeviceId(formatter.formatCellValue(row.getCell(6)));			// 관리번호
		        	String deviceType = formatter.formatCellValue(row.getCell(7));						// 장비유형
		        	deviceVO.setDeviceTypeCode(deviceTypeList.stream()
		            		.filter(code -> code.getCodeName().equals(deviceType))
		            		.map(code -> code.getCode())
		            		.findFirst()
		            		.orElse(CodeConstants.DEVICE_TYPE_ETC));
		        	deviceVO.setManufacturer(formatter.formatCellValue(row.getCell(8)));			// 제조사
		        	deviceVO.setModelName(formatter.formatCellValue(row.getCell(9)));				// 모델명
		        	deviceVO.setManufactureDate(formatter.formatCellValue(row.getCell(10)));	// 제조일
		        	deviceVO.setCpuSpec(formatter.formatCellValue(row.getCell(11)));				// CPU
		        	deviceVO.setMemorySize(formatter.formatCellValue(row.getCell(12)));			// 메모리
		        	deviceVO.setStorageInfo(formatter.formatCellValue(row.getCell(13)));			// 스토리지 정보
		        	deviceVO.setOperatingSystem(formatter.formatCellValue(row.getCell(14)));	// OS
		        	deviceVO.setScreenSize(formatter.formatCellValue(row.getCell(15)));			// 화면크기
		        	deviceVO.setGpuSpec(formatter.formatCellValue(row.getCell(16)));				// GPU
		        	deviceVO.setPurchaseDate(formatter.formatCellValue(row.getCell(17)));		// 구매일자
		        	deviceVO.setReturnDate(formatter.formatCellValue(row.getCell(18)));			// 반납일자
		        	String deviceStatus = formatter.formatCellValue(row.getCell(19));				// 장비상태
		        	deviceVO.setDeviceStatusCode(deviceStatusList.stream()
		            		.filter(code -> code.getCodeName().equals(deviceStatus))
		            		.map(code -> code.getCode())
		            		.findFirst()
		            		.orElse(null));
		        	deviceVO.setRemarks(formatter.formatCellValue(row.getCell(20)));							// 비고
		        	deviceVO.setCreateUser(userInfo.getEmpNum());

		        	deviceList.add(deviceVO);
		        	log.info("##### Row {} : {}", i, deviceVO.toString());
		        }

		        // DB 저장
				if(deviceList.size() > 0) {
					deviceMapper.insertDeviceList(deviceList);
				}
			}

		} catch (Exception e) {
			log.error("excel upload error : ", e);
			return Header.ERROR("500", "ERROR");
		}finally {
			if(workbook != null) {
				workbook.close();
			}
		}

		return Header.OK();
	}

	/* 권한에 따른 조건 추가 */
	public boolean setParamsByUserRole(UserInfoVO userInfo, HashMap<String, Object> paramMap) {
		paramMap.put("roleCode", userInfo.getRoleCode());
		switch (userInfo.getRoleCode()) {
			case CodeConstants.ROLE_USER:
				paramMap.put("loginEmpNum", userInfo.getEmpNum());
				break;
			case CodeConstants.ROLE_TEAM_MANAGER:
				paramMap.put("loginOrgId", userInfo.getOrgId());
				break;
			case CodeConstants.ROLE_ASSET_MANAGER:
			case CodeConstants.ROLE_SYSTEM_MANAGER:
				break;
			default:
				return true;
		}
		return false;
	}
}
