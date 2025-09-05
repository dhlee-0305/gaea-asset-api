package com.gaea.asset.manager.code.service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gaea.asset.manager.code.vo.CodeVO;
import com.gaea.asset.manager.util.Header;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeService {
	private final CodeMapper codeMapper;

	public Header<List<CodeVO>> getCodeList(String category) {
		CodeVO codeVO = new CodeVO();
		codeVO.setCategory(category);
		
		List<CodeVO> codeList = codeMapper.getCodeList(codeVO);
		
		return Header.OK("200", "", codeList);
	}

	@Transactional
	public Header<CodeVO> insertCode(CodeVO codeVO) {
		if (codeMapper.insertCode(codeVO) > 0) {
			return Header.OK("200", "", null);
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}

	@Transactional
	public Header<CodeVO> updateCode(CodeVO codeVO) {
		if (codeMapper.updateCode(codeVO) > 0) {
			return Header.OK("200", "", null);
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}

	@Transactional
	public Header<String> updateUseYn(String category, String code) {
		CodeVO codeVO = new CodeVO();
		codeVO.setCategory(category);
		codeVO.setCode(code);
		
		if (codeMapper.updateUseYn(codeVO) > 0) {
			return Header.OK("200", "", null);
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}
	
	public Header<List<CodeVO>> getCategoryList() {
		List<CodeVO> categoryList = codeMapper.getCategoryList();
		
		return Header.OK("200", "", categoryList);
	}

	public Header<HashMap<String, Object>> getCodeListByCodes(List<String> categoryList) {
		List<CodeVO> codeList = codeMapper.getCodeListByCodes(categoryList);
		int index = 0;
		HashMap<String, Object> resData = new HashMap<>();

		for(String category : categoryList){
			List<CodeVO> resultData = codeList.stream()
					.filter(code -> code.getCategory().equals(category))
					.collect(Collectors.toList());
			resData.put(category, resultData);
		}

		return Header.OK("0000", "", resData);
	}
}
