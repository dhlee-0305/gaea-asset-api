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

import com.gaea.asset.manager.device.service.DeviceService;
import com.gaea.asset.manager.device.vo.DeviceVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "전산 장비 관리 관리자 API", description = "전산 장비 관리 관리자 API 입니다.")
@RequiredArgsConstructor
public class DeviceController {
	private final DeviceService deviceService;

	@GetMapping("/devices")
	@Operation(summary = "전산 장비 목록 조회", description = "전산 장비 목록 조회 API")
	Header<List<DeviceVO>> getDeviceList(@RequestParam(value="currentPage", defaultValue = "1") int currentPage, @RequestParam(value="pageSize", defaultValue = "10") int pageSize, Search search) {
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
	@Operation(summary = "전산 장비 정보 수정", description = "전산 장비 수정 요청 API")
	Header<DeviceVO> updateDevice(@RequestBody DeviceVO deviceVO, @RequestParam("userRoleCode") String userRoleCode) {
		return deviceService.updateDevice(deviceVO, userRoleCode);
	}

	@PostMapping("/devices/{deviceNum}/approval")
	@Operation(summary = "전산 장비 승인", description = "전산 장비 승인 처리 API")
	public Header<DeviceVO> approveDeviceUpdate(@RequestBody DeviceVO deviceVO, @RequestParam("userRoleCode") String userRoleCode) {
		return deviceService.approveDeviceUpdate(deviceVO, userRoleCode);
	}

	@PostMapping("/devices/{deviceNum}/rejection")
	@Operation(summary = "전산 장비 반려", description = "전산 장비 반려 처리 API")
	public Header<DeviceVO> rejectDeviceUpdate(@RequestBody DeviceVO deviceVO, @RequestParam("userRoleCode") String userRoleCode) {
		return deviceService.rejectDeviceUpdate(deviceVO, userRoleCode);
	}

	@DeleteMapping("/devices/{deviceNum}")
	@Operation(summary = "전산 장비 삭제", description = "전산 장비 삭제 API")
	Header<String> deleteDevice(@PathVariable(name="deviceNum") Integer deviceNum) {
		return deviceService.deleteDevice(deviceNum);
	}
}
