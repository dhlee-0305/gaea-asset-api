package com.gaea.asset.manager.device;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gaea.asset.manager.device.service.DeviceService;
import com.gaea.asset.manager.device.vo.DeviceHistoryVO;
import com.gaea.asset.manager.device.vo.DeviceVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "전산 장비 관리 관리자 API", description = "전산 장비 관리 관리자 API 입니다.")
@RequiredArgsConstructor
public class DeviceController {
	private final DeviceService deviceService;

	@GetMapping("/devices")
	@Operation(summary = "전산 장비 목록 조회", description = "전산 장비 목록 조회 API")
	Header<List<DeviceVO>> getDeviceList(@RequestParam(value="currentPage", defaultValue = "1") int currentPage,
			@RequestParam(value="pageSize", defaultValue = "10") int pageSize,
			Search search) {

		return deviceService.getDeviceList(currentPage, pageSize, search);
	}

	@GetMapping("/devices/{deviceNum}")
	@Operation(summary = "전산 장비 상세 조회", description = "전산 장비 상세 조회 API")
	Header<DeviceVO> getDevice(@PathVariable(name="deviceNum") Integer deviceNum) {
		return deviceService.getDevice(deviceNum);
	}

	@GetMapping("/devices/{deviceNum}/draft")
	@Operation(summary = "전산 장비 변경 정보 조회", description = "전산 장비 수정 요청 정보 조회 API")
	Header<DeviceVO> getDeviceTemp(@PathVariable(name="deviceNum") Integer deviceNum) {
		return deviceService.getDeviceTemp(deviceNum);
	}

	@PostMapping("/devices")
	@Operation(summary = "전산 장비 등록", description = "전산 장비 등록 API")
	Header<DeviceVO> insertDevice(@RequestBody DeviceVO deviceVO) {
		return deviceService.insertDevice(deviceVO);
	}

	@PutMapping("/devices/{deviceNum}")
	@Operation(summary = "전산 장비 수정", description = "전산 장비 수정 요청 API")
	Header<DeviceVO> updateDevice(@RequestBody DeviceVO deviceVO) {
		return deviceService.updateDevice(deviceVO);
	}

	@PostMapping("/devices/{deviceNum}/approval")
	@Operation(summary = "전산 장비 승인", description = "전산 장비 승인 처리 API")
	public Header<DeviceVO> approveDeviceUpdate(@RequestBody DeviceVO deviceVO) {
		return deviceService.approveDeviceUpdate(deviceVO);
	}

	@PostMapping("/devices/{deviceNum}/rejection")
	@Operation(summary = "전산 장비 반려", description = "전산 장비 반려 처리 API")
	public Header<DeviceVO> rejectDeviceUpdate(@RequestBody DeviceVO deviceVO) {
		return deviceService.rejectDeviceUpdate(deviceVO);
	}

	@DeleteMapping("/devices/{deviceNum}")
	@Operation(summary = "전산 장비 삭제", description = "전산 장비 삭제 API")
	Header<String> deleteDevice(@PathVariable(name="deviceNum") Integer deviceNum) {
		return deviceService.deleteDevice(deviceNum);
	}

	@GetMapping("/histories")
	@Operation(summary = "전산 장비 이력 목록 조회", description = "전산 장비 이력 목록 조회 API (검색 조건에 따라 필터링 가능)")
	public Header<List<DeviceHistoryVO>> getDeviceHistoryList(
			@RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
			Search search) {
		return deviceService.getDeviceHistoryList(currentPage, pageSize, search);
	}

	@GetMapping("/histories/{historyNum}")
	@Operation(summary = "전산 장비 이력 상세 조회", description = "전산 장비 이력 상세 조회 API")
	public Header<DeviceHistoryVO> getDeviceHistory(@PathVariable(name = "historyNum") Integer historyNum) {
		return deviceService.getDeviceHistory(historyNum);
	}

	@GetMapping("/devices/pending")
	@Operation(summary = "전산 장비 승인 요청 조회", description = "전산 장비 승인 요청 건 조회 API")
	public Header<List<DeviceVO>> getDevicePendingList(
			@RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
			@RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
		return deviceService.getDevicePendingList(currentPage, pageSize);
	}

	@GetMapping("/devices/download/excel")
	@Operation(summary = "전산 장비 엑셀 다운로드", description = "전산 장비 엑셀 다운로드 API")
	public void downloadDeviceExcel(HttpServletResponse response) {
		deviceService.downloadDeviceExcel(response);
	}
	
	@PostMapping("/devices/upload/excel")
	@Operation(summary = "전산 장비 엑셀 업로드", description = "전산 장비 엑셀 업로드 API")
	public Header<DeviceVO> uploadDeviceExcel(@RequestParam("file") MultipartFile file) throws Exception {
		return deviceService.uploadDeviceExcel(file);
	}
}
