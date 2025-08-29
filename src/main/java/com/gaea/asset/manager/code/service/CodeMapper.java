package com.gaea.asset.manager.code.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gaea.asset.manager.code.vo.CodeVO;

@Mapper
public interface CodeMapper {
	List<CodeVO> getCodeList(CodeVO entity);
	int insertCode(CodeVO entity);
	int updateCode(CodeVO entity);
	int updateUseYn(CodeVO entity);
	List<CodeVO> getCodeListByCodes(@Param("codes") List<String> codes);
	List<CodeVO> getCategoryList();
}
