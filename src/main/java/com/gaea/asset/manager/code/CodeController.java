package com.gaea.asset.manager.code;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaea.asset.manager.code.service.CodeService;
import com.gaea.asset.manager.code.vo.CodeVO;
import com.gaea.asset.manager.util.Header;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "공통 코드 관리 API", description = "공통 코드 관리 API 입니다.")
@RequiredArgsConstructor
public class CodeController {
	private final CodeService codeService;

	@GetMapping("/codes")
	@Operation(summary = "공통 코드 목록 조회", description = "공통 코드 목록 조회 API")
	Header<List<CodeVO>> getCodeList(@RequestParam(value="category", defaultValue = "") String category) {
		return codeService.getCodeList(category);
	}

	@PostMapping("/codes")
	@Operation(summary = "공통 코드 등록", description = "공통 코드 등록 API")
	Header<CodeVO> insertCode(@RequestBody CodeVO codeVO) {
		return codeService.insertCode(codeVO);
	}

	@PutMapping("/codes")
	@Operation(summary = "공통 코드 수정", description = "공통 코드 수정 API")
	Header<CodeVO> updateCode(@RequestBody CodeVO codeVO) {
		return codeService.updateCode(codeVO);
	}

	@DeleteMapping("/codes/{category}/{code}")
	@Operation(summary = "공통 코드 삭제", description = "공통 코드비 삭제 API")
	@Parameters({
		@Parameter(name = "category", description = "그룹 코드", example = "C001"),
		@Parameter(name = "code", description = "코드", example = "01")
	})
	Header<String> updateUseYn(@PathVariable(name="category") String category, @PathVariable(name="code") String code) {
		return codeService.updateUseYn(category, code);
	}
	
	@GetMapping("/categories")
	@Operation(summary = "공통 카테고리 목록 조회", description = "공통 카테고리 목록 조회 API")
	Header<List<CodeVO>> getCategoryList() {
		return codeService.getCategoryList();
	}

	@GetMapping("/codesByCategories")
	@Operation(summary = "공통 코드 목록 조회", description = "공통 코드 목록 조회 API")
	Header<HashMap<String, Object>> getCodeListByCodes(@RequestParam(value="categoryList", defaultValue = "") List<String> categoryList) {
		return codeService.getCodeListByCodes(categoryList);
	}
}
