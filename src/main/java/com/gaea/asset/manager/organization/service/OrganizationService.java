package com.gaea.asset.manager.organization.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaea.asset.manager.common.constants.ResultCode;
import com.gaea.asset.manager.organization.vo.OrganizationVO;
import com.gaea.asset.manager.util.Header;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationMapper organizationMapper;

    private static final String TYPE_COMPANY = "COMPANY";
    private static final String TYPE_DIVISION = "DIVISION";
    private static final String TYPE_TEAM = "TEAM";
    private static final String STATUS_INACTIVE = "N";

    // 부서 목록 조회
    public Header<List<OrganizationVO>> getOrganizationList() {
        try {
            List<OrganizationVO> list = organizationMapper.selectOrganizationList();
            if (list == null || list.isEmpty()) {
                return Header.OK(ResultCode.NO_CONTENT, "등록된 부서정보가 없습니다.", list);
            }
            return Header.OK(list);
        } catch (Exception e) {
            return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "부서 목록 조회 중 오류가 발생했습니다: ");
        }
    }

    // 부서 상세 조회
    public Header<OrganizationVO> getOrganization(Integer orgId) {
        if (orgId == null) {
            return Header.ERROR(ResultCode.BAD_REQUEST, "필수 파라미터(orgId)가 없습니다.");
        }
        try {
            OrganizationVO vo = organizationMapper.selectOrganization(orgId);
            if (vo == null) {
                return Header.OK(ResultCode.NO_CONTENT, "해당 부서 정보가 없습니다.", null);
            }
            return Header.OK(vo);
        } catch (Exception e) {
            return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "부서 상세 조회 중 오류가 발생했습니다: ");
        }
    }

    // 신규등록 부서(최상위 COMPANY)
    public Header<String> createOrganization(OrganizationVO vo) {
        if (vo == null || vo.getOrgName() == null || vo.getOrgName().isBlank()) {
            return Header.ERROR(ResultCode.BAD_REQUEST, "필수 파라미터(orgName)가 없습니다.");
        }
        try {
            int newOrgId = (organizationMapper.selectMaxOrgIdByType("COMPANY") == null ? 0 : organizationMapper.selectMaxOrgIdByType("COMPANY")) + 1;
            vo.setOrgId(newOrgId);
            vo.setSortOrder(newOrgId);
            vo.setOrgType("COMPANY");
            vo.setParentOrgId(null);
            vo.setOrgLevel(1);
            int result = organizationMapper.insertOrganization(vo);
            if (result > 0) {
                return Header.OK(ResultCode.OK, "등록되었습니다.", null);
            } else {
                return Header.OK(ResultCode.NO_CONTENT, "부서 등록에 실패했습니다.", null);
            }
        } catch (Exception e) {
            return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "부서 등록 중 오류가 발생했습니다.");
        }
    }

    // 부서 정보 수정
    public Header<String> updateOrganization(OrganizationVO vo) {
        if (vo == null || vo.getOrgId() == null || vo.getOrgName() == null || vo.getOrgName().isBlank()) {
            return Header.ERROR(ResultCode.BAD_REQUEST, "필수 파라미터(orgId, orgName)가 없습니다.");
        }
        try {
            int result = organizationMapper.updateOrganization(vo);
            if (result > 0) {
                return Header.OK(ResultCode.OK, "수정되었습니다.", null);
            } else {
                return Header.OK(ResultCode.NO_CONTENT, "수정 대상 부서 정보가 없습니다.", null);
            }
        } catch (Exception e) {
            return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "부서 정보 수정 중 오류가 발생했습니다.");
        }
    }

    // 하위부서 신규 등록
    public Header<String> createChildOrganization(Integer parentOrgId, OrganizationVO vo) {
        // 파라미터 검증
        if (parentOrgId == null)
            return Header.ERROR(ResultCode.BAD_REQUEST, "필수 파라미터(parentOrgId)가 없습니다.");
        if (vo == null || vo.getOrgName() == null || vo.getOrgName().isBlank())
            return Header.ERROR(ResultCode.BAD_REQUEST, "필수 파라미터(orgName)가 없습니다.");

        try {
            // 상위 조직 정보 조회
            Header<OrganizationVO> parentResult = getOrganization(parentOrgId);
            OrganizationVO parent = parentResult.getData();
            if (parent == null)
                return Header.ERROR(ResultCode.NO_CONTENT, "상위 부서 정보가 존재하지 않습니다.");

            // 하위 조직 유형 및 레벨 결정
            String childType;
            int childLevel;
            switch (parent.getOrgType()) {
                case TYPE_COMPANY:
                    childType = TYPE_DIVISION;
                    childLevel = 2;
                    break;
                case TYPE_DIVISION:
                    childType = TYPE_TEAM;
                    childLevel = 3;
                    break;
                default:
                    return Header.ERROR(ResultCode.BAD_REQUEST, "TEAM 하위에는 부서를 생성할 수 없습니다.");
            }
            vo.setOrgType(childType);
            vo.setOrgLevel(childLevel);

            // 신규 orgId 및 sortOrder 계산
            int newOrgId = organizationMapper.selectMaxOrgIdByParent(parentOrgId) + 1;
            vo.setOrgId(newOrgId);
            vo.setParentOrgId(parentOrgId);
            String parentStr = String.valueOf(parentOrgId);
            String childStr = String.valueOf(newOrgId);
            int sortOrder = childStr.startsWith(parentStr) ? Integer.parseInt(childStr.substring(parentStr.length())) : Integer.parseInt(childStr);
            vo.setSortOrder(sortOrder);

            // DB 등록
            int insertResult = organizationMapper.insertOrganization(vo);
            if (insertResult > 0)
                return Header.OK(ResultCode.OK, "하위부서가 등록되었습니다.", null);
            else
                return Header.OK(ResultCode.NO_CONTENT, "하위부서 등록에 실패했습니다.", null);
        } catch (Exception e) {
            return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "하위부서 등록 중 오류가 발생했습니다");
        }
    }

    // orgId 및 모든 하위 조직 비활성화 처리
    @Transactional
    public Header<String> deactivateOrganizationWithChildren(Integer orgId) {
        if (orgId == null) {
            return Header.ERROR(ResultCode.BAD_REQUEST, "필수 파라미터(orgId)가 없습니다.");
        }
        try {
            List<Integer> allOrgIds = organizationMapper.selectAllChildOrgIds(orgId);
            if (allOrgIds == null || allOrgIds.isEmpty()) {
                return Header.OK(ResultCode.NO_CONTENT, "삭제 대상 부서가 없습니다.", null);
            }
            allOrgIds.forEach(id -> organizationMapper.updateIsActive(id, STATUS_INACTIVE));
            return Header.OK(ResultCode.OK, "부서 및 하위 부서가 모두 삭제 되었습니다.", null);
        } catch (Exception e) {
            return Header.ERROR(ResultCode.INTERNAL_SERVER_ERROR, "부서 삭제 처리 중 오류가 발생했습니다.");
        }
    }
}
