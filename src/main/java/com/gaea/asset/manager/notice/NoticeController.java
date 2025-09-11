package com.gaea.asset.manager.notice;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.gaea.asset.manager.notice.service.NoticeService;
import com.gaea.asset.manager.notice.vo.NoticeVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "공지사항 관리 API", description = "공지사항 관리 API 입니다.")
@RequiredArgsConstructor
public class NoticeController {
	private final NoticeService noticeService;

	@GetMapping("/notices")
	@Operation(summary = "공지사항 목록 조회", description = "공지사항 목록 조회 API")
	Header<List<NoticeVO>> getNoticeList(@RequestParam(value="currentPage", defaultValue = "1") int currentPage, @RequestParam(value="pageSize", defaultValue = "10") int pageSize, Search search) {
		return noticeService.getNoticeList(currentPage, pageSize, search);
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
    Header<NoticeVO> insertNotice(@ModelAttribute NoticeVO NoticeVO, @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        return noticeService.insertNotice(NoticeVO, files);
    }

	@PutMapping("/notices/{noticeNum}")
	@Operation(summary = "공지사항 정보 수정", description = "공지사항 정보 수정 API")
	@Parameters({
		@Parameter(name = "noticeNum", description = "공지사항 번호", example = "15")
	})
	Header<NoticeVO> updateNotice(@ModelAttribute NoticeVO NoticeVO, @RequestParam(value = "files", required = false) List<MultipartFile> files) {
		return noticeService.updateNotice(NoticeVO, files);
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
