package com.gaea.asset.manager.user.service;

import java.util.HashMap;
import java.util.List;

import com.gaea.asset.manager.common.constants.CodeConstants;
import com.gaea.asset.manager.util.Pagination;
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

		paramMap.put("page", (currentPage - 1) * pageSize);
		paramMap.put("size", pageSize);
		paramMap.put("searchColumn", search.getSearchColumn());
		paramMap.put("searchKeyword", search.getSearchKeyword());

		List<UserVO> userList = userMapper.getUserList(paramMap);
		Pagination pagination = new Pagination(
				userMapper.getUserListCount(paramMap),
				currentPage,
				pageSize,
				10
		);


		return Header.OK(userList, pagination);
	}

	public Header<UserVO> getUser(Integer empNum) {
		return Header.OK(userMapper.getUser(empNum));
	}

	public Header<UserVO> insertNotice(UserVO userVO) {
		if(CodeConstants.TEAM_LEADER.equals(userVO.getUserPositionCd()) && userMapper.chkLeaderAvl(userVO) > 0){
			// 팀장 선택 가능여부 체크
			return Header.ERROR("409", "이미 팀장이 존재합니다.");
		}

		if (userMapper.insertUser(userVO) > 0) {
			return Header.OK(userVO);
		} else {
			return Header.ERROR("500", "ERROR");
		}
	}

	public Header<UserVO> updateUser(UserVO userVO) {
		if(CodeConstants.TEAM_LEADER.equals(userVO.getUserPositionCd()) && userMapper.chkLeaderAvl(userVO) > 0){
			// 팀장 선택 가능여부 체크
			return Header.ERROR("409", "이미 팀장이 존재합니다.");
		}

		if (userMapper.updateUser(userVO) > 0){
			return Header.OK(userVO);
		} else {
			return Header.ERROR("500", "ERROR");
		}
	}

	public Header<String> deleteUser(Integer empNum) {
		if(userMapper.deleteUser(empNum) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("500", "ERROR");
		}
	}

	public Header<String> initPassword(UserVO userVO ){
		if(userVO == null || userVO.getUserId().isBlank()){
			return Header.ERROR("400", "필수입력 정보가 누락 되었습니다.");
		}
		if(userMapper.initPassword(userVO) > 0){
			return Header.OK();
		} else {
			return  Header.ERROR("500", "패스워드 초기화 중 오류가 발생했습니다.");
		}
	}
}
