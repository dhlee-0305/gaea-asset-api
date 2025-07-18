package com.gaea.asset.manager.login.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.login.vo.LoginVO;
import com.gaea.asset.manager.login.vo.UserVO;
import com.gaea.asset.manager.util.Header;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {
	private final String SECRET_KEY = "gaeaAssetSecretKey";
	private final LoginMapper loginMapper;
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	public Header<String> authLogin(LoginVO loginVO) {
		UserVO userVO = loginMapper.authLogin(loginVO);
		
		LOG.info("reqPass : {}, resPass : {}", loginVO.getPassword(), userVO.getPassword());
		LOG.info("BCryptPasswordEncoder : {}", new BCryptPasswordEncoder().matches(loginVO.getPassword(), userVO.getPassword()));
		
		if (userVO != null && new BCryptPasswordEncoder().matches(loginVO.getPassword(), userVO.getPassword())) {
			String token = generateToken(userVO);
			
			LOG.info("generateToken : ", token);
			
			return Header.OK(token);
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}
	
	public String generateToken(UserVO userVO) {
		return Jwts.builder()
			.claim("empNum", userVO.getEmpNum())
			.claim("userId", userVO.getUserId())
			.claim("userName", userVO.getUserName())
			.claim("orgId", userVO.getOrgId())
			.claim("orgName", userVO.getOrgName())
			.claim("roleCode", userVO.getRoleCode())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1일
			.signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
			.compact();
	}
}
