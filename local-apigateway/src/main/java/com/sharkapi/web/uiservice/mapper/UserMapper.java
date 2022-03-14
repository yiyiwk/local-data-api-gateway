package com.sharkapi.web.uiservice.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    Map getUser(Map map);

    public int updatePassword(Map map);

    public List<Map> checkToken(String token);

    int insertToken(Map tokenMap);

    int deleteToken(String token);

    Integer getDataAndInterNum();

    int saveConfig(Map map);

    Map getConfig();
}
