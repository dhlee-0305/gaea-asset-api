package com.gaea.asset.manager.message.service;

import com.gaea.asset.manager.message.vo.MessageVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper {
    Integer getEmpNum(String userID);
    String getUserID(Integer empNum);
    String getDeviceOwner(Integer deviceNum);
    Integer getManagerEmpNum(String roleCode);
    void insertMessage(MessageVO entity);
}
