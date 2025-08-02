package com.gaea.asset.manager.device.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceHistoryVO {
    private Integer historyNum;
    private Integer deviceNum;
    private Integer empNum;
    private String userName;
    private String deviceStatus; // deviceStatus 추가
    private String approvalStatus; // approvalStatus 추가
    private String changeContents;
    private String reason;
    private String createDatetime;
    private Integer createUser;
    private String deviceType;
}