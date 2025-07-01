package com.gaea.asset.manager.fo.device.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gaea.asset.manager.fo.device.vo.DeviceVO;

@Mapper
public interface DeviceMapper {
	List<DeviceVO> getDeviceList(HashMap<String, Object> paramMap);

	int getDeviceTotalCount(HashMap<String, Object> paramMap);

	DeviceVO getDeviceInfo(Long idx);

	int insertDevice(DeviceVO entity);

	int updateDevice(DeviceVO entity);

	int deleteDevice(Long idx);
}
