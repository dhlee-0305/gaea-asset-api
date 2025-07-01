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

@RestController
public class SampleController {
	private final SampleService sampleService;
	
	public SampleController(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	@GetMapping("/bo/deviceList")
	Header<List<SampleVO>> getDeviceList(@RequestParam(value="page", defaultValue = "0") int page, @RequestParam(value="size", defaultValue = "10") int size, Search search) {
		return sampleService.getDeviceList(page, size, search);
	}

	@GetMapping("/bo/getDeviceInfo/{deviceNumber}")
	Header<SampleVO> getDeviceInfo(@PathVariable(name="deviceNumber") Long deviceNumber) {
		return sampleService.getDeviceInfo(deviceNumber);
	}

	@PostMapping("/bo/insertDevice")
	Header<SampleVO> insertDevice(@RequestBody SampleVO SampleVO) {
		return sampleService.insertDevice(SampleVO);
	}

	@PatchMapping("/bo/updateDevice")
	Header<SampleVO> updateDevice(@RequestBody SampleVO SampleVO) {
		return sampleService.updateDevice(SampleVO);
	}

	@DeleteMapping("/bo/deleteDevice/{deviceNumber}")
	Header<String> deleteDevice(@PathVariable(name="deviceNumber") Long deviceNumber) {
		return sampleService.deleteDevice(deviceNumber);
	}
}
