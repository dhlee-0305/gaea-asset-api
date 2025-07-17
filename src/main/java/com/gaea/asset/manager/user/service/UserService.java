package com.gaea.asset.manager.user.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gaea.asset.manager.user.vo.UserVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Search;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserMapper userMapper;

	public Header<List<UserVO>> getUserList(Integer currentPage, Integer pageSize, Search search) {
		HashMap<String, Object> paramMap = new HashMap<>();

		paramMap.put("searchColumn", search.getSearchColumn());
		paramMap.put("searchKeyword", search.getSearchKeyword());

		List<UserVO> userList = userMapper.getUserList(paramMap);

		return Header.OK(userList);
	}
}
