package com.gaea.asset.manager.user;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaea.asset.manager.user.service.UserService;
import com.gaea.asset.manager.user.vo.UserVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "사용자 관리 관리자 API", description = "사용자 관리 관리자 API 입니다.")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/userList")
	@Operation(summary = "사용자 목록 조회", description = "사용자 목록 조회 API")
	Header<List<UserVO>> getUserList(@RequestParam(value="currentPage", required = false) Integer currentPage, @RequestParam(value="pageSize", required = false) Integer pageSize, Search search) {
		return userService.getUserList(currentPage, pageSize, search);
	}

}
