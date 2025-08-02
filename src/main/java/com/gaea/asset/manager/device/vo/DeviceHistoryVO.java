package com.gaea.asset.manager.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "전산 장비 이력 정보")
@Data
public class DeviceHistoryVO {

    @Schema(description = "이력 순번", example = "1")
    private Integer historyNum;

    @Schema(description = "장비 순번", example = "1")
    private Integer deviceNum;

    @Schema(description = "사원번호", example = "100000")
    private Integer empNum;

    @Schema(description = "사용자명", example = "홍길동")
    private String userName;

    @Schema(description = "장비 상태", example = "사용 중")
    private String deviceStatus;

    @Schema(description = "결재 상태", example = "승인 완료")
    private String approvalStatus;

    @Schema(description = "변경 내용", example = "메모리 업그레이드")
    private String changeContents;

    @Schema(description = "사유", example = "성능 개선")
    private String reason;

    @Schema(description = "생성 일시", example = "2025-08-02 10:00:00")
    private String createDatetime;

    @Schema(description = "생성자", example = "100000")
    private Integer createUser;

    @Schema(description = "장비 유형", example = "PC")
    private String deviceType;
}