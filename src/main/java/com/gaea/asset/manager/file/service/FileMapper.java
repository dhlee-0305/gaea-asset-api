package com.gaea.asset.manager.file.service;

import com.gaea.asset.manager.file.vo.FileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileMapper {

    List<FileVO> getFileList(Long idx);

    FileVO getFileInfo(Long idx);

    void insertFile(FileVO entity);

    void updateFileFlag(Long idx);

    void deleteFile(@Param("postType") String postType, @Param("postNum") Long postNum);
}
