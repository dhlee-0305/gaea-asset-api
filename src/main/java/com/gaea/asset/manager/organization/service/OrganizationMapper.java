package com.gaea.asset.manager.organization.service;

import com.gaea.asset.manager.organization.vo.OrganizationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrganizationMapper {

    // 부서 목록 조회
    List<OrganizationVO> selectOrganizationList();

    // 부서 상세 조회
    OrganizationVO selectOrganization(@Param("orgId") Integer orgId);

    // 부서 등록
    int insertOrganization(OrganizationVO vo);

    // 부서 정보 수정
    int updateOrganization(OrganizationVO vo);

    // 하위부서 중 ORG_ID 최고값 조회
    Integer selectMaxOrgIdByParent(@Param("parentOrgId") Integer parentOrgId);

    // 부서 활성/비활성 처리
    int updateIsActive(@Param("orgId") Integer orgId, @Param("isActive") String isActive);

    // ORG_TYPE별 ORG_ID 최고값 조회
    Integer selectMaxOrgIdByType(@Param("orgType") String orgType);

    // 특정 조직 및 하위 조직 전체 orgId 조회 
    List<Integer> selectAllChildOrgIds(@Param("orgId") Integer orgId);

}
