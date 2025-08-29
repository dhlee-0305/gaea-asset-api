package com.gaea.asset.manager.notice.vo;

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
@Schema(description = "공지사항 정보")
public class NoticeVO {
    @Schema(description = "공지사항 순번", example = "1")
    private Integer rowNum;

	@Schema(description = "공지사항 번호", example = "1")
	private Long noticeNum;
	
	@Schema(description = "공지사항 제목", example = "[공지] 공지사항")
	private String title;
	
	@Schema(description = "본문 내용", example = "공지사항입니다")
	private String content;
	
	@Schema(description = "생성 일시", example = "2025-07-10")
	private String createDateTime;
	
	@Schema(description = "생성자", example = "admin")
	private String createUser;

	@Schema(description = "최종 변경 일시", example = "2025-07-11")
	private String updateDateTime;

	@Schema(description = "최종 변경자", example = "admin")
	private String updateUser;

    @Schema(description = "파일 목록", example = "[이미지1, 이미지2]")
    private List<FileVO> fileList;
}
