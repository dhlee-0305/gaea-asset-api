package com.gaea.asset.manager.code.vo;

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
@Schema(description = "공통코드")
public class CodeVO {
	@Schema(description = "그룹코드", example = "C001")
	private String category;

	@Schema(description = "그룹명", example = "직책")
	private String categoryName;

	@Schema(description = "코드", example = "03")
	private String code;

	@Schema(description = "코드명", example = "팀장")
	private String codeName;
}
