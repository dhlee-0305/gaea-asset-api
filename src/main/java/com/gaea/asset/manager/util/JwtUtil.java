package com.gaea.asset.manager.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gaea.asset.manager.login.vo.UserInfoVO;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	@Value("${jwt.secret}")
	private String SECRET_KEY;
	
	private final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60; // 1시간
	private final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7일

	public String generateToken(UserInfoVO userInfoVO) {
		return Jwts.builder()
				.claim("empNum", userInfoVO.getEmpNum())
				.claim("userId", userInfoVO.getUserId())
				.claim("userName", userInfoVO.getUserName())
				.claim("orgId", userInfoVO.getOrgId())
				.claim("orgName", userInfoVO.getOrgName())
				.claim("roleCode", userInfoVO.getRoleCode())
				.setSubject(userInfoVO.getUserId())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
				.compact();
	}
	
	public String generateRefreshToken(UserInfoVO userInfoVO) {
		return Jwts.builder()
				.claim("empNum", userInfoVO.getEmpNum())
				.claim("userId", userInfoVO.getUserId())
				.claim("userName", userInfoVO.getUserName())
				.claim("orgId", userInfoVO.getOrgId())
				.claim("orgName", userInfoVO.getOrgName())
				.claim("roleCode", userInfoVO.getRoleCode())
				.setSubject(userInfoVO.getUserId())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
				.compact();
	}
	
	// 토큰에서 사용자 ID 추출
	public String extractUserId(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody().getSubject();
	}

	// 토큰 만료 시간 확인
	public boolean isTokenExpired(String token) {
		Date expiration = Jwts.parser().setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody().getExpiration();
		
		return expiration.before(new Date());
	}

	// 토큰 유효성 검증
	public boolean validateToken(String token, String userId) {
		return extractUserId(token).equals(userId) && !isTokenExpired(token);
	}
	
	public boolean validateRefreshToken(String token) {
		try {
			Jwts.parser().setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
