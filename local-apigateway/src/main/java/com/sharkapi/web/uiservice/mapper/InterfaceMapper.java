package com.sharkapi.web.uiservice.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface InterfaceMapper {

    List<Map> all();

    public int insertInterface(Map map);

    public Map<String,Object> getInterfaceOne(int id);

    public int  deleteInterface(int id);

    public int updateInterface(Map map);

    public Map getPathSame(String path);

    public Map getNameSame(String name);

    public Map findInterfaceOne(String path);

    public int getSumDataitem(int id);

    public int getSumHandler(Integer id);

    public int getSumParamHandler(Integer id);
}
