package com.gaea.asset.manager.notice;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/files")
    @Operation(summary = "파일 다운로드", description = "파일 다운로드 API")
    public ResponseEntity<Resource> downloadFile(@RequestParam("storedFileName") String storedFileName) {
        String filePath = "D:/uploads/files/" + storedFileName;
        File file = new File(filePath);

        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일이 존재하지 않습니다.");
        }

        try {
            Path path = file.toPath();
            Resource resource = new UrlResource(path.toUri());

            Header<String> originFileHeader = noticeService.getOriginFileName(storedFileName);
            if (!NoticeService.OK.equals(originFileHeader.getResultCode()) || originFileHeader.getData() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, originFileHeader.getDescription());
            }

            String originalFileName = originFileHeader.getData();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + URLEncoder.encode(originalFileName, StandardCharsets.UTF_8) + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드 실패", e);
        }
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
