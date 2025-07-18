package com.gaea.asset.manager.login.service;

import org.apache.ibatis.annotations.Mapper;

import com.gaea.asset.manager.login.vo.LoginVO;
import com.gaea.asset.manager.login.vo.UserVO;

@Mapper
public interface LoginMapper {
	UserVO authLogin(LoginVO loginVO);
}
