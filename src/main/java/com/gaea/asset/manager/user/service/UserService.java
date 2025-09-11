package com.gaea.asset.manager.user.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.gaea.asset.manager.code.service.CodeMapper;
import com.gaea.asset.manager.code.vo.CodeVO;
import com.gaea.asset.manager.common.constants.CodeConstants;
import com.gaea.asset.manager.common.constants.Constants;
import com.gaea.asset.manager.organization.service.OrganizationMapper;
import com.gaea.asset.manager.organization.vo.OrganizationVO;
import com.gaea.asset.manager.util.Pagination;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.user.vo.UserVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Search;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserMapper userMapper;
	private final CodeMapper codeMapper;
	private final OrganizationMapper organizationMapper;

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

	public Header<HashMap<String, Object>> getUser(Integer empNum) {
		UserVO userVO = userMapper.getUser(empNum);
		if(userVO == null){
			return Header.ERROR(String.valueOf(HttpServletResponse.SC_NO_CONTENT), "조회된 정보가 없습니다.");
		}
		HashMap<String, Object> resData = this.getUserCommonCode(true, userVO);
		resData.put("userInfo", userVO);

		return Header.OK(resData);
	}

	public Header<UserVO> insertUser(UserVO userVO) {
		if(!chkUserData(userVO)){
			// 팀장 선택 가능여부 체크
			return Header.ERROR(String.valueOf(HttpServletResponse.SC_CONFLICT), "이미 팀장이 존재합니다.");
		}

		if (userMapper.insertUser(userVO) > 0) {
			return Header.OK(userVO);
		} else {
			return Header.ERROR(String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), "ERROR");
		}
	}

	public Header<UserVO> updateUser(UserVO userVO) {
		if(!chkUserData(userVO)){
			// 팀장 선택 가능여부 체크
			return Header.ERROR(String.valueOf(HttpServletResponse.SC_CONFLICT), "이미 팀장이 존재합니다.");
		}

		if (userMapper.updateUser(userVO) > 0){
			return Header.OK(userVO);
		} else {
			return Header.ERROR(String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), "ERROR");
		}
	}

	public Header<String> deleteUser(Integer empNum) {
		if(userMapper.deleteUser(empNum) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR(String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), "ERROR");
		}
	}

	public Header<String> initPassword(UserVO userVO ){
		if(userVO == null || userVO.getUserId().isBlank()){
			return Header.ERROR(String.valueOf(HttpServletResponse.SC_BAD_REQUEST), "필수입력 정보가 누락 되었습니다.");
		}
		userVO.setInitPassword(Constants.INIT_PASSWORD);
		if(userMapper.initPassword(userVO) > 0){
			return Header.OK();
		} else {
			return  Header.ERROR(String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), "패스워드 초기화 중 오류가 발생했습니다.");
		}
	}

	/**
	 * 사용자 정보 유효성 체크
	 * @param userVO
	 * @return
	 */
	public boolean chkUserData(UserVO userVO){
		if(CodeConstants.TEAM_LEADER.equals(userVO.getUserPositionCd()) && userMapper.chkLeaderAvl(userVO) > 0){
			// 팀장 선택 가능여부 체크
			return false;
		}
		return true;
	}

	public HashMap<String, Object> getUserCommonCode(boolean isUpdate, UserVO userVO) {
		HashMap<String, Object> resData = new HashMap<>();
		// 부서정보 조회
		List<OrganizationVO> organizationList = organizationMapper.selectOrganizationList();
		if (isUpdate) {
			// 수정페이지에서 요청한 경우
			OrganizationVO disivion = organizationList.stream().filter(org-> org.getOrgId().equals(userVO.getParentOrgId())).findFirst().get();
			resData.put("division", disivion.getOrgId());
			OrganizationVO company = organizationList.stream().filter(org-> org.getOrgId().equals(disivion.getParentOrgId())).findFirst().get();
			resData.put("company", company.getOrgId());
		}
		resData.put("organizationList", organizationList);
		return resData;
	}
}
