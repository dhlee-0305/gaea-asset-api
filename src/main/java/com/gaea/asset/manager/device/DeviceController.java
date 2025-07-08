package com.gaea.asset.manager.device;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaea.asset.manager.device.service.DeviceService;
import com.gaea.asset.manager.device.vo.DeviceVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "전산 장비 관리 API", description = "전산 장비 관리 API 입니다.")
public class DeviceController {
	private final DeviceService deviceService;
	
	public DeviceController(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	@GetMapping("/deviceList")
	@Operation(summary = "전산 장비 목록 조회", description = "전산 장비 목록 조회 API")
	Header<List<DeviceVO>> getDeviceList(@RequestParam(value="page", defaultValue = "0") int page, @RequestParam(value="size", defaultValue = "10") int size, Search search) {
		return deviceService.getDeviceList(page, size, search);
	}

	@GetMapping("/getDeviceInfo/{deviceNumber}")
	@Parameters({
		@Parameter(name = "deviceNumber", description = "전산장비 번호", example = "1")
	})
	@Operation(summary = "전산 장비 상세 조회", description = "전산 장비 상세 조회 API")
	Header<DeviceVO> getDeviceInfo(@PathVariable(name="deviceNumber") Long deviceNumber) {
		return deviceService.getDeviceInfo(deviceNumber);
	}

	@PostMapping("/insertDevice")
	@Operation(summary = "전산 장비 등록", description = "전산 장비 등록 API")
	Header<DeviceVO> insertDevice(@RequestBody DeviceVO DeviceVO) {
		return deviceService.insertDevice(DeviceVO);
	}

	@PatchMapping("/updateDevice")
	@Operation(summary = "전산 장비 정보 수정", description = "전산 장비 정보 수정 API")
	Header<DeviceVO> updateDevice(@RequestBody DeviceVO DeviceVO) {
		return deviceService.updateDevice(DeviceVO);
	}

	@DeleteMapping("/deleteDevice/{deviceNumber}")
	@Operation(summary = "전산 장비 삭제", description = "전산 장비 삭제 API")
	@Parameters({
		@Parameter(name = "deviceNumber", description = "전산장비 번호", example = "15")
	})
	Header<String> deleteDevice(@PathVariable(name="deviceNumber") Long deviceNumber) {
		return deviceService.deleteDevice(deviceNumber);
	}
}
