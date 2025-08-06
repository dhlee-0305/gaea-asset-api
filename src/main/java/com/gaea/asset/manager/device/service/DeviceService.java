package com.gaea.asset.manager.device.service;

import java.util.HashMap;
import java.util.List;

import com.gaea.asset.manager.common.constants.Constants;
import com.gaea.asset.manager.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.common.constants.CodeConstants;
import com.gaea.asset.manager.device.vo.DeviceHistoryVO;
import com.gaea.asset.manager.device.vo.DeviceVO;
import com.gaea.asset.manager.login.vo.UserInfoVO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.gaea.asset.manager.util.DeviceFieldUtil.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {
	private final DeviceMapper deviceMapper;

	/**
	 * 전상 장비 목록 조회
	 * @param currentPage
	 * @param pageSize
	 * @param search
	 * @return
	 */
	public Header<List<DeviceVO>> getDeviceList(int currentPage, int pageSize, Search search) {
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
                return Header.ERROR("403", "조회 권한이 없습니다.");
		}

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
                return Header.ERROR("403", "조회 권한이 없습니다.");
		}

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
			insertDeviceHistory(deviceVO, null, userInfo.getEmpNum(), Constants.UPDATE);
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
                originDevice.setApprovalStatusCode(CodeConstants.APPROVAL_STATUS_TEAM_MANAGER_PENDING);
                if (deviceMapper.insertDeviceTemp(deviceVO) > 0) {
                    deviceMapper.updateApprovalStatusCode(originDevice);
					insertDeviceHistory(originDevice, deviceVO, userInfo.getEmpNum(), Constants.UPDATE);
                    return Header.OK();
                }
                break;
            case CodeConstants.ROLE_TEAM_MANAGER: // 부서장
                originDevice.setApprovalStatusCode(CodeConstants.APPROVAL_STATUS_ADMIN_PENDING);
                if (deviceMapper.insertDeviceTemp(deviceVO) > 0) {
                    deviceMapper.updateApprovalStatusCode(originDevice);
					insertDeviceHistory(originDevice, deviceVO, userInfo.getEmpNum(), Constants.UPDATE);
                    return Header.OK();
                }
                break;
            case CodeConstants.ROLE_ASSET_MANAGER:
            case CodeConstants.ROLE_SYSTEM_MANAGER: // 관리자/시스템 관리자
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
                return Header.OK();
            }
        }

		insertDeviceHistory(originDevice, deviceVO, userInfo.getEmpNum(), Constants.APPROVE);

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
        if (deviceMapper.updateApprovalStatusCode(originDevice) > 0) {
            deviceMapper.deleteDeviceTemp(deviceVO.getDeviceNum());
			insertDeviceHistory(originDevice, null, userInfo.getEmpNum(), Constants.REJECT);
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
		HashMap<String, Object> paramMap = new HashMap<>();

		// 페이징
		paramMap.put("page", (currentPage - 1) * pageSize);
		paramMap.put("size", pageSize);

		// 검색
		paramMap.put("searchColumn", search.getSearchColumn());
		paramMap.put("searchKeyword", search.getSearchKeyword());

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
	 * 전산 장비 이력 상세 조회
	 * @param historyNum
	 * @return
	 */
	public Header<DeviceHistoryVO> getDeviceHistory(Integer historyNum) {
		return Header.OK(deviceMapper.getDeviceHistory(historyNum));
	}

	/**
	 * Insert device history with type (REGISTER, UPDATE, APPROVE, REJECT)
	 * @param origin
	 * @param updated
	 * @param empNum
	 * @param type
	 */
	public void insertDeviceHistory(DeviceVO origin, DeviceVO updated, int empNum, String type) {
		DeviceHistoryVO history = new DeviceHistoryVO();
		history.setDeviceNum(origin.getDeviceNum());
		history.setCreateUser(empNum);
		history.setEmpNum(origin.getEmpNum()); // 최종 승인 시 변경된 장비 담당자 반영

		switch (type) {
			case Constants.REGISTER: // 등록 장비 정보 요약
				setRegisterHistory(history, origin);
				break;
			case Constants.UPDATE: // 변경 사항 요약
				setUpdateHistory(history, origin, updated);
				break;
			case Constants.APPROVE: // 장비/결재 상태 저장
				history.setDeviceStatus(updated.getDeviceStatusCode());
				history.setApprovalStatus(CodeConstants.APPROVAL_STATUS_APPROVED);
				break;
			case Constants.REJECT: // 장비/결재 상태 저장
				history.setDeviceStatus(origin.getDeviceStatusCode());
				history.setApprovalStatus(CodeConstants.APPROVAL_STATUS_REJECTED);
				break;
		}
		deviceMapper.insertDeviceHistory(history);
	}

	private void setRegisterHistory(DeviceHistoryVO history, DeviceVO origin) {
		StringBuilder sb = new StringBuilder();
		appendIfPresent(sb, "장비담당자", origin.getUserName());
		appendIfPresent(sb, "모델명", origin.getModelName());
		appendIfPresent(sb, "용도구분", origin.getUsageDivision());
		appendIfPresent(sb, "제조사", origin.getManufacturerCode());
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
	}

	private void setUpdateHistory(DeviceHistoryVO history, DeviceVO origin, DeviceVO updated) {
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
		if (!isEqual(origin.getManufacturerCode(), updated.getManufacturerCode())) {
			sb.append("제조사: \"").append(updated.getManufacturerCode()).append("\" || ");
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
	}
}
