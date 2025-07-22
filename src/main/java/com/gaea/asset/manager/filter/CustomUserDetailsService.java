package com.gaea.asset.manager.filter;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.login.service.LoginService;
import com.gaea.asset.manager.login.vo.LoginVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
	private final LoginService loginService;

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		LoginVO loginVO = new LoginVO();
		loginVO.setUserId(userId);
		
		return new CustomUserDetails(loginService.getUserInfo(loginVO));
	}

}
