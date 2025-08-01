package com.gaea.asset.manager.common.filter;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gaea.asset.manager.login.vo.UserInfoVO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails{
	private static final long serialVersionUID = 1L;
	private final UserInfoVO userInfoVO;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return userInfoVO.getUserId().toString();
	}

	public UserInfoVO getUserInfo() {
		return userInfoVO;
	}
}
