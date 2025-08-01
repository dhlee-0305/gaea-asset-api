package com.gaea.asset.manager.device.service;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.common.constants.CodeConstants;
import com.gaea.asset.manager.device.vo.DeviceHistoryVO;
import com.gaea.asset.manager.device.vo.DeviceVO;
import com.gaea.asset.manager.login.vo.UserInfoVO;
import com.gaea.asset.manager.util.AuthUtil;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Pagination;
import com.gaea.asset.manager.util.Search;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
                    return Header.OK();
                }
                break;
            case CodeConstants.ROLE_TEAM_MANAGER: // 부서장
                originDevice.setApprovalStatusCode(CodeConstants.APPROVAL_STATUS_ADMIN_PENDING);
                if (deviceMapper.insertDeviceTemp(deviceVO) > 0) {
                    deviceMapper.updateApprovalStatusCode(originDevice);
                    return Header.OK();
                }
                break;
            case CodeConstants.ROLE_ASSET_MANAGER:
            case CodeConstants.ROLE_SYSTEM_MANAGER: // 관리자/시스템 관리자
                if (deviceMapper.updateDevice(deviceVO) > 0) {
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
	 * @param userRoleCode
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
                return Header.OK();
            }
        }

        return Header.OK();
    }

    /**
     * 전산 장비 반려
     * @param deviceVO
     * @param userRoleCode
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

        originDevice.setApprovalStatusCode(CodeConstants.APPROVAL_STATUS_REJECTED);
        if (deviceMapper.updateApprovalStatusCode(originDevice) > 0) {
            deviceMapper.deleteDeviceTemp(deviceVO.getDeviceNum());
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
}
