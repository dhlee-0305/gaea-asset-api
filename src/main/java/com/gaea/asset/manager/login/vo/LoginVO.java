package com.gaea.asset.manager.login.vo;

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
@Schema(description = "전산 장비 정보")
public class LoginVO {
	@Schema(description = "사용자ID", example = "")
	private String userId;
	
	@Schema(description = "비밀번호 (암호화 BCrypt)", example = "1111")
	private String password;
}
