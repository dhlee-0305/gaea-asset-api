package com.gaea.asset.manager.verification;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.verification.service.UserVerificationService;
import com.gaea.asset.manager.verification.vo.UserVerificationVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "인증번호 발송 및 인증 API", description = "인증번호 관리 API 입니다.")
@RequiredArgsConstructor
public class UserVerificationController {
	private final UserVerificationService userVerificationService;

	@PostMapping("/verification/request")
	@Operation(summary = "인증번호 발송", description = "인증번호 발송 API")
	Header<UserVerificationVO> sendVerificationCode(@RequestBody UserVerificationVO userVerificationVO) {
		return userVerificationService.sendVerificationCode(userVerificationVO);
	}

	@PostMapping("/verification/verify")
	@Operation(summary = "인증번호 인증", description = "인증번호 인증 API")
	Header<UserVerificationVO> verifyCode(@RequestBody UserVerificationVO userVerificationVO) {
		return userVerificationService.verifyCode(userVerificationVO);
	}
}
