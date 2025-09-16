package com.gaea.asset.manager.user.service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.gaea.asset.manager.code.service.CodeMapper;
import com.gaea.asset.manager.code.vo.CodeVO;
import com.gaea.asset.manager.common.constants.CodeConstants;
import com.gaea.asset.manager.common.constants.Constants;
import com.gaea.asset.manager.device.vo.DeviceVO;
import com.gaea.asset.manager.login.vo.UserInfoVO;
import com.gaea.asset.manager.organization.service.OrganizationMapper;
import com.gaea.asset.manager.organization.vo.OrganizationVO;
import com.gaea.asset.manager.util.AuthUtil;
import com.gaea.asset.manager.util.Pagination;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.user.vo.UserVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Search;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserMapper userMapper;
	private final CodeMapper codeMapper;
	private final OrganizationMapper organizationMapper;

	private static final long MAX_EXCEL_FILE_SIZE = 5 * 1024 * 1024;	// 엑셀 파일 업로드 최대 사이즈 5MB
	private static final int BULK_INSERT_SIZE = 1000;					// BULK INSERT 사이즈

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

	/**
	 * 사용자 정보 엑셀 업로드
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws EncryptedDocumentException
	 */
	public Header<UserVO> uploadUserExcel(MultipartFile file) throws Exception {
		log.info("upload excelFilename : {}", file.getOriginalFilename());
		UserInfoVO userInfo = AuthUtil.getLoginUserInfo();

		// 파일 확장자 검증
		String filename = file.getOriginalFilename();
		if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
			return Header.ERROR(String.valueOf(HttpServletResponse.SC_BAD_REQUEST), "Excel 파일만 업로드 가능합니다.");
		}

		// 파일 사이즈 체크
		if(file.getSize() > MAX_EXCEL_FILE_SIZE) {
			return Header.ERROR(String.valueOf(HttpServletResponse.SC_BAD_REQUEST), "파일 크기가 너무 큽니다.");
		}

		// 코드 목록 조회
		List<CodeVO> codeList = codeMapper.getCodeListByCodes(Arrays.asList(
				CodeConstants.CATEGORY_POSITION,
				CodeConstants.CATEGORY_GRADE));
		// 직책 코드 목록
		List<CodeVO> positionList = codeList.stream()
				.filter(code -> code.getCategory().equals(CodeConstants.CATEGORY_POSITION))
				.collect(Collectors.toList());
		// 직위 코드 목록
		List<CodeVO> gradeList = codeList.stream()
				.filter(code -> code.getCategory().equals(CodeConstants.CATEGORY_GRADE))
				.collect(Collectors.toList());
		// 부서정보 조회
		List<OrganizationVO> organizationList = organizationMapper.selectOrganizationList();

		// Excel 파일 처리
		int startRow = 2;
		Workbook workbook = null;
		DataFormatter formatter = new DataFormatter();
		try {
			workbook = WorkbookFactory.create(file.getInputStream());

			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);
				List<UserVO> userList = new ArrayList<UserVO>();

				for (Row row : sheet) {
					if (row.getRowNum() < startRow) continue;
					UserVO userVO = new UserVO();

					Integer empNum = Integer.valueOf(formatter.formatCellValue(row.getCell(1))); 		// 사번
					userVO.setEmpNum(empNum);
					String userId = formatter.formatCellValue(row.getCell(2)); // ID
					userVO.setUserId(userId);
					String userName = formatter.formatCellValue(row.getCell(3)); // 사용자명
					userVO.setUserName(userName);
					String orgName = formatter.formatCellValue(row.getCell(4)); // 조직명
					userVO.setOrgId(organizationList.stream()
							.filter(org -> org.getOrgName().equals(orgName))
							.map(org -> org.getOrgId())
							.findFirst()
							.get());
					String positionName = formatter.formatCellValue(row.getCell(5)); // 직책명
					userVO.setUserPositionCd(positionList.stream()
							.filter(code -> code.getCodeName().equals(positionName))
							.map(code -> code.getCode())
							.findFirst()
							.get());
					String gradeName = formatter.formatCellValue(row.getCell(6)); // 직위명
					userVO.setUserGradeCd(gradeList.stream()
							.filter(code -> code.getCodeName().equals(gradeName))
							.map(code -> code.getCode())
							.findFirst()
							.get());

					if(!chkUserData(userVO)){
						// 팀장 선택 가능여부 체크
						return Header.ERROR(String.valueOf(HttpServletResponse.SC_CONFLICT), "팀장이 중복되는 항목이 존재하여 업로드가 불가능합니다.");
					}

					userList.add(userVO);
					log.info("##### Row {} : {}", i, userVO.toString());

					// DB 저장
					if (userList.size() >= BULK_INSERT_SIZE) {
						Map<Integer, List<UserVO>> leaderByOrg = userList.stream()
								.filter(user -> user.getUserPositionCd().equals(CodeConstants.TEAM_LEADER))
								.collect(Collectors.groupingBy(UserVO::getOrgId));

						boolean hasDuplicateLeader = leaderByOrg.values().stream()
								.anyMatch(list -> list.size() > 1);
						if(hasDuplicateLeader){
							log.info("[엑셀 안에서 팀장이 중복된 케이스] 팀장이 중복되는 항목이 존재하여 업로드가 불가능합니다.");
						}

//						userMapper.insertUserList(userList);
						userList.clear();
					}
				}

				// DB 저장
				if(userList.size() > 0) {
					Map<Integer, List<UserVO>> leaderByOrg = userList.stream()
							.filter(user -> user.getUserPositionCd().equals(CodeConstants.TEAM_LEADER))
							.collect(Collectors.groupingBy(UserVO::getOrgId));

					boolean hasDuplicateLeader = leaderByOrg.values().stream()
							.anyMatch(list -> list.size() > 1);
					if(hasDuplicateLeader){
						log.info("[엑셀 안에서 팀장이 중복된 케이스] 팀장이 중복되는 항목이 존재하여 업로드가 불가능합니다.");
					}

//					userMapper.insertUserList(userList);
				}
			}

		} catch (Exception e) {
			log.error("excel upload error : ", e);
			return Header.ERROR(String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), "ERROR");
		}finally {
			if(workbook != null) {
				workbook.close();
			}
		}

		return Header.OK();
	}
}
