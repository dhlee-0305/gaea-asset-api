package com.gaea.asset.manager.util;

import org.springframework.security.core.context.SecurityContextHolder;

import com.gaea.asset.manager.common.filter.CustomUserDetails;
import com.gaea.asset.manager.login.vo.UserInfoVO;

public class AuthUtil {

	public static UserInfoVO getLoginUserInfo() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {

            return ((CustomUserDetails) principal).getUserInfo();
        } else {
            throw new IllegalStateException("사용자 정보가 존재하지 않습니다.");
        }
    }
}
