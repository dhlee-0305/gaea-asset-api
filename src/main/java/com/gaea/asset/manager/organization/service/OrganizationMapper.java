package com.gaea.asset.manager.organization.service;

import com.gaea.asset.manager.organization.vo.OrganizationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrganizationMapper {
    List<OrganizationVO> selectOrganizationList();
    OrganizationVO selectOrganization(@Param("orgId") Integer orgId);
    int insertOrganization(OrganizationVO vo);
    int updateOrganization(OrganizationVO vo);
    int insertOrganizations(@Param("list") List<OrganizationVO> list); 
    Integer selectMaxOrgIdByParent(@Param("parentOrgId") Integer parentOrgId);
    int updateIsActive(@Param("orgId") Integer orgId, @Param("isActive") String isActive);
    Integer selectMaxOrgIdByType(@Param("orgType") String orgType);
    List<Integer> selectAllChildOrgIds(@Param("orgId") Integer orgId);
}
