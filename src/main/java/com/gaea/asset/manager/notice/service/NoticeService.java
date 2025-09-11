package com.gaea.asset.manager.notice.service;

import java.io.*;
import java.util.HashMap;
import java.util.List;

import com.gaea.asset.manager.common.constants.CodeConstants;
import com.gaea.asset.manager.file.service.FileService;
import com.gaea.asset.manager.file.vo.FileVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.gaea.asset.manager.notice.vo.NoticeVO;
import com.gaea.asset.manager.util.Header;
import com.gaea.asset.manager.util.Pagination;
import com.gaea.asset.manager.util.Search;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {
    private final FileService fileService;
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
            List<FileVO> fileList = fileService.getFileList(noticeNum);
            if (fileList != null) {
                noticeVO.setFileList(fileList);
            }
            return Header.OK(noticeVO);
        } catch (Exception e) {
            return Header.ERROR(INTERNAL_SERVER_ERROR, "공지사항 상세 조회 중 오류가 발생했습니다.");
        }
	}

    // 공지사항 등록
    @Transactional
    public Header<NoticeVO> insertNotice(NoticeVO noticeVO, List<MultipartFile> files) {
        try {
            int result = noticeMapper.insertNotice(noticeVO);
            if (result <= 0) {
                return Header.ERROR(BAD_REQUEST, "공지사항 등록에 실패했습니다.");
            }
            Long noticeNum = noticeVO.getNoticeNum();
            fileService.saveFile(files, noticeNum, CodeConstants.POST_TYPE_NOTICE);
            return Header.OK(OK, "공지사항이 등록되었습니다.", noticeVO);
        } catch (IllegalArgumentException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Header.ERROR(BAD_REQUEST, e.getMessage());
        } catch (IOException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Header.ERROR(INTERNAL_SERVER_ERROR, "파일 저장 중 오류가 발생했습니다.");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Header.ERROR(INTERNAL_SERVER_ERROR, "공지사항 등록 중 오류가 발생했습니다.");
        }
    }

    // 공지사항 정보 수정
    @Transactional
	public Header<NoticeVO> updateNotice(NoticeVO noticeVO, List<MultipartFile> files) {
        try {
            int result = noticeMapper.updateNotice(noticeVO);
            if (result <= 0) {
                return Header.ERROR(BAD_REQUEST, "공지사항 수정에 실패했습니다.");
            }
            Long noticeNum = noticeVO.getNoticeNum();
            fileService.saveFile(files, noticeNum, CodeConstants.POST_TYPE_NOTICE);
            return Header.OK(OK, "공지사항이 수정되었습니다.", noticeVO);
        } catch (IllegalArgumentException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Header.ERROR(BAD_REQUEST, e.getMessage());
        } catch (IOException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Header.ERROR(INTERNAL_SERVER_ERROR, "파일 저장 중 오류가 발생했습니다.");
        } catch (Exception e) {
            return Header.ERROR(INTERNAL_SERVER_ERROR, "공지사항 수정 중 오류가 발생했습니다." + e.getMessage());
        }
	}

    // 공지사항 삭제
    @Transactional
	public Header<String> deleteNotice(Long noticeNum) {
        try {
            List<FileVO> fileList = fileService.getFileList(noticeNum);
            String savePath = fileService.savePath(CodeConstants.POST_TYPE_NOTICE);

            for (FileVO fileVO : fileList) {
                String storedFileName = fileVO.getStoredFileName();
                File file = new File(savePath, storedFileName);
                if (file.exists()  && !file.delete()) {
                    return Header.ERROR(BAD_REQUEST, "파일 삭제에 실패했습니다.");
                }
            }
            fileService.deleteFile(CodeConstants.POST_TYPE_NOTICE, noticeNum);
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
