package com.gaea.asset.manager.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Search {
	// 검색 항목
	private String searchColumn;
	// 검색어
	private String searchKeyword;

	// 장비유형
	private String deviceType;
}
