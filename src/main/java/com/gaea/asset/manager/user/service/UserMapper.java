package com.gaea.asset.manager.user.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gaea.asset.manager.user.vo.UserVO;

@Mapper
public interface UserMapper {
	List<UserVO> getUserList(HashMap<String, Object> paramMap);
}
