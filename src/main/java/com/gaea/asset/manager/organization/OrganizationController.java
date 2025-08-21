package com.gaea.asset.manager.organization;

import com.gaea.asset.manager.organization.service.OrganizationService;
import com.gaea.asset.manager.organization.vo.OrganizationVO;
import com.gaea.asset.manager.util.Header;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "부서 관리 API", description = "부서 관리 관련 API 입니다.")
@RestController
@RequestMapping("/organization")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    /**
     * 부서 목록 조회
     */
    @GetMapping
    @Operation(summary = "부서 목록 조회", description = "활성화된 모든 부서 목록을 조회합니다.")
    public Header<List<OrganizationVO>> getOrganizationList() {
        return organizationService.getOrganizationList();
    }

    /**
     * 부서 상세 조회
     */
    @GetMapping("/{orgId}")
    @Operation(summary = "부서 상세 조회", description = "특정 부서의 상세 정보를 조회합니다.")
    public Header<OrganizationVO> getOrganization(@PathVariable("orgId") Integer orgId) {
        return organizationService.getOrganization(orgId);
    }

    /**
     * 부서 등록
     */
    @PostMapping
    @Operation(summary = "부서 등록", description = "새로운 부서를 등록합니다.")
    public Header<String> createOrganization(@RequestBody OrganizationVO vo) {
        return organizationService.createOrganization(vo);
    }

    /**
     * 부서 정보 수정
     */
    @PutMapping("/{orgId}")
    @Operation(summary = "부서 정보 수정", description = "기존 부서 정보를 수정합니다.")
    public Header<String> updateOrganization(@PathVariable("orgId") Integer orgId, @RequestBody OrganizationVO vo) {
        vo.setOrgId(orgId);
        return organizationService.updateOrganization(vo);
    }

    /**
     * 하위부서 등록
     */
    @PostMapping("/{parentOrgId}/child")
    @Operation(summary = "하위부서 등록", description = "선택한 부서의 하위부서를 등록합니다.")
    public Header<String> createChildOrganization(
            @PathVariable("parentOrgId") Integer parentOrgId,
            @RequestBody OrganizationVO vo
    ) {
        return organizationService.createChildOrganization(parentOrgId, vo);
    }

    /**
     * 부서 및 하위부서 비활성화
     */
    @PutMapping("/{orgId}/inactive")
    @Operation(summary = "부서 비활성화", description = "부서, 하위부서 모두 비활성화 처리합니다.")
    public Header<String> deactivateOrganization(@PathVariable("orgId") Integer orgId) {
        return organizationService.deactivateOrganizationWithChildren(orgId);
    }
}
