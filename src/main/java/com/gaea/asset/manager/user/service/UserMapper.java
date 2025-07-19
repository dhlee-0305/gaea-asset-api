package com.gaea.asset.manager.user.service;

import java.util.HashMap;
import java.util.List;

import com.gaea.asset.manager.notice.vo.NoticeVO;
import org.apache.ibatis.annotations.Mapper;

import com.gaea.asset.manager.user.vo.UserVO;

@Mapper
public interface UserMapper {
	List<UserVO> getUserList(HashMap<String, Object> paramMap);

	int getUserListCount(HashMap<String, Object> paramMap);

	UserVO getUser(Integer empNum);

	int chkLeaderAvl(UserVO entity);

	int insertUser(UserVO entity);

	int updateUser(UserVO entity);

	int deleteUser(Integer empNum);
}
