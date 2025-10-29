package com.gaea.asset.manager.activity.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gaea.asset.manager.activity.vo.ActivityVO;
import com.gaea.asset.manager.device.service.DeviceMapper;
import com.gaea.asset.manager.device.vo.DeviceHistoryVO;
import com.gaea.asset.manager.login.vo.UserInfoVO;
import com.gaea.asset.manager.util.AuthUtil;
import com.gaea.asset.manager.util.Header;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityMapper activityMapper;
    private final DeviceMapper deviceMapper;

    public Header<ActivityVO> getActivityInfo() {
        UserInfoVO userInfo = AuthUtil.getLoginUserInfo();
        
        // 최근 활동 가져오기
        List<ActivityVO.RecentActivityItem> recentActivities = getRecentActivities();
        
        // 대기중인 승인 건수
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("roleCode", userInfo.getRoleCode());
        paramMap.put("empNum", userInfo.getEmpNum());
        int pendingApprovalCount = activityMapper.getPendingApprovalCount(paramMap);
        
        // 읽지 않은 메시지 건수
        int unreadMessageCount = activityMapper.getUnreadMessageCount(userInfo.getUserId());
        
        // 전체 장비 수
        int totalDeviceCount = activityMapper.getTotalDeviceCount();
        
        // 사용 중인 장비 수
        int activeDeviceCount = activityMapper.getActiveDeviceCount();
        
        // 내 장비 수
        int myDeviceCount = activityMapper.getMyDeviceCount(userInfo.getEmpNum());
        
        // 마지막 업데이트 시간
        String lastUpdateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        ActivityVO activityVO = ActivityVO.builder()
                .recentActivities(recentActivities)
                .pendingApprovalCount(pendingApprovalCount)
                .unreadMessageCount(unreadMessageCount)
                .totalDeviceCount(totalDeviceCount)
                .activeDeviceCount(activeDeviceCount)
                .myDeviceCount(myDeviceCount)
                .lastUpdateTime(lastUpdateTime)
                .build();
        
        return Header.OK(activityVO);
    }
    
    private List<ActivityVO.RecentActivityItem> getRecentActivities() {
        List<ActivityVO.RecentActivityItem> activities = new ArrayList<>();
        
        // 최근 장비 이력 가져오기 (최대 5개)
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("page", 0);
        paramMap.put("size", 5);
        
        List<DeviceHistoryVO> histories = deviceMapper.getDeviceHistoryList(paramMap);
        
        for (DeviceHistoryVO history : histories) {
            String activityType = getActivityType(history.getApprovalStatusCode());
            String description = getActivityDescription(history);
            
            ActivityVO.RecentActivityItem item = ActivityVO.RecentActivityItem.builder()
                    .activityType(activityType)
                    .deviceNum(history.getDeviceNum())
                    .deviceType(history.getDeviceType())
                    .userName(history.getUserName())
                    .description(description)
                    .createDatetime(history.getCreateDatetime())
                    .build();
            
            activities.add(item);
        }
        
        return activities;
    }
    
    private String getActivityType(String approvalStatusCode) {
        if (approvalStatusCode == null) return "DEVICE_UPDATED";
        
        switch (approvalStatusCode) {
            case "A1":
                return "APPROVAL_PENDING_TEAM";
            case "A2":
                return "APPROVAL_PENDING_MANAGER";
            case "A3":
                return "APPROVAL_APPROVED";
            case "A4":
                return "APPROVAL_REJECTED";
            default:
                return "DEVICE_UPDATED";
        }
    }
    
    private String getActivityDescription(DeviceHistoryVO history) {
        String deviceType = history.getDeviceType() != null ? history.getDeviceType() : "장비";
        String statusCode = history.getApprovalStatusCode();
        
        if (statusCode == null) {
            return deviceType + " 상태가 변경되었습니다";
        }
        
        switch (statusCode) {
            case "A1":
                return deviceType + " 부서장 승인 대기 중입니다";
            case "A2":
                return deviceType + " 관리자 승인 대기 중입니다";
            case "A3":
                return deviceType + " 승인이 완료되었습니다";
            case "A4":
                return deviceType + " 승인이 반려되었습니다";
            default:
                return deviceType + " 상태가 변경되었습니다";
        }
    }
}
