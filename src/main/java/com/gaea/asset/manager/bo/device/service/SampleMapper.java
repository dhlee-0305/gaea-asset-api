package com.gaea.asset.manager.bo.device.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gaea.asset.manager.bo.device.vo.SampleVO;

@Mapper
public interface SampleMapper {
	List<SampleVO> getDeviceList(HashMap<String, Object> paramMap);

	int getDeviceTotalCount(HashMap<String, Object> paramMap);

	SampleVO getDeviceInfo(Long idx);

	int insertDevice(SampleVO entity);

	int updateDevice(SampleVO entity);

	int deleteDevice(Long idx);
}
