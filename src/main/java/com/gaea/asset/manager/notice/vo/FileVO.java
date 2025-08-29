package com.gaea.asset.manager.notice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "파일 정보")
public class FileVO {
  	@Schema(description = "파일 번호", example = "1")
	private Integer fileNum;

	@Schema(description = "파일 원본 제목", example = "이미지")
	private String originFileName;

	@Schema(description = "파일 저장 제목", example = "이미지1")
	private String storedFileName;

	@Schema(description = "업로드 일시", example = "2025-07-10")
	private String uploadDateTime;

	@Schema(description = "삭제 여부", example = "N")
	private boolean isDeleted;

    @Schema(description = "공지사항 번호", example = "1")
    private Long noticeNum;
}
