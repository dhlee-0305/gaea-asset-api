package com.gaea.asset.manager.login.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.login.vo.LoginVO;
import com.gaea.asset.manager.login.vo.UserInfoVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {
	private final LoginMapper loginMapper;
	private final JwtUtil jwtUtil;
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	public Header<String> authLogin(LoginVO loginVO) {
		if(loginVO == null || loginVO.getUserId().isBlank() || loginVO.getPassword().isBlank()) {
			LOG.info("로그인 정보 누락.");
			return Header.ERROR("9999", "로그인 정보 누락.");
		}
		
		UserInfoVO userInfoVO = getUserInfo(loginVO);
		
		if (userInfoVO != null && loginVO.getPassword().equals(userInfoVO.getPassword())) {
			String token = jwtUtil.generateToken(userInfoVO);
			return Header.OK(token);
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}
	
	public Header<LoginVO> updatePassword(LoginVO loginVO) {
		if(loginVO == null || loginVO.getUserId().isBlank() || loginVO.getPassword().isBlank() || loginVO.getNewPassword().isBlank()) {
			LOG.info("필수 정보 누락.");
			return Header.ERROR("9999", "필수 정보 누락.");
		}
		
		//사용자 정보 조회
		UserInfoVO userInfoVO = getUserInfo(loginVO);
		if(userInfoVO == null || !loginVO.getPassword().equals(userInfoVO.getPassword())) {
			LOG.info("사용자 정보 입력 오류.");
			return Header.ERROR("9999", "사용자 정보 입력 오류.");
		}
		
		if (loginMapper.updatePassword(loginVO) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}
	
	public Header<LoginVO> updatePasswordResetReq(LoginVO loginVO) {
		if(loginVO == null || loginVO.getUserId().isBlank()) {
			LOG.info("필수 정보 누락.");
			return Header.ERROR("9999", "필수 입력 정보가 누락 되었습니다.");
		}
		
		//사용자 정보 조회
		UserInfoVO userInfoVO = getUserInfo(loginVO);
		if(userInfoVO == null) {
			LOG.info("사용자 정보 입력 오류.");
			return Header.ERROR("9999", "사용자 정보 입력 오류 입니다.");
		}
		
		if (loginMapper.updatePasswordResetReq(loginVO) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("9999", "초기화 요청 중 오류가 발생했습니다.");
		}
	}
	
	public UserInfoVO getUserInfo(LoginVO loginVO) {
		return loginMapper.authLogin(loginVO);
	}
}
