package com.gaea.asset.manager.device.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gaea.asset.manager.device.vo.DeviceVO;
import com.gaea.asset.manager.device.vo.DeviceHistoryVO;

@Mapper
public interface DeviceMapper {
    // Device 관련 메서드
    List<DeviceVO> getDeviceList(HashMap<String, Object> paramMap);

    int getDeviceTotalCount(HashMap<String, Object> paramMap);

    DeviceVO getDevice(HashMap<String, Object> paramMap);

    int insertDevice(DeviceVO entity);

    int updateDevice(DeviceVO entity);

    int deleteDevice(Integer idx);

    DeviceVO getDeviceTemp(Integer deviceNum);

    int insertDeviceTemp(DeviceVO entity);

    void deleteDeviceTemp(Integer deviceNum);

    int updateApprovalStatusCode(DeviceVO entity);

    // DeviceHistory 관련 메서드
    List<DeviceHistoryVO> getDeviceHistoryList(HashMap<String, Object> paramMap);

    int getDeviceHistoryTotalCount(HashMap<String, Object> paramMap);

    List<DeviceHistoryVO> getDeviceHistory(Integer deviceNum);

    void insertDeviceHistory(DeviceHistoryVO deviceHistory);

    List<DeviceVO> getDeviceExcelList(HashMap<String, Object> paramMap);
    
    int insertDeviceList(List<DeviceVO> deviceList);

    List<DeviceVO> getDevicePendingList(HashMap<String, Object> paramMap);
}
