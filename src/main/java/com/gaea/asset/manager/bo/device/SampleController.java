package com.gaea.asset.manager.bo.device;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaea.asset.manager.bo.device.service.SampleService;
import com.gaea.asset.manager.bo.device.vo.SampleVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "전산 장비 관리 관리자 API", description = "전산 장비 관리 관리자 API 입니다.")
public class SampleController {
	private final SampleService sampleService;
	
	public SampleController(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	@GetMapping("/bo/deviceList")
	@Operation(summary = "전산 장비 목록 조회[BO]", description = "전산 장비 목록 조회 API")
	Header<List<SampleVO>> getDeviceList(@RequestParam(value="page", defaultValue = "0") int page, @RequestParam(value="size", defaultValue = "10") int size, Search search) {
		return sampleService.getDeviceList(page, size, search);
	}

	@GetMapping("/bo/getDeviceInfo/{deviceNumber}")
	@Operation(summary = "전산 장비 상세 조회[BO]", description = "전산 장비 상세 조회 API")
	Header<SampleVO> getDeviceInfo(@PathVariable(name="deviceNumber") Long deviceNumber) {
		return sampleService.getDeviceInfo(deviceNumber);
	}

	@PostMapping("/bo/insertDevice")
	@Operation(summary = "전산 장비 등록[BO]", description = "전산 장비 등록 API")
	Header<SampleVO> insertDevice(@RequestBody SampleVO SampleVO) {
		return sampleService.insertDevice(SampleVO);
	}

	@PatchMapping("/bo/updateDevice")
	@Operation(summary = "전산 장비 정보 수정[BO]", description = "전산 장비 정보 수정 API")
	Header<SampleVO> updateDevice(@RequestBody SampleVO SampleVO) {
		return sampleService.updateDevice(SampleVO);
	}

	@DeleteMapping("/bo/deleteDevice/{deviceNumber}")
	@Operation(summary = "전산 장비 삭제[BO]", description = "전산 장비 삭제 API")
	Header<String> deleteDevice(@PathVariable(name="deviceNumber") Long deviceNumber) {
		return sampleService.deleteDevice(deviceNumber);
	}
}
