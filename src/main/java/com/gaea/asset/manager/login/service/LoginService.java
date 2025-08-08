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
			return Header.ERROR("400", "필수입력 정보가 누락 되었습니다.");
		}
		
		UserInfoVO userInfoVO = getUserInfo(loginVO);
		
		if (userInfoVO != null && loginVO.getPassword().equals(userInfoVO.getPassword())) {
			if(userInfoVO.getPasswordChangeDate().isBlank() ) {
				return Header.ERROR("204", "초기 비밀번호를 사용 중입니다. 비밀번호를 변경해 주세요.");
			}
			String token = jwtUtil.generateToken(userInfoVO);
			return Header.OK("200", "", token);
		} else {
			return Header.ERROR("500", "ERROR");
		}
	}
	
	public Header<LoginVO> updatePassword(LoginVO loginVO) {
		if(loginVO == null || loginVO.getUserId().isBlank() || loginVO.getNewPassword().isBlank()) {
			LOG.info("필수 정보 누락.");
			return Header.ERROR("400", "필수입력 정보가 누락 되었습니다.");
		}
		
		//사용자 정보 조회
		UserInfoVO userInfoVO = getUserInfo(loginVO);
		if(userInfoVO == null) {
			LOG.info("사용자 정보 입력 오류.");
			return Header.ERROR("500", "사용자 정보 입력 오류 입니다.");
		}
		
		if (loginMapper.updatePassword(loginVO) > 0) {
			return Header.OK("200", "수정되었습니다.", null);
		} else {
			return Header.ERROR("500", "ERROR");
		}
	}
	
	public Header<LoginVO> updatePasswordResetReq(LoginVO loginVO) {
		if(loginVO == null || loginVO.getUserId().isBlank()) {
			LOG.info("필수 정보 누락.");
			return Header.ERROR("400", "필수입력 정보가 누락 되었습니다.");
		}
		
		//사용자 정보 조회
		UserInfoVO userInfoVO = getUserInfo(loginVO);
		if(userInfoVO == null) {
			LOG.info("사용자 정보 입력 오류.");
			return Header.ERROR("500", "사용자 정보 입력 오류 입니다.");
		}
		
		if (loginMapper.updatePasswordResetReq(loginVO) > 0) {
			return Header.OK("200", "등록되었습니다.", null);
		} else {
			return Header.ERROR("500", "초기화 요청 중 오류가 발생했습니다.");
		}
	}
	
	public UserInfoVO getUserInfo(LoginVO loginVO) {
		return loginMapper.authLogin(loginVO);
	}
}
