package com.gaea.asset.manager.device.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gaea.asset.manager.device.vo.DeviceVO;

@Mapper
public interface DeviceMapper {
	List<DeviceVO> getDeviceList(HashMap<String, Object> paramMap);

	int getDeviceTotalCount(HashMap<String, Object> paramMap);

	DeviceVO getDevice(Integer deviceNum);

	int insertDevice(DeviceVO entity);

	int updateDevice(DeviceVO entity);

	int deleteDevice(Integer deviceNum);
}
