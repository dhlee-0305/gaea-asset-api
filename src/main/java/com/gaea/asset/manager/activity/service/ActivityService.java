package com.gaea.asset.manager.activity.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gaea.asset.manager.activity.vo.ActivityVO;
import com.gaea.asset.manager.common.constants.CommonCode;
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
        
        // 역할 기반 필터 파라미터 생성
        HashMap<String, Object> paramMap = getRoleFilterParams(userInfo);
        
        // 최근 활동 가져오기
        List<ActivityVO.RecentActivityItem> recentActivities = getRecentActivities(paramMap);
        
        // 대기중인 승인 건수
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
    
    private List<ActivityVO.RecentActivityItem> getRecentActivities(HashMap<String, Object> paramMap) {
        List<ActivityVO.RecentActivityItem> activities = new ArrayList<>();
        
        // 최근 장비 이력 가져오기 (최대 5개) - 역할 기반 필터 적용
        HashMap<String, Object> historyParams = new HashMap<>(paramMap);
        historyParams.put("page", 0);
        historyParams.put("size", 5);
        
        List<DeviceHistoryVO> histories = deviceMapper.getDeviceHistoryList(historyParams);
        
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
    
    /**
     * 사용자 역할에 따른 필터 파라미터 생성
     * DeviceService.getRoleFilterParams()와 동일한 로직 사용
     * (최소한의 변경을 위해 복제, 향후 공통 유틸리티로 리팩토링 권장)
     * 
     * @param userInfo 사용자 정보
     * @return 필터 파라미터 맵
     */
    private HashMap<String, Object> getRoleFilterParams(UserInfoVO userInfo) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("roleCode", userInfo.getRoleCode());
        paramMap.put("empNum", userInfo.getEmpNum());
        
        switch (userInfo.getRoleCode()) {
            case CommonCode.ROLE_USER:
                paramMap.put("loginEmpNum", userInfo.getEmpNum());
                break;
            case CommonCode.ROLE_TEAM_MANAGER:
                paramMap.put("loginOrgId", userInfo.getOrgId());
                break;
            case CommonCode.ROLE_ASSET_MANAGER:
            case CommonCode.ROLE_SYSTEM_MANAGER:
                // 관리자는 모든 데이터 접근 가능
                break;
        }
        return paramMap;
    }
}
