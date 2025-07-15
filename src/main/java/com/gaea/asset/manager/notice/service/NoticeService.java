package com.gaea.asset.manager.notice.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.notice.vo.NoticeVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Pagination;
import com.gaea.asset.manager.util.Search;

@Service
public class NoticeService {
	private final NoticeMapper noticeMapper;

	@Autowired
	public NoticeService(NoticeMapper noticeMapper) {
		this.noticeMapper = noticeMapper;
	}

	public Header<List<NoticeVO>> getNoticeList(int page, int size, Search search) {
		HashMap<String, Object> paramMap = new HashMap<>();

		if (page <= 1) {	//페이지가 1 이하로 입력되면 0으로 고정,
			paramMap.put("page", 0);
		} else {			//페이지가 2 이상
			paramMap.put("page", (page - 1) * size);
		}
		paramMap.put("size", size);
		paramMap.put("searchKey", search.getSearchKey());
		paramMap.put("searchValue", search.getSearchValue());

		List<NoticeVO> boardList = noticeMapper.getNoticeList(paramMap);
		Pagination pagination = new Pagination(
				noticeMapper.getNoticeTotalCount(paramMap),
				page,
				size,
				10
		);

		return Header.OK(boardList, pagination);
	}

	public Header<NoticeVO> getNoticeInfo(Long noticeNum) {
		return Header.OK(noticeMapper.getNoticeInfo(noticeNum));
	}

	public Header<NoticeVO> insertNotice(NoticeVO NoticeVO) {
		if (noticeMapper.insertNotice(NoticeVO) > 0) {
			return Header.OK(NoticeVO);
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}

	public Header<NoticeVO> updateNotice(NoticeVO NoticeVO) {
		if (noticeMapper.updateNotice(NoticeVO) > 0) {
			return Header.OK(NoticeVO);
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}

	public Header<String> deleteNotice(Long noticeNum) {
		if (noticeMapper.deleteNotice(noticeNum) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("9999", "ERROR");
		}
	}
}
