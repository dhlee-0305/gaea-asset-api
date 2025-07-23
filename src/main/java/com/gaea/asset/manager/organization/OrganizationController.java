package com.gaea.asset.manager.organization;

import com.gaea.asset.manager.organization.service.OrganizationService;
import com.gaea.asset.manager.organization.vo.OrganizationVO;
import com.gaea.asset.manager.util.Header;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;

//import java.io.InputStream;
import java.util.List;

//import com.gaea.asset.manager.util.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "부서 관리 API", description = "부서 관리 관련 API 입니다.")
@RestController
@RequestMapping("/organization")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    @GetMapping
    @Operation(summary = "부서 목록 조회", description = "활성화된 모든 부서 목록을 조회합니다.")
    public Header<List<OrganizationVO>> getOrganizationList() {
        return Header.OK(organizationService.getOrganizationList());
    }

    @GetMapping("/{orgId}")
    @Operation(summary = "부서 상세 조회", description = "특정 부서의 상세 정보를 조회합니다.")
    public Header<OrganizationVO> getOrganization(@PathVariable("orgId") Integer orgId) {
        return Header.OK(organizationService.getOrganization(orgId));
    }

    @PostMapping
    @Operation(summary = "부서 등록", description = "새로운 부서를 등록합니다.")
    public Header<String> createOrganization(@RequestBody OrganizationVO vo) {
        organizationService.createOrganization(vo);
        return Header.OK("등록되었습니다.");
    }

    @PutMapping("/{orgId}")
    @Operation(summary = "부서 정보 수정", description = "기존 부서 정보를 수정합니다.")
    public Header<String> updateOrganization(@PathVariable("orgId") Integer orgId, @RequestBody OrganizationVO vo) {
        vo.setOrgId(orgId);
        organizationService.updateOrganization(vo);
        return Header.OK("수정되었습니다.");
    }
    /* 
    @PostMapping("/excel")
    @Operation(summary = "부서 엑셀 일괄등록", description = "엑셀 파일을 업로드하여 여러 부서를 한 번에 등록합니다.")
    public Header<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            List<OrganizationVO> list = ExcelUtil.parseOrganizationExcel(is);
            organizationService.batchInsertOrganizations(list);
            return Header.OK("엑셀 일괄등록 완료");
        } catch (Exception e) {
            return Header.ERROR("500", "엑셀 업로드 실패: " + e.getMessage());
        }
    }
    */
    @PostMapping("/{parentOrgId}/child")
    @Operation(summary = "하위부서 등록", description = "선택한 부서의 하위부서를 등록합니다.")
    public Header<String> createChildOrganization(
            @PathVariable("parentOrgId") Integer parentOrgId,
            @RequestBody OrganizationVO vo
    ) {
        try {
            organizationService.createAndUpdateChildOrganization(parentOrgId, vo);
            return Header.OK("하위부서가 등록되었습니다.");
        } catch (IllegalArgumentException e) {
            return Header.ERROR("400", e.getMessage());
        }
    }

    @PutMapping("/{orgId}/inactive")
    @Operation(summary = "부서 비활성화", description = "부서, 하위부서 모두 비활성화 처리합니다.")
    public Header<String> deactivateOrganization(@PathVariable("orgId") Integer orgId) {
        organizationService.deactivateOrganizationWithChildren(orgId);
        return Header.OK("부서 및 하위 부서가 모두 비활성화되었습니다.");
    }
}
