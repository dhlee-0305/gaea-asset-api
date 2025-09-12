package com.gaea.asset.manager.common.constants;

public class CodeConstants {
	public static final String CATEGORY_POSITION = "C001";			// 직책
	public static final String CATEGORY_GRADE = "C002";				// 직위
	public static final String CATEGORY_DEVICE_TYPE = "C004";		// 장비 유형
	public static final String CATEGORY_DEVICE_STATUS = "C005";		// 장비 상태
	public static final String CATEGORY_APPROVAL_STATUS = "C006";	// 결재 상태
	public static final String CATEGORY_USAGE_DIVISION = "C007";	// 용도구분
	// 권한
	public static final String ROLE_USER = "00";					// User
	public static final String ROLE_TEAM_MANAGER = "01";			// Team-Manager
	public static final String ROLE_ASSET_MANAGER = "02";			// Asset-Manager
	public static final String ROLE_SYSTEM_MANAGER = "03";			// System-Manager

	// 결재 상태 코드
	public static final String APPROVAL_STATUS_TEAM_MANAGER_PENDING = "A1";		// 부서장 승인대기
	public static final String APPROVAL_STATUS_ADMIN_PENDING = "A2";			// 관리자 승인대기
	public static final String APPROVAL_STATUS_APPROVED = "A3";					// 승인완료
	public static final String APPROVAL_STATUS_REJECTED = "A4";					// 반려

	// 장비 유형 코드
	public static final String DEVICE_TYPE_COMPUTER = "PC";
	public static final String DEVICE_TYPE_MONITOR = "MO";
	public static final String DEVICE_TYPE_PHONE = "HP";
	public static final String DEVICE_TYPE_ETC = "ETC";

	// 메세지 상태 코드
	public static final String MESSAGE_DEVICE_ASSIGNED = "M1";				// 장비 할당
	public static final String MESSAGE_DEVICE_CHANGE_REQUESTED = "M2";		// 장비 상태 변경 요청
	public static final String MESSAGE_DEVICE_CHANGE_APPROVED = "M3";		// 장비 상태 변경 승인
	public static final String MESSAGE_DEVICE_CHANGE_REJECTED = "M4";		// 장비 상태 변경 반려

	// 파일 유형 코드
	public static final String POST_TYPE_NOTICE = "NO";

	// 직책 코드
	public static final String TEAM_LEADER = "03";		// 팀장
	public static final String TEAM_MEMBER = "04";		// 팀원

	// 직위 코드
	public static final String MANAGER = "03";				// 부장
	public static final String CONDUCTOR = "04";			// 차장
	public static final String EXAGGERATION = "05";			// 과장
	public static final String Assistant_Manager = "06";	// 대리
	public static final String EMPLOYEE = "07";				// 사원
}
