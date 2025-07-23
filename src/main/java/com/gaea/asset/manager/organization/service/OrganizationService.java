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

    public List<OrganizationVO> getOrganizationList() {
        return organizationMapper.selectOrganizationList();
    }

    public OrganizationVO getOrganization(Integer orgId) {
        return organizationMapper.selectOrganization(orgId);
    }

    @Transactional
    public int createOrganization(OrganizationVO vo) {
        // ORG_TYPE이 COMPANY인 조직 중 ORG_ID 최고값 조회
        Integer maxOrgId = organizationMapper.selectMaxOrgIdByType("COMPANY");
        int newOrgId = (maxOrgId == null ? 0 : maxOrgId) + 1;

        vo.setOrgId(newOrgId);
        vo.setSortOrder(newOrgId);
        vo.setOrgType("COMPANY");
        vo.setParentOrgId(null);
        vo.setOrgLevel(1);
        vo.setOrgPath("1");

        return organizationMapper.insertOrganization(vo);
    }

    @Transactional
    public int updateOrganization(OrganizationVO vo) {
        return organizationMapper.updateOrganization(vo);
    }

    @Transactional
    public int batchInsertOrganizations(List<OrganizationVO> list) {
        return organizationMapper.insertOrganizations(list);
    }

    @Transactional
    public int createChildOrganization(Integer parentOrgId, OrganizationVO vo) {
        // 하위부서 중 ORG_ID 최고값 조회
        int maxOrgId = organizationMapper.selectMaxOrgIdByParent(parentOrgId);
        int newOrgId = maxOrgId + 1;
        vo.setOrgId(newOrgId);
        vo.setParentOrgId(parentOrgId);
        return organizationMapper.insertOrganization(vo);
    }


    /**
     * orgId 및 모든 하위 조직을 비활성화 처리
     */
    @Transactional
    public void deactivateOrganizationWithChildren(Integer orgId) {
        // 하위 조직 포함 전체 orgId 조회
        List<Integer> allOrgIds = organizationMapper.selectAllChildOrgIds(orgId);
        for (Integer id : allOrgIds) {
            organizationMapper.updateIsActive(id, "N");
        }
    }

    @Transactional
    public void createAndUpdateChildOrganization(Integer parentOrgId, OrganizationVO vo) {
        OrganizationVO parentOrg = getOrganization(parentOrgId);

        String childOrgType;
        int childOrgLevel;
      
        if ("COMPANY".equals(parentOrg.getOrgType())) {
            childOrgType = "DIVISION";
            childOrgLevel = 2;
        } else if ("DIVISION".equals(parentOrg.getOrgType())) {
            childOrgType = "TEAM";
            childOrgLevel = 3;
        } else {
            throw new IllegalArgumentException("TEAM 하위에는 조직을 생성할 수 없습니다.");
        }
        vo.setOrgType(childOrgType);
        vo.setOrgLevel(childOrgLevel);
        vo.setOrgPath(parentOrg.getOrgPath() + "/" + childOrgLevel);
        
        // 신규 org_id 채번 및 등록
        createChildOrganization(parentOrgId, vo);

        String parentStr = String.valueOf(parentOrgId);
        String childStr = String.valueOf(vo.getOrgId());
        String sortOrderStr = childStr.startsWith(parentStr) ? childStr.substring(parentStr.length()) : childStr;
        vo.setSortOrder(Integer.parseInt(sortOrderStr));

        // org_id, org_path, sort_order가 모두 세팅된 vo로 최종 update
        updateOrganization(vo);
    }
}
