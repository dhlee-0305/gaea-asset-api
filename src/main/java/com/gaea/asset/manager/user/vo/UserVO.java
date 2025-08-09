package com.gaea.asset.manager.user.vo;

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
public class UserVO {
	@Schema(description = "사원번호", example = "100000")
	Integer empNum;

	@Schema(description = "사용자ID", example = "gaea")
	String userId;

	@Schema(description = "사용자명", example = "홍길동")
	String userName;

	@Schema(description = "비밀번호", example = "1111")
	String password;

	@Schema(description = "조직ID", example = "1111")
	Integer orgId;

	@Schema(description = "조직명", example = "IT서비스부문")
	String orgName;

	@Schema(description = "직책코드", example = "04")
	String userPositionCd;

	@Schema(description = "직위코드", example = "07")
	String userGradeCd;

	@Schema(description = "직책명", example = "04")
	String userPositionName;

	@Schema(description = "직위명", example = "07")
	String userGradeName;

	@Schema(description = "권한코드", example = "01")
	String roleCode;

	@Schema(description = "재직여부", example = "Y")
	String isEmployed;

	@Schema(description = "비밀번호 변경일자", example = "20250101")
	String passwordChangeDate;

	@Schema(description = "생성일시", example = "20250101100000")
	String createDatetime;

	@Schema(description = "비밀번호초기화요청", example = "N")
	String passwordResetReq;
}
