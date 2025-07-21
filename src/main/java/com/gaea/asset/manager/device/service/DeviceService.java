package com.gaea.asset.manager.device.service;

import java.util.HashMap;
import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.device.vo.DeviceVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Pagination;
import com.gaea.asset.manager.util.Search;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceService {
	private final DeviceMapper deviceMapper;

	public Header<List<DeviceVO>> getDeviceList(int currentPage, int pageSize, Search search) {
		HashMap<String, Object> paramMap = new HashMap<>();

		paramMap.put("page", (currentPage - 1) * pageSize);
		paramMap.put("size", pageSize);
		paramMap.put("searchColumn", search.getSearchColumn());
		paramMap.put("searchKeyword", search.getSearchKeyword());

		List<DeviceVO> deviceList = deviceMapper.getDeviceList(paramMap);
		Pagination pagination = new Pagination(
				deviceMapper.getDeviceTotalCount(paramMap),
				currentPage,
				pageSize,
				10
		);

		return Header.OK(deviceList, pagination);
	}

	public Header<DeviceVO> getDevice(Integer deviceNum) {
		return Header.OK(deviceMapper.getDevice(deviceNum));
	}

	public Header<DeviceVO> getDeviceTemp(Integer deviceNum) {
		return Header.OK(deviceMapper.getDeviceTemp(deviceNum));
	}

	public Header<DeviceVO> insertDevice(DeviceVO deviceVO) {
		if (deviceMapper.insertDevice(deviceVO) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}

    @Transactional
    public Header<DeviceVO> updateDevice(DeviceVO deviceVO, String userRoleCode) {
        DeviceVO originDevice = deviceMapper.getDevice(deviceVO.getDeviceNum());
        String currentStatus = originDevice.getApprovalStatusCode();

        // 결재 대기 상태(A1, A2)면 수정 불가
        if ("A1".equals(currentStatus) || "A2".equals(currentStatus)) {
            return Header.ERROR("9999", "승인 대기 중인 장비는 수정할 수 없습니다.");
        }

        switch (userRoleCode) {
            case "00": // 일반 사용자
                originDevice.setApprovalStatusCode("A1");
                if (deviceMapper.insertDeviceTemp(deviceVO) > 0) {
                    deviceMapper.updateApprovalStatusCode(originDevice);
                    return Header.OK();
                }
                break;
            case "01": // 부서장
                originDevice.setApprovalStatusCode("A2");
                if (deviceMapper.insertDeviceTemp(deviceVO) > 0) {
                    deviceMapper.updateApprovalStatusCode(originDevice);
                    return Header.OK();
                }
                break;
            case "02":
            case "03": // 관리자/시스템 관리자
                deviceVO.setApprovalStatusCode("A3");
                if (deviceMapper.updateDevice(deviceVO) > 0) {
                    return Header.OK();
                }
                break;
            default:
                return Header.ERROR("3000", "수정 권한이 없습니다.");
        }
        return Header.ERROR("9999", "ERROR");
    }

	public Header<String> deleteDevice(Integer deviceNum) {
		if (deviceMapper.deleteDevice(deviceNum) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}

    @Transactional
    public Header<DeviceVO> approveDeviceUpdate(DeviceVO deviceVO, String userRoleCode) {
        DeviceVO originDevice = deviceMapper.getDevice(deviceVO.getDeviceNum());
        String currentStatus = originDevice.getApprovalStatusCode();
        String nextStatus;

        // 승인 권한 확인 및 다음 결재 상태 세팅
        switch (userRoleCode) {
            case "01": // 부서장
                if (!"A1".equals(currentStatus)) {
                    return Header.ERROR("3001", "승인 권한이 없습니다.");
                }
                nextStatus = "A2";
                break;
            case "02": // 관리자
                if (!"A2".equals(currentStatus)) {
                    return Header.ERROR("3001", "승인 권한이 없습니다.");
                }
                nextStatus = "A3";
                break;
            case "03": // 시스템 관리자
                nextStatus = "A3";
                break;
            default:
                return Header.ERROR("3001", "승인 권한이 없습니다.");
        }

        // 결재 상태 업데이트
        originDevice.setApprovalStatusCode(nextStatus);
        deviceMapper.updateApprovalStatusCode(originDevice);

        // 최종 승인일 때만 device 정보 업데이트 + temp 삭제
        if ("A3".equals(nextStatus)) {
            deviceVO.setApprovalStatusCode(nextStatus);
            if (deviceMapper.updateDevice(deviceVO) > 0) {
                deviceMapper.deleteDeviceTemp(deviceVO.getDeviceNum());
                return Header.OK();
            }
        }

        return Header.OK();
    }

    @Transactional
    public Header<DeviceVO> rejectDeviceUpdate(DeviceVO deviceVO, String userRoleCode) {
        DeviceVO originDevice = deviceMapper.getDevice(deviceVO.getDeviceNum());
        String currentStatus = originDevice.getApprovalStatusCode();

        // 권한 확인
        if (userRoleCode == null || "00".equals(userRoleCode)) {
            return Header.ERROR("3002", "반려 권한이 없습니다.");
        }
        if ("01".equals(userRoleCode) && !"A1".equals(currentStatus)) {
            return Header.ERROR("3002", "반려 권한이 없습니다.");
        }
        if ("02".equals(userRoleCode) && !"A2".equals(currentStatus)) {
            return Header.ERROR("3002", "반려 권한이 없습니다.");
        }

        originDevice.setApprovalStatusCode("A4");
        if (deviceMapper.updateApprovalStatusCode(originDevice) > 0) {
            deviceMapper.deleteDeviceTemp(deviceVO.getDeviceNum());
            return Header.OK();
        }

		return Header.ERROR("9999", "ERROR");
	}
}
