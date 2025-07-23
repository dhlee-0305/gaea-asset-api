package com.gaea.asset.manager.organization.service;

import com.gaea.asset.manager.organization.vo.OrganizationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationMapper organizationMapper;

    // 부서 목록 조회 
    public List<OrganizationVO> getOrganizationList() {
        return organizationMapper.selectOrganizationList();
    }

    // 부서 상세 조회 
    public OrganizationVO getOrganization(Integer orgId) {
        return organizationMapper.selectOrganization(orgId);
    }

    // 신규 부서(최상위 COMPANY) 등록
    public int createOrganization(OrganizationVO vo) {
        int newOrgId = (organizationMapper.selectMaxOrgIdByType("COMPANY") == null ? 0 : organizationMapper.selectMaxOrgIdByType("COMPANY")) + 1;
        vo.setOrgId(newOrgId);
        vo.setSortOrder(newOrgId);
        vo.setOrgType("COMPANY");
        vo.setParentOrgId(null);
        vo.setOrgLevel(1);
        vo.setOrgPath("1");
        return organizationMapper.insertOrganization(vo);
    }

    // 부서 정보 수정
    public int updateOrganization(OrganizationVO vo) {
        return organizationMapper.updateOrganization(vo);
    }

    // 부서 일괄 등록
    public int batchInsertOrganizations(List<OrganizationVO> list) {
        return organizationMapper.insertOrganizations(list);
    }

    // 하위부서 신규 등록
    public int createChildOrganization(Integer parentOrgId, OrganizationVO vo) {
        int newOrgId = organizationMapper.selectMaxOrgIdByParent(parentOrgId) + 1;
        vo.setOrgId(newOrgId);
        vo.setParentOrgId(parentOrgId);
        return organizationMapper.insertOrganization(vo);
    }

    // orgId 및 모든 하위 조직 비활성화 처리
    @Transactional
    public void deactivateOrganizationWithChildren(Integer orgId) {
        organizationMapper.selectAllChildOrgIds(orgId)
                .forEach(id -> organizationMapper.updateIsActive(id, "N"));
    }

    // 하위부서 생성 및 정보 갱신
    @Transactional
    public void createAndUpdateChildOrganization(Integer parentOrgId, OrganizationVO vo) {
        OrganizationVO parentOrg = getOrganization(parentOrgId);
        if ("COMPANY".equals(parentOrg.getOrgType())) {
            vo.setOrgType("DIVISION");
            vo.setOrgLevel(2);
        } else if ("DIVISION".equals(parentOrg.getOrgType())) {
            vo.setOrgType("TEAM");
            vo.setOrgLevel(3);
        } else {
            throw new IllegalArgumentException("TEAM 하위에는 부서를 생성할 수 없습니다.");
        }
        vo.setOrgPath(parentOrg.getOrgPath() + "/" + vo.getOrgLevel());
        createChildOrganization(parentOrgId, vo);
        // sortOrder 계산 및 update
        String parentStr = String.valueOf(parentOrgId);
        String childStr = String.valueOf(vo.getOrgId());
        vo.setSortOrder(childStr.startsWith(parentStr) ? Integer.parseInt(childStr.substring(parentStr.length())) : Integer.parseInt(childStr));
        updateOrganization(vo);
    }
}
