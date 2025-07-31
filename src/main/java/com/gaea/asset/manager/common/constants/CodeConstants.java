package com.gaea.asset.manager.common.constants;

public class CodeConstants {
	// 권한
	public static final String ROLE_USER = "00";							// User
	public static final String ROLE_TEAM_MANAGER = "01";			// Team-Manager
	public static final String ROLE_ASSET_MANAGER = "02";		// Asset-Manager
	public static final String ROLE_SYSTEM_MANAGER = "03";		// System-Manager

	// 결재 상태 코드
	public static final String APPROVAL_STATUS_TEAM_MANAGER_PENDING = "A1";		// 부서장 승인대기
	public static final String APPROVAL_STATUS_ADMIN_PENDING = "A2";						// 관리자 승인대기
	public static final String APPROVAL_STATUS_APPROVED = "A3";								// 승인완료
	public static final String APPROVAL_STATUS_REJECTED = "A4";									// 반려

}
