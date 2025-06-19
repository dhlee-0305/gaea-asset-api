package com.gaea.asset.manager.util;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Header<T> {
	private LocalDateTime transactionTime;
	private String resultCode;
	private String description;
	private T data;
	private Pagination pagination;

	@SuppressWarnings("unchecked")
	public static <T> Header<T> OK() {
		return (Header<T>) Header.builder()
				.transactionTime(LocalDateTime.now())
				.resultCode("0000")
				.description("OK")
				.build();
	}

	//DATA OK
	@SuppressWarnings("unchecked")
	public static <T> Header<T> OK(T data) {
		return (Header<T>) Header.builder()
				.transactionTime(LocalDateTime.now())
				.resultCode("0000")
				.description("OK")
				.data(data)
				.build();
	}

	@SuppressWarnings("unchecked")
	public static <T> Header<T> OK(T data, Pagination pagination) {
		return (Header<T>) Header.builder()
				.transactionTime(LocalDateTime.now())
				.resultCode("0000")
				.description("OK")
				.data(data)
				.pagination(pagination)
				.build();
	}

	@SuppressWarnings("unchecked")
	public static <T> Header<T> ERROR(String resultCode, String description) {
		return (Header<T>) Header.builder()
				.transactionTime(LocalDateTime.now())
				.resultCode(resultCode)
				.description(description)
				.build();
	}
}
