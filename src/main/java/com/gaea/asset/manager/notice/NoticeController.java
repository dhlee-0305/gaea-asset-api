package com.gaea.asset.manager.notice;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaea.asset.manager.notice.service.NoticeService;
import com.gaea.asset.manager.notice.vo.NoticeVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "공지사항 관리 API", description = "공지사항 관리 API 입니다.")
public class NoticeController {
	private final NoticeService noticeService;

	public NoticeController(NoticeService noticeService) {
		this.noticeService = noticeService;
	}

	@GetMapping("/notices")
	@Operation(summary = "공지사항 목록 조회", description = "공지사항 목록 조회 API")
	Header<List<NoticeVO>> getNoticeList(@RequestParam(value="page", defaultValue = "0") int page, @RequestParam(value="size", defaultValue = "10") int size, Search search) {
		return noticeService.getNoticeList(page, size, search);
	}

	@GetMapping("/notices/{noticeNum}")
	@Parameters({
		@Parameter(name = "noticeNum", description = "공지사항 번호", example = "1")
	})
	@Operation(summary = "공지사항 상세 조회", description = "공지사항 상세 조회 API")
	Header<NoticeVO> getNoticeInfo(@PathVariable(name="noticeNum") Long noticeNum) {
		return noticeService.getNoticeInfo(noticeNum);
	}

	@PostMapping("/notices")
	@Operation(summary = "공지사항 등록", description = "공지사항 등록 API")
	Header<NoticeVO> insertNotice(@RequestBody NoticeVO NoticeVO) {
		return noticeService.insertNotice(NoticeVO);
	}

	@PutMapping("/notices/{noticeNum}")
	@Operation(summary = "공지사항 정보 수정", description = "공지사항 정보 수정 API")
	@Parameters({
		@Parameter(name = "noticeNum", description = "공지사항 번호", example = "15")
	})
	Header<NoticeVO> updateNotice(@RequestBody NoticeVO NoticeVO) {
		return noticeService.updateNotice(NoticeVO);
	}

	@DeleteMapping("/notices/{noticeNum}")
	@Operation(summary = "공지사항 삭제", description = "공지사항 삭제 API")
	@Parameters({
		@Parameter(name = "noticeNum", description = "공지사항 번호", example = "15")
	})
	Header<String> deleteNotice(@PathVariable(name="noticeNum") Long noticeNum) {
		return noticeService.deleteNotice(noticeNum);
	}
}
