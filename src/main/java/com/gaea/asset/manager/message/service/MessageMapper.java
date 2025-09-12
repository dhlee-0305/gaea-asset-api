package com.gaea.asset.manager.message.service;

import com.gaea.asset.manager.message.vo.MessageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    String getUserName(Integer Integer);
    String getDeviceOwnerID(Integer deviceNum);
    List<String> getManagerID(String roleCode);
    void insertMessage(MessageVO entity);
}
