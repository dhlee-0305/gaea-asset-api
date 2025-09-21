package com.gaea.asset.manager.verification.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.gaea.asset.manager.common.constants.CommonCode;
import com.gaea.asset.manager.common.constants.ResultCode;
import com.gaea.asset.manager.message.service.MessageService;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.verification.vo.UserVerificationVO;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserVerificationService {
	private final UserVerificationMapper userVerificationMapper;
	private final MessageService messageService;

	@Transactional
	public Header<UserVerificationVO> sendVerificationCode(UserVerificationVO userVerificationVO) {
		
		String code = generateVerificationCode();
		LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
		
		UserVerificationVO verification = new UserVerificationVO();
		verification.setUserId(userVerificationVO.getUserId());
		verification.setCode(code);
		verification.setExpiresDatetime(expiresAt);

		if (userVerificationMapper.insertVerificationCode(verification) > 0) {
			try {
				messageService.sendToUser(CommonCode.MESSAGE_RESET_PASSWORD_OTP, userVerificationVO.getUserId(), code);
				return Header.OK(ResultCode.OK, "인증번호가 발송되었습니다.", null);
			} catch (MessagingException e) {
				return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "인증번호가 발송중 오류가 발생했습니다.");
			}
		} else {
			return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "인증번호가 발송중 오류가 발생했습니다.");
		}
	}

	@Transactional
	public Header<UserVerificationVO> verifyCode(UserVerificationVO userVerificationVO) {
		UserVerificationVO verification = userVerificationMapper.getLatestValidCode(userVerificationVO);
		
		if(verification != null) {
			if (userVerificationMapper.updateVerified(verification) > 0) {
				return Header.OK(ResultCode.OK, "", null);
			} else {
				return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "유효하지 않거나 만료된 인증번호입니다.");
			}
		}else{
			return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "유효하지 않거나 만료된 인증번호입니다.");
		}
	}
	
	private String generateVerificationCode() {
		return String.valueOf(new Random().nextInt(900000) + 100000);
	}
}
