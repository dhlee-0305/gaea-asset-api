package com.gaea.asset.manager.device.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceHistoryVO {
    private Integer historyNum;
    private Integer deviceNum;
    private String deviceStatusCode;
    private String approvalStatusCode;
    private String changeContents;
    private String reason;
    private String createDatetime;
    private Integer createUser;
}