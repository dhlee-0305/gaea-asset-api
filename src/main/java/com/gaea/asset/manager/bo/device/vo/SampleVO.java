package com.gaea.asset.manager.bo.device.vo;

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
public class SampleVO {
	private Long deviceNumber;
	private String deviceName;
	private String contents;
	private String regUser;
	private String modUser;
}
