package com.gaea.asset.manager.file;

import com.gaea.asset.manager.file.service.FileService;
import com.gaea.asset.manager.file.vo.FileVO;
import com.gaea.asset.manager.util.Header;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Tag(name = "파일 관리 API", description = "파일 관리 API 입니다.")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @GetMapping("/files/{postType}/{fileNum}")
    @Operation(summary = "파일 다운로드", description = "파일 다운로드 API")
    @Parameters({
            @Parameter(name = "postType", description = "파일 유형", example = "notice"),
            @Parameter(name = "fileNum", description = "파일 번호", example = "1")
    })
    ResponseEntity<Resource> downloadFile(@PathVariable String postType, @PathVariable Long fileNum) {
        FileVO fileVO = fileService.getFileInfo(fileNum);
        if (fileVO == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일 정보를 찾을 수 없습니다.");
        }

        String path = fileService.savePath(postType);
        Path filePath = Paths.get(path).resolve(fileVO.getStoredFileName()).normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());

            String encodedFileName = URLEncoder.encode(fileVO.getOriginFileName(), StandardCharsets.UTF_8);
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

    @DeleteMapping("/files/{postType}/{fileNum}")
    @Operation(summary = "파일 삭제", description = "파일 삭제 API")
    @Parameters({
            @Parameter(name = "postType", description = "파일 유형", example = "notice"),
            @Parameter(name = "fileNum", description = "파일 번호", example = "1")
    })
    Header<String> deleteFile(@PathVariable String postType, @PathVariable Long fileNum) {
        return fileService.updateFileFlag(postType, fileNum);
    }
}
