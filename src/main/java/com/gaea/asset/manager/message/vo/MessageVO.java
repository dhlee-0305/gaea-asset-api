package com.gaea.asset.manager.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "메세지 정보")
public class MessageVO {
    @Schema(description = "수신자", example = "user@gaeasoft.co.kr")
    private String to;

    @Schema(description = "제목", example = "[메세지] 메세지")
    private String subject;

    @Schema(description = "내용", example = "메세지입니다")
    private String text;
}
