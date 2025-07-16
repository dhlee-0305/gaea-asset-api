package com.gaea.asset.manager.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "전산 장비 정보")
public class DeviceVO {

	@Schema(description = "장비순번", example = "1")
	private Integer deviceNum;

	@Schema(description = "사원번호", example = "100000")
	private Integer empNum;

	@Schema(description = "사용자명", example = "홍길동")
	private String userName;

	@Schema(description = "기존 장비 관리 번호", example = "IT-PC-100010")
	private String oldDeviceId;

	@Schema(description = "조직명", example = "IT서비스부문")
	private String orgName;

	@Schema(description = "용도구분", example = "")
	private String usageDivision;

	@Schema(description = "사용용도", example = "")
	private String usagePurpose;

	@Schema(description = "사용/보관 위치", example = "")
	private String archiveLocation;

	@Schema(description = "장비유형 코드", example = "")
	private String deviceTypeCode;

	@Schema(description = "장비유형", example = "")
	private String deviceType;

	@Schema(description = "제조사 코드", example = "")
	private String manufacturerCode;

	@Schema(description = "모델명", example = "")
	private String modelName;

	@Schema(description = "제조일자", example = "")
	private String manufactureDate;

	@Schema(description = "CPU 사용", example = "")
	private String cpuSpec;

	@Schema(description = "메모리", example = "")
	private Integer memorySize;

	@Schema(description = "스토리지 정보", example = "")
	private String storageInfo;

	@Schema(description = "운영체제", example = "")
	private String operatingSystem;

	@Schema(description = "화면크기", example = "")
	private Integer screenSize;

	@Schema(description = "GPU", example = "")
	private Integer gpuSpec;

	@Schema(description = "장비상태 코드", example = "")
	private String statusCode;

	@Schema(description = "장비상태", example = "")
	private String status;

	@Schema(description = "구매일자", example = "")
	private String purchaseDate;

	@Schema(description = "반납일자", example = "")
	private String returnDate;

	@Schema(description = "비고", example = "")
	private String remarks;

	@Schema(description = "생성일시", example = "")
	private String createDatetime;

	@Schema(description = "생성자", example = "")
	private String createUser;

	@Schema(description = "최종 변경일시", example = "")
	private String updateDatetime;

	@Schema(description = "최종 변경자", example = "")
	private String updateUser;
}
