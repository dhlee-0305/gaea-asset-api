package com.gaea.asset.manager.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileValidator {
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "jpg", "jpeg", "png", "gif", "pdf", "hwp", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "csv", "txt"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public static boolean isAllowedExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return false;
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    public static boolean isAllowedFileSize(long fileSize) {
        return fileSize <= MAX_FILE_SIZE;
    }

    public static void validate(MultipartFile file) throws IllegalArgumentException {
        if (!isAllowedExtension(file.getOriginalFilename())) {
            throw new IllegalArgumentException("허용되지 않은 파일 형식입니다: " + file.getContentType());
        }
        if (!isAllowedFileSize(file.getSize())) {
            throw new IllegalArgumentException("파일 크기가 10MB를 초과했습니다: " + file.getSize());
        }
    }

}
