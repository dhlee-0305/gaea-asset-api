package com.gaea.asset.manager.organization.vo;

import lombok.Data;

@Data
public class OrganizationVO {
    private Integer orgId;         // 조직 ID (기본키)
    private String orgName;        // 조직명
    private String orgType;        // 조직 유형 (COMPANY, DIVISION, TEAM)
    private Integer parentOrgId;   // 상위 조직 ID
    private Integer orgLevel;      // 조직 레벨
    private String orgPath;        // 조직 경로
    private Integer sortOrder;     // 정렬 순서
    private String isActive;       // 활성 여부 (Y/N)
    private String createDatetime; // 생성일시 (String 또는 LocalDateTime)
}