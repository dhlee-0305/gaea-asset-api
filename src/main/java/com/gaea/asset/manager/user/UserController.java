package com.gaea.asset.manager.user;

import java.util.HashMap;
import java.util.List;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import org.springframework.web.bind.annotation.*;

import com.gaea.asset.manager.user.service.UserService;
import com.gaea.asset.manager.user.vo.UserVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "사용자 관리 관리자 API", description = "사용자 관리 관리자 API 입니다.")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/users")
	@Operation(summary = "사용자 목록 조회", description = "사용자 목록 조회 API")
	Header<List<UserVO>> getUserList(@RequestParam(value="currentPage", required = false) Integer currentPage, @RequestParam(value="pageSize", required = false) Integer pageSize, Search search) {
		return userService.getUserList(currentPage, pageSize, search);
	}

	@GetMapping("/users/{empNum}")
	@Operation(summary = "사용자 상세 조회", description = "사용자 상세 조회 API")
	Header<HashMap<String, Object>> getUser(@PathVariable(name="empNum") Integer empNum) {
		return userService.getUser(empNum);
	}

	@PostMapping("/users")
	@Operation(summary = "사용자 등록", description = "사용자 등록 API")
	Header<UserVO> insertUser(@RequestBody UserVO userVO) {
		return  userService.insertUser(userVO);
	}

	@PutMapping("users/{empNum}")
	@Operation(summary = "사용자 정보 수정", description = "사용자 정보 수정 API")
	@Parameters({
			@Parameter(name = "empNum", description = "사원번호", example = "100000")
	})
	Header<UserVO> updateUser(@RequestBody UserVO userVO) {
		return userService.updateUser(userVO);
	}

	@DeleteMapping("/users/{empNum}")
	@Operation(summary = "사용자 삭제", description = "사용자 삭제 API")
	Header<String> deleteUser(@PathVariable(name = "empNum") Integer empNum) {
		return userService.deleteUser(empNum);
	}

	@PutMapping("/users/initPassword")
	@Operation(summary = "패스워드초기화", description = "패스워드 초기화 API")
	Header<String> initPassword(@RequestBody UserVO userVO){
		return userService.initPassword(userVO);
	}

	@GetMapping("/users/commonCode")
	@Operation(summary = "사용자 정보 관련 공통 코드 조회", description = "사용자 정보 관련 공통 코드 조회 API")
	Header<HashMap<String, Object>> getCommonCode(){
		return Header.OK(userService.getUserCommonCode(false, null));
	}

	@PostMapping("/users/upload/excel")
	@Operation(summary = "사용자 엑셀 업로드", description = "사용자 엑셀 업로드 API")
	public Header<UserVO> uploadDeviceExcel(@RequestParam("file") MultipartFile file) throws Exception {
		return userService.uploadUserExcel(file);
	}

}
