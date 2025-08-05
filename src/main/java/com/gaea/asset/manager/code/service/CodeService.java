package com.gaea.asset.manager.code.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gaea.asset.manager.code.vo.CodeVO;
import com.gaea.asset.manager.util.Header;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeService {
	private final CodeMapper codeMapper;

	public Header<List<CodeVO>> getCodeList() {
		List<CodeVO> codeList = codeMapper.getCodeList();
		
		return Header.OK(codeList);
	}

	public Header<CodeVO> insertCode(CodeVO codeVO) {
		if (codeMapper.insertCode(codeVO) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}

	@Transactional
	public Header<CodeVO> updateCode(CodeVO codeVO) {
		if (codeMapper.updateCode(codeVO) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("9999", "ERROR");
		}
    }

	public Header<String> updateUseYn(String category, String code) {
		CodeVO codeVO = new CodeVO();
		codeVO.setCategory(category);
		codeVO.setCode(code);
		
		if (codeMapper.updateUseYn(codeVO) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}
}
