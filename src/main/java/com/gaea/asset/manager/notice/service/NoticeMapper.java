package com.gaea.asset.manager.notice.service;

import java.util.HashMap;
import java.util.List;

import com.gaea.asset.manager.notice.vo.FileVO;
import org.apache.ibatis.annotations.Mapper;

import com.gaea.asset.manager.notice.vo.NoticeVO;

@Mapper
public interface NoticeMapper {
	List<NoticeVO> getNoticeList(HashMap<String, Object> paramMap);

	int getNoticeTotalCount(HashMap<String, Object> paramMap);

	NoticeVO getNoticeInfo(Long idx);

	int insertNotice(NoticeVO entity);

	int updateNotice(NoticeVO entity);

	int deleteNotice(Long idx);

    void insertFile(FileVO entity);

    List<FileVO> getFileList(Long idx);

    FileVO getOriginFileName(String storedFileName);
}
