package com.gaea.asset.manager.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "활동 정보 (Live Activity 용)")
public class ActivityVO {
    @Schema(description = "최근 장비 변경 이력", example = "[]")
    private List<RecentActivityItem> recentActivities;

    @Schema(description = "대기중인 승인 건수", example = "5")
    private Integer pendingApprovalCount;

    @Schema(description = "읽지 않은 메시지 건수", example = "3")
    private Integer unreadMessageCount;

    @Schema(description = "전체 장비 수", example = "150")
    private Integer totalDeviceCount;

    @Schema(description = "사용 중인 장비 수", example = "120")
    private Integer activeDeviceCount;

    @Schema(description = "내 장비 수", example = "2")
    private Integer myDeviceCount;

    @Schema(description = "마지막 업데이트 시간", example = "2025-10-29T23:39:53")
    private String lastUpdateTime;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "최근 활동 항목")
    public static class RecentActivityItem {
        @Schema(description = "활동 유형", example = "DEVICE_CREATED")
        private String activityType;

        @Schema(description = "장비 번호", example = "1")
        private Integer deviceNum;

        @Schema(description = "장비 유형", example = "컴퓨터")
        private String deviceType;

        @Schema(description = "사용자명", example = "홍길동")
        private String userName;

        @Schema(description = "활동 내용", example = "장비가 등록되었습니다")
        private String description;

        @Schema(description = "생성일시", example = "2025-10-29T23:39:53")
        private String createDatetime;
    }
}
