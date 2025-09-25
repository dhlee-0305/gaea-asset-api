package com.gaea.asset.manager.login;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gaea.asset.manager.login.service.LoginService;
import com.gaea.asset.manager.login.vo.LoginVO;
import com.gaea.asset.manager.util.Header;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "로그인 API", description = "로그인 API 입니다.")
@RequiredArgsConstructor
public class LoginController {
	private final LoginService loginService;
	
	@PostMapping("/auth/login")
	@Operation(summary = "사용자 로그인", description = "사용자 로그인 API")
	Header<String> authLogin(@RequestBody LoginVO loginVO, HttpServletResponse res) {
		return loginService.authLogin(loginVO, res);
	}
	
	@PostMapping("/auth/logout")
	@Operation(summary = "사용자 로그아웃", description = "사용자 로그아웃 API")
	Header<String> authLogOut(HttpServletResponse res) {
		return loginService.authLogOut(res);
	}
	
	@PostMapping("/auth/refresh")
	@Operation(summary = "Refresh Token", description = "Refresh Token API")
	Header<String> authRefresh(@CookieValue("refreshToken") String refreshToken) {
		return loginService.authRefresh(refreshToken);
	}
	
	@PutMapping("/auth/password")
	@Operation(summary = "비밀번호 변경", description = "비밀번호 변경 API")
	Header<LoginVO> updatePassword(@RequestBody LoginVO loginVO) {
		return loginService.updatePassword(loginVO);
	}
	
	@PutMapping("/auth/password/reset")
	@Operation(summary = "비밀번호 초기화 요청", description = "비밀번호 초기화 요청 API")
	Header<LoginVO> updatePasswordResetReq(@RequestBody LoginVO loginVO) {
		return loginService.updatePasswordResetReq(loginVO);
	}
}
