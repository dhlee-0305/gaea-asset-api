package com.gaea.asset.manager.login.service;

import org.apache.ibatis.annotations.Mapper;

import com.gaea.asset.manager.login.vo.LoginVO;
import com.gaea.asset.manager.login.vo.UserInfoVO;

@Mapper
public interface LoginMapper {
	UserInfoVO authLogin(LoginVO loginVO);
	int updatePassword(LoginVO loginVO);
}
