package com.gaea.asset.manager.file.service;

import com.gaea.asset.manager.file.vo.FileVO;
import com.gaea.asset.manager.util.FileTypeMapper;
import com.gaea.asset.manager.util.FileValidator;
import com.gaea.asset.manager.util.Header;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final FileMapper fileMapper;

    public static final String OK = "200";
    public static final String NO_CONTENT = "204";
    public static final String BAD_REQUEST = "400";
    public static final String INTERNAL_SERVER_ERROR = "500";

    // 파일 경로 설정
    public String savePath(String postType) {
        String basePath = System.getProperty("user.dir") + "/files/";
        String fileType = FileTypeMapper.getFileType(postType);
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return basePath + fileType + "/" + datePath + "/";
    }

    // 파일 등록
    public String insertFile(MultipartFile multipartFile, String savePath) throws IOException {
        String originFileName = multipartFile.getOriginalFilename();
        String fileExtension = FilenameUtils.getExtension(originFileName);
        if (fileExtension == null || fileExtension.isBlank()) {
            throw new IllegalArgumentException("파일 확장자가 존재하지 않습니다.");
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        String uuid = UUID.randomUUID().toString().substring(0, 6);
        String storedFileName = timestamp + "_" + uuid + "." + fileExtension;
        File file = new File(savePath, storedFileName);

        try (InputStream in = multipartFile.getInputStream();
             OutputStream out = new FileOutputStream(file)) {
            FileCopyUtils.copy(in, out);
        }

        return storedFileName;
    }

    // 파일 저장
    public void saveFile(List<MultipartFile> files, Long postNum, String postType) throws IOException {
        String savePath = savePath(postType);
        File uploadDir = new File(savePath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new RuntimeException("파일 저장 경로 생성에 실패했습니다.");
        }

        if (files != null && !files.isEmpty()) {
            for (MultipartFile multipartFile : files) {
                if (!multipartFile.isEmpty()) {
                    FileValidator.validate(multipartFile);
                    String storedFileName = insertFile(multipartFile, savePath);

                    FileVO fileVO = new FileVO();
                    fileVO.setPostNum(postNum);
                    fileVO.setPostType(postType);
                    fileVO.setStoredFileName(storedFileName);
                    fileVO.setOriginFileName(multipartFile.getOriginalFilename());

                    fileMapper.insertFile(fileVO);
                }
            }
        }
    }

    // 파일 목록 조회
    public List<FileVO> getFileList(Long noticeNum) {
        return fileMapper.getFileList(noticeNum);
    }

    // 파일 상세 조회
    public FileVO getFileInfo(Long fileNum) {
        return fileMapper.getFileInfo(fileNum);
    }

    // 파일 삭제여부 수정
    public Header<String> updateFileFlag(String postType, Long fileNum) {
        try {
            FileVO FileVO = fileMapper.getFileInfo(fileNum);
            if (FileVO == null) {
                return Header.OK(NO_CONTENT, "해당 파일을 찾을 수 없습니다.", null);
            }

            String savePath = savePath(postType);
            File file = new File(savePath, FileVO.getStoredFileName());
            if (file.exists() && !file.delete()) {
                return Header.ERROR(BAD_REQUEST, "파일 삭제에 실패했습니다.");
            }

            fileMapper.updateFileFlag(fileNum);
            return Header.OK(OK, "파일이 삭제되었습니다.", String.valueOf(fileNum));
        } catch (Exception e) {
            return Header.ERROR(INTERNAL_SERVER_ERROR, "파일 삭제 중 오류가 발생했습니다.");
        }
    }

    // 파일 삭제
    public void deleteFile(String postType, Long postNum) {
        List<FileVO> fileList = fileMapper.getFileList(postNum);
        String savePath = savePath(postType);

        for (FileVO fileVO : fileList) {
            File file = new File(savePath, fileVO.getStoredFileName());
            if (file.exists() && !file.delete()) {
                throw new RuntimeException("파일 삭제에 실패했습니다.");
            }
        }

        fileMapper.deleteFile(postType, postNum);
    }
}
