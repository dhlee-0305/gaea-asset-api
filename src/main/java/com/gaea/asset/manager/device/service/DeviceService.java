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
	public Header<DeviceVO> insertDeviceTemp(DeviceVO deviceVO) {
		DeviceVO device = deviceMapper.getDevice(deviceVO.getDeviceNum());

		// 결재 대기 상태(A1, A2)면 요청 불가
		if ("A1".equals(device.getApprovalStatusCode()) || "A2".equals(device.getApprovalStatusCode())) {
			return Header.ERROR("9999", "ERROR");
		}

		// 결재 상태 업데이트
		deviceVO.setApprovalStatusCode("A1");

		if (deviceMapper.updateApprovalStatusCode(deviceVO) > 0) {
			if (deviceMapper.insertDeviceTemp(deviceVO) > 0) {
				return Header.OK(deviceVO);
			}
		} else {
			return Header.ERROR("9999", "ERROR");
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
	public Header<DeviceVO> processApproval(DeviceVO deviceVO) {
		String status = deviceVO.getApprovalStatusCode();

		if ("A3".equals(status)) { // 승인 처리
			if (deviceMapper.updateDevice(deviceVO) > 0) {
				deviceMapper.deleteDeviceTemp(deviceVO.getDeviceNum());
				return Header.OK(deviceVO);
			}
		} else if ("A4".equals(status)) { // 반려 처리
			if (deviceMapper.updateApprovalStatusCode(deviceVO) > 0) {
				deviceMapper.deleteDeviceTemp(deviceVO.getDeviceNum());
				return Header.OK(deviceVO);
			}
		}

		return Header.ERROR("9999", "ERROR");
	}
}
