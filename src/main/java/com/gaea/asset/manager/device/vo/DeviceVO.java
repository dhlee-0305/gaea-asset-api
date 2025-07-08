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
	@Schema(description = "전산장비 번호", example = "1")
	private Long deviceNumber;
	
	@Schema(description = "전산장비 이름", example = "테스트 장비")
	private String deviceName;
	
	@Schema(description = "전산장비 설명", example = "테스트 설명")
	private String contents;
	
	@Schema(description = "등록 사용자", example = "admin")
	private String regUser;
	
	@Schema(description = "수정 사용자", example = "admin")
	private String modUser;
}
