package com.gaea.asset.manager.bo.device.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.bo.device.vo.SampleVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Pagination;
import com.gaea.asset.manager.util.Search;

@Service
public class SampleService {
	private final SampleMapper sampleMapper;
	
	@Autowired
	public SampleService(SampleMapper sampleMapper) {
		this.sampleMapper = sampleMapper;
	}

	public Header<List<SampleVO>> getDeviceList(int page, int size, Search search) {
		HashMap<String, Object> paramMap = new HashMap<>();

		if (page <= 1) {	//페이지가 1 이하로 입력되면 0으로 고정,
			paramMap.put("page", 0);
		} else {			//페이지가 2 이상
			paramMap.put("page", (page - 1) * size);
		}
		paramMap.put("size", size);
		paramMap.put("searchKey", search.getSearchKey());
		paramMap.put("searchValue", search.getSearchValue());

		List<SampleVO> boardList = sampleMapper.getDeviceList(paramMap);
		Pagination pagination = new Pagination(
				sampleMapper.getDeviceTotalCount(paramMap),
				page,
				size,
				10
		);

		return Header.OK(boardList, pagination);
	}

	public Header<SampleVO> getDeviceInfo(Long deviceNumber) {
		return Header.OK(sampleMapper.getDeviceInfo(deviceNumber));
	}

	public Header<SampleVO> insertDevice(SampleVO SampleVO) {
		if (sampleMapper.insertDevice(SampleVO) > 0) {
			return Header.OK(SampleVO);
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}

	public Header<SampleVO> updateDevice(SampleVO SampleVO) {
		if (sampleMapper.updateDevice(SampleVO) > 0) {
			return Header.OK(SampleVO);
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}

	public Header<String> deleteDevice(Long deviceNumber) {
		if (sampleMapper.deleteDevice(deviceNumber) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}
}
