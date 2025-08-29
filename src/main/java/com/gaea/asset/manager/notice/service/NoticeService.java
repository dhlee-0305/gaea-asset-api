package com.gaea.asset.manager.notice.service;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.gaea.asset.manager.notice.vo.FileVO;
import com.gaea.asset.manager.util.FileValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.notice.vo.NoticeVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Pagination;
import com.gaea.asset.manager.util.Search;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {
	private final NoticeMapper noticeMapper;

    public static final String OK = "200";
    public static final String NO_CONTENT = "204";
    public static final String BAD_REQUEST = "400";
    public static final String INTERNAL_SERVER_ERROR = "500";

    // 공지사항 목록 조회
	public Header<List<NoticeVO>> getNoticeList(int currentPage, int pageSize, Search search) {
        try {
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

            if (boardList == null || boardList.isEmpty()) {
                return Header.OK(NO_CONTENT, "등록된 공지사항이 없습니다.", boardList);
            }
            return Header.OK(boardList, pagination);
        } catch (Exception e) {
            return Header.ERROR(INTERNAL_SERVER_ERROR, "공지사항 목록 조회 중 오류가 발생했습니다.");
        }

	}

    // 공지사항 상세 조회
	public Header<NoticeVO> getNoticeInfo(Long noticeNum) {
        if (noticeNum == null) {
            return Header.ERROR(BAD_REQUEST, "공지사항 번호가 누락되었습니다.");
        }
        try {
            NoticeVO noticeVO = noticeMapper.getNoticeInfo(noticeNum);
            if (noticeVO == null) {
                return Header.OK(NO_CONTENT, "해당 공지사항을 찾을 수 없습니다.", null);
            }
            List<FileVO> fileList = noticeMapper.getFileList(noticeNum);
            if (fileList != null) {
                noticeVO.setFileList(fileList);
            }
            return Header.OK(noticeVO);
        } catch (Exception e) {
            return Header.ERROR(INTERNAL_SERVER_ERROR, "공지사항 상세 조회 중 오류가 발생했습니다.");
        }
	}

    // 원본 파일명 조회
    public Header<String> getOriginFileName(String storedFileName) {
        if (storedFileName == null || storedFileName.isBlank()) {
            return Header.ERROR(BAD_REQUEST, "파일명이 누락되었습니다.");
        }
        try {
            FileVO fileVO = noticeMapper.getOriginFileName(storedFileName);
            if (fileVO == null || fileVO.getOriginFileName() == null) {
                return Header.OK(NO_CONTENT, "원본 파일명을 찾을 수 없습니다.", null);
            }
            return Header.OK(fileVO.getOriginFileName());
        } catch (Exception e) {
            return Header.ERROR(INTERNAL_SERVER_ERROR, "파일명 조회 중 오류가 발생했습니다.");
        }
    }

    // 공지사항 등록
    @Transactional
    public Header<NoticeVO> insertNotice(NoticeVO NoticeVO, List<MultipartFile> files) {
        String savePath = "D:/uploads/files/";
        File uploadDir = new File(savePath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            return Header.ERROR(BAD_REQUEST, "파일 저장 경로 생성에 실패했습니다.");
        }

        try {
            int result = noticeMapper.insertNotice(NoticeVO);
            if (result <= 0) {
                return Header.ERROR(BAD_REQUEST, "공지사항 등록에 실패했습니다.");
            }

            Long noticeNum = NoticeVO.getNoticeNum();

            if (files != null && !files.isEmpty()) {
                for (MultipartFile multipartFile : files) {
                    try {
                        if (!multipartFile.isEmpty()) {
                            FileValidator.validate(multipartFile);

                            String storedFileName = insertFile(multipartFile, savePath);

                            FileVO fileVO = new FileVO();
                            fileVO.setNoticeNum(noticeNum);
                            fileVO.setStoredFileName(storedFileName);
                            fileVO.setOriginFileName(multipartFile.getOriginalFilename());

                            noticeMapper.insertFile(fileVO);
                        }
                    } catch (IllegalArgumentException e) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return Header.ERROR(BAD_REQUEST, e.getMessage());
                    } catch (IOException e) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return Header.ERROR(INTERNAL_SERVER_ERROR, "파일 저장 중 오류가 발생했습니다.");
                    }
                }
            }
            return Header.OK(OK, "공지사항이 등록되었습니다.", NoticeVO);
        } catch (Exception e) {
            return Header.ERROR(INTERNAL_SERVER_ERROR, "공지사항 등록 중 오류가 발생했습니다.");
        }
    }

    // 파일 등록
    public String insertFile(MultipartFile multipartFile, String savePath) throws IOException {
        String originFileName = multipartFile.getOriginalFilename();
        String fileExtension = null;
        if (FilenameUtils.getExtension(originFileName) != null) {
            fileExtension = FilenameUtils.getExtension(originFileName).toLowerCase();
        }
        String storedFileName = UUID.randomUUID() + "." + fileExtension;
        File file = new File(savePath, storedFileName);

        try (InputStream in = multipartFile.getInputStream();
             OutputStream out = new FileOutputStream(file)) {
            FileCopyUtils.copy(in, out);
        }

        return storedFileName;
    }

    // 공지사항 정보 수정
	public Header<NoticeVO> updateNotice(NoticeVO NoticeVO) {
        try {
            if (noticeMapper.updateNotice(NoticeVO) > 0) {
                return Header.OK(OK, "공지사항이 수정되었습니다.", NoticeVO);
            } else {
                return Header.ERROR(BAD_REQUEST, "공지사항 수정에 실패했습니다.");
            }
        } catch (Exception e) {
            return Header.ERROR(INTERNAL_SERVER_ERROR, "공지사항 수정 중 오류가 발생했습니다.");
        }
	}

    // 공지사항 삭제
	public Header<String> deleteNotice(Long noticeNum) {
        try {
            if (noticeMapper.deleteNotice(noticeNum) > 0) {
                return Header.OK(OK, "공지사항이 삭제되었습니다.", String.valueOf(noticeNum));
            } else {
                return Header.ERROR(BAD_REQUEST, "공지사항 삭제에 실패했습니다.");
            }
        } catch (Exception e) {
            return Header.ERROR(INTERNAL_SERVER_ERROR, "공지사항 삭제 중 오류가 발생했습니다.");
        }
    }
}
