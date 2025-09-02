package com.gaea.asset.manager.notice;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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

    @GetMapping("/files/{fileName}")
    @Operation(summary = "파일 다운로드", description = "파일 다운로드 API")
    @Parameters({
            @Parameter(name = "fileName", description = "파일 이름", example = "이미지1.png")
    })
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        String path = System.getProperty("user.dir") + "/notice/files/";
        Path filePath = Paths.get(path).resolve(fileName).normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            String cleanFileName = StringUtils.cleanPath(encodedFileName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", cleanFileName);

            return ResponseEntity.ok()
                    .headers(headers)
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
	Header<NoticeVO> updateNotice(@ModelAttribute NoticeVO NoticeVO, @RequestParam(value = "files", required = false) List<MultipartFile> files) {
		return noticeService.updateNotice(NoticeVO, files);
	}

    @DeleteMapping("/files/{fileNum}")
    @Operation(summary = "파일 삭제", description = "파일 삭제 API")
    @Parameters({
            @Parameter(name = "fileNum", description = "파일 번호", example = "1")
    })
    public Header<String> deleteFile(@PathVariable(name="fileNum") Long fileNum) {
        return noticeService.deleteFile(fileNum);
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
