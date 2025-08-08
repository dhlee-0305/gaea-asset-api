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
    @Schema(description = "메세지 순번", example = "1")
    private Integer messageNum;

    @Schema(description = "수신자", example = "100000")
    private Integer recipient;

    @Schema(description = "발신자", example = "100000")
    private Integer sender;

    @Schema(description = "제목", example = "[메세지] 메세지")
    private String title;

    @Schema(description = "내용", example = "메세지입니다")
    private String content;

    @Schema(description = "메세지 상태 코드", example = "")
    private String messageStatusCode;

    @Schema(description = "생성일시", example = "")
    private String createDatetime;
}
