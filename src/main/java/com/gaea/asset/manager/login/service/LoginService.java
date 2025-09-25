package com.gaea.asset.manager.login.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.common.constants.ResultCode;
import com.gaea.asset.manager.login.vo.LoginVO;
import com.gaea.asset.manager.login.vo.UserInfoVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {
	private final LoginMapper loginMapper;
	private final JwtUtil jwtUtil;

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	public Header<String> authLogin(LoginVO loginVO, HttpServletResponse res) {
		if(loginVO == null || loginVO.getUserId().isBlank() || loginVO.getPassword().isBlank()) {
			LOG.info("로그인 정보 누락.");
			return Header.ERROR(ResultCode.BAD_REQUEST, "필수입력 정보가 누락 되었습니다.");
		}

		UserInfoVO userInfoVO = getUserInfo(loginVO);

		if (userInfoVO != null && loginVO.getPassword().equals(userInfoVO.getPassword())) {
			if(userInfoVO.getPasswordChangeDate() == null || userInfoVO.getPasswordChangeDate().isBlank() ) {
				return Header.ERROR(ResultCode.NO_CONTENT, "초기 비밀번호를 사용 중입니다. 비밀번호를 변경해 주세요.");
			}
			String accessToken = jwtUtil.generateToken(userInfoVO);
			String refreshToken = jwtUtil.generateRefreshToken(userInfoVO);

			Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
			refreshCookie.setHttpOnly(true);
			refreshCookie.setPath("/");
			refreshCookie.setMaxAge(7 * 24 * 60 * 60);
			res.addCookie(refreshCookie);
			
			return Header.OK(ResultCode.OK, "", accessToken);
		} else {
			return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "ERROR");
		}
	}
	
	public Header<String> authLogOut(HttpServletResponse res) {
		Cookie cookie = new Cookie("refreshToken", null);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(0); // 쿠키 만료 즉시 삭제
		res.addCookie(cookie);
			
		return Header.OK(ResultCode.OK, "", null);
	}
	
	public Header<String> authRefresh(String refreshToken) {
		
		if (!jwtUtil.validateRefreshToken(refreshToken)) {
			LOG.info("authRefresh Invalid token : {}", refreshToken);
			return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "Invalid token");
		}

		String userId = jwtUtil.extractUserId(refreshToken);
		
		LoginVO loginVO = new LoginVO();
		loginVO.setUserId(userId);
		
		UserInfoVO userInfoVO = getUserInfo(loginVO);
		String newAccessToken = jwtUtil.generateToken(userInfoVO);
		
		LOG.info("authRefresh newAccessToken : {}", newAccessToken);
		
		return Header.OK(ResultCode.OK, "", newAccessToken);
	}

	public Header<LoginVO> updatePassword(LoginVO loginVO) {
		if(loginVO == null || loginVO.getUserId().isBlank() || loginVO.getNewPassword().isBlank()) {
			LOG.info("필수 정보 누락.");
			return Header.ERROR(ResultCode.BAD_REQUEST, "필수입력 정보가 누락 되었습니다.");
		}

		//사용자 정보 조회
		UserInfoVO userInfoVO = getUserInfo(loginVO);
		if(userInfoVO == null) {
			LOG.info("사용자 정보 입력 오류.");
			return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "사용자 정보 입력 오류 입니다.");
		}

		if (loginMapper.updatePassword(loginVO) > 0) {
			return Header.OK(ResultCode.OK, "수정되었습니다.", null);
		} else {
			return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "ERROR");
		}
	}

	public Header<LoginVO> updatePasswordResetReq(LoginVO loginVO) {
		if(loginVO == null || loginVO.getUserId().isBlank()) {
			LOG.info("필수 정보 누락.");
			return Header.ERROR(ResultCode.BAD_REQUEST, "필수입력 정보가 누락 되었습니다.");
		}

		//사용자 정보 조회
		UserInfoVO userInfoVO = getUserInfo(loginVO);
		if(userInfoVO == null) {
			LOG.info("사용자 정보 입력 오류.");
			return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "사용자 정보 입력 오류 입니다.");
		}

		if (loginMapper.updatePasswordResetReq(loginVO) > 0) {
			return Header.OK(ResultCode.OK, "등록되었습니다.", null);
		} else {
			return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "초기화 요청 중 오류가 발생했습니다.");
		}
	}

	public UserInfoVO getUserInfo(LoginVO loginVO) {
		return loginMapper.authLogin(loginVO);
	}
}
