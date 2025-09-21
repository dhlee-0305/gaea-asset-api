package com.gaea.asset.manager.verification.vo;

import java.time.LocalDateTime;

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
@Schema(description = "인증번호")
public class UserVerificationVO {
	@Schema(description = "인증순번", example = "")
	private String verificationNum;
	
	@Schema(description = "사용자 ID", example = "")
	private String userId;
	
	@Schema(description = "사용자 email", example = "")
	private String email;
	
	@Schema(description = "인증코드", example = "")
	private String code;
	
	@Schema(description = "인증여부", example = "FALSE")
	private String isVerified;
	
	@Schema(description = "만료일시", example = "")
	private LocalDateTime expiresDatetime;
	
	@Schema(description = "생성일시", example = "")
	private String createDatetime;
}
