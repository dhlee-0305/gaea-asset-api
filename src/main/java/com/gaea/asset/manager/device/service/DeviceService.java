package com.gaea.asset.manager.device.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.device.vo.DeviceVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Pagination;
import com.gaea.asset.manager.util.Search;

@Service
public class DeviceService {
	private final DeviceMapper deviceMapper;
	
	@Autowired
	public DeviceService(DeviceMapper deviceMapper) {
		this.deviceMapper = deviceMapper;
	}

	public Header<List<DeviceVO>> getDeviceList(int page, int size, Search search) {
		HashMap<String, Object> paramMap = new HashMap<>();

		if (page <= 1) {	//페이지가 1 이하로 입력되면 0으로 고정,
			paramMap.put("page", 0);
		} else {			//페이지가 2 이상
			paramMap.put("page", (page - 1) * size);
		}
		paramMap.put("size", size);
		paramMap.put("searchKey", search.getSearchKey());
		paramMap.put("searchValue", search.getSearchValue());

		List<DeviceVO> boardList = deviceMapper.getDeviceList(paramMap);
		Pagination pagination = new Pagination(
				deviceMapper.getDeviceTotalCount(paramMap),
				page,
				size,
				10
		);

		return Header.OK(boardList, pagination);
	}

	public Header<DeviceVO> getDeviceInfo(Long deviceNumber) {
		return Header.OK(deviceMapper.getDeviceInfo(deviceNumber));
	}

	public Header<DeviceVO> insertDevice(DeviceVO DeviceVO) {
		if (deviceMapper.insertDevice(DeviceVO) > 0) {
			return Header.OK(DeviceVO);
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

	public Header<String> deleteDevice(Long deviceNumber) {
		if (deviceMapper.deleteDevice(deviceNumber) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}
}
