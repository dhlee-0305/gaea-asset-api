package com.gaea.asset.manager.device.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gaea.asset.manager.device.vo.DeviceVO;
import com.gaea.asset.manager.user.vo.UserVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Pagination;
import com.gaea.asset.manager.util.Search;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceService {
	private final DeviceMapper deviceMapper;

	public Header<List<DeviceVO>> getDeviceList(int page, int size, Search search) {
		HashMap<String, Object> paramMap = new HashMap<>();

		paramMap.put("page", (page - 1) * size);
		paramMap.put("size", size);
		paramMap.put("searchKey", search.getSearchKey());
		paramMap.put("searchValue", search.getSearchValue());

		List<DeviceVO> deviceList = deviceMapper.getDeviceList(paramMap);
		Pagination pagination = new Pagination(
				deviceMapper.getDeviceTotalCount(paramMap),
				page,
				size,
				10
		);

		return Header.OK(deviceList, pagination);
	}

	public Header<DeviceVO> getDevice(Integer deviceNum) {
		return Header.OK(deviceMapper.getDevice(deviceNum));
	}

	public Header<DeviceVO> insertDevice(DeviceVO DeviceVO) {
		if (deviceMapper.insertDevice(DeviceVO) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}

	public Header<DeviceVO> updateDevice(DeviceVO DeviceVO) {
		if (deviceMapper.updateDevice(DeviceVO) > 0) {
			return Header.OK(DeviceVO);
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}

	public Header<String> deleteDevice(Integer deviceNum) {
		if (deviceMapper.deleteDevice(deviceNum) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}
}
