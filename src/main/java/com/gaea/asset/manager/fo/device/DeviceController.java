package com.gaea.asset.manager.fo.device;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaea.asset.manager.fo.device.service.DeviceService;
import com.gaea.asset.manager.fo.device.vo.DeviceVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Search;

@RestController
public class DeviceController {
	private final DeviceService deviceService;
	
	public DeviceController(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	@GetMapping("/fo/deviceList")
	Header<List<DeviceVO>> getDeviceList(@RequestParam(value="page", defaultValue = "0") int page, @RequestParam(value="size", defaultValue = "10") int size, Search search) {
		return deviceService.getDeviceList(page, size, search);
	}

	@GetMapping("/fo/getDeviceInfo/{deviceNumber}")
	Header<DeviceVO> getDeviceInfo(@PathVariable(name="deviceNumber") Long deviceNumber) {
		return deviceService.getDeviceInfo(deviceNumber);
	}

	@PostMapping("/fo/insertDevice")
	Header<DeviceVO> insertDevice(@RequestBody DeviceVO DeviceVO) {
		return deviceService.insertDevice(DeviceVO);
	}

	@PatchMapping("/fo/updateDevice")
	Header<DeviceVO> updateDevice(@RequestBody DeviceVO DeviceVO) {
		return deviceService.updateDevice(DeviceVO);
	}

	@DeleteMapping("/fo/deleteDevice/{deviceNumber}")
	Header<String> deleteDevice(@PathVariable(name="deviceNumber") Long deviceNumber) {
		return deviceService.deleteDevice(deviceNumber);
	}
}
