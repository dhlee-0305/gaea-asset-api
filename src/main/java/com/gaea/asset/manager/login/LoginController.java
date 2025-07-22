package com.gaea.asset.manager.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gaea.asset.manager.login.service.LoginService;
import com.gaea.asset.manager.login.vo.LoginVO;
import com.gaea.asset.manager.util.Header;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "로그인 API", description = "로그인 API 입니다.")
@RequiredArgsConstructor
public class LoginController {
	private final LoginService loginService;

	@PostMapping("/auth/login")
	@Operation(summary = "사용자 로그인", description = "사용자 로그인 API")
	Header<String> authLogin(@RequestBody LoginVO loginVO) {
		return loginService.authLogin(loginVO);
	}
	
	@PutMapping("/auth/password")
	@Operation(summary = "비밀번호 변경", description = "비밀번호 변경 API")
	Header<LoginVO> updatePassword(@RequestBody LoginVO loginVO) {
		return loginService.updatePassword(loginVO);
	}
}
