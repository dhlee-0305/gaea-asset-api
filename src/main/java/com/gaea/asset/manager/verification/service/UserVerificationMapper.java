package com.gaea.asset.manager.verification.service;

import org.apache.ibatis.annotations.Mapper;

import com.gaea.asset.manager.verification.vo.UserVerificationVO;

@Mapper
public interface UserVerificationMapper {
	UserVerificationVO getLatestValidCode(UserVerificationVO entity);
	int insertVerificationCode(UserVerificationVO entity);
	int updateVerified(UserVerificationVO entity);
}
