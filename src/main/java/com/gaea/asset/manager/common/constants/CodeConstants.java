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

	// 장비 유형 코드
	public static final String DEVICE_TYPE_COMPUTER = "PC";
	public static final String DEVICE_TYPE_MONITOR = "MO";
	public static final String DEVICE_TYPE_PHONE = "HP";
	public static final String DEVICE_TYPE_ETC = "ETC";

	// 메세지 상태 코드
	public static final String MESSAGE_DEVICE_ASSIGNED = "A1";				// 장비 할당
	public static final String MESSAGE_STATUS_CHANGE_REQUESTED = "A2";		// 장비 상태 변경 요청
	public static final String MESSAGE_STATUS_CHANGE_APPROVED = "A3";		// 장비 상태 변경 승인
	public static final String MESSAGE_STATUS_CHANGE_REJECTED = "A4";		// 장비 상태 변경 반려
}
