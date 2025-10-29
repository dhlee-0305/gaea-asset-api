package com.gaea.asset.manager.activity.service;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ActivityMapper {
    // 통계 관련 메서드
    int getTotalDeviceCount();
    
    int getActiveDeviceCount();
    
    int getMyDeviceCount(Integer empNum);
    
    int getPendingApprovalCount(HashMap<String, Object> paramMap);
    
    int getUnreadMessageCount(String userId);
}
