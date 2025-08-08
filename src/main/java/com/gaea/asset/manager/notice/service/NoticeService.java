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

	public Header<List<NoticeVO>> getNoticeList(int currentPage, int pageSize, Search search) {
		HashMap<String, Object> paramMap = new HashMap<>();

		paramMap.put("page", (currentPage - 1) * pageSize);
		paramMap.put("size", pageSize);
		paramMap.put("searchColumn", search.getSearchColumn());
		paramMap.put("searchKeyword", search.getSearchKeyword());

		List<NoticeVO> boardList = noticeMapper.getNoticeList(paramMap);
		Pagination pagination = new Pagination(
				noticeMapper.getNoticeTotalCount(paramMap),
				currentPage,
				pageSize,
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
			return Header.ERROR("500", "ERROR");
		}
	}

	public Header<NoticeVO> updateNotice(NoticeVO NoticeVO) {
		if (noticeMapper.updateNotice(NoticeVO) > 0) {
			return Header.OK(NoticeVO);
		} else {
			return Header.ERROR("500", "ERROR");
		}
	}

	public Header<String> deleteNotice(Long noticeNum) {
		if (noticeMapper.deleteNotice(noticeNum) > 0) {
			return Header.OK();
		} else {
			return Header.ERROR("500", "ERROR");
		}
	}
}
