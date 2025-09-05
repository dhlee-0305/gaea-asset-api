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

    List<FileVO> getFileList(Long idx);

    FileVO getFileInfo(Long idx);

    void insertFile(FileVO entity);

	int insertNotice(NoticeVO entity);

	int updateNotice(NoticeVO entity);

    void updateFileFlag(Long idx);

    void deleteFile(Long idx);

	int deleteNotice(Long idx);
}
