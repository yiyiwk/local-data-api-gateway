package com.sharkapi.web.uiservice.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DatamartMapper {

    List<Map> all();

    public int insertDatamart(Map map);

    public Map<String,Object> getDatamartOne(int id);

    public int deleteDatamart(int id);

    public int updateDatamart(Map map);

    public List<Map> allDatabase();

    public Map<String,Object> getDatabaseOne(Integer id);

    public Map<String,Object> getSameWithName(String name);

    public List<Map> getApiUseList(Integer id);

    public Map<String,Object> getMaxDataBase();

    void insertDataBase(Map<String, Object> map);

    void deleteDataBase(Integer id);

    void updateDataBase(Map map);

    public Map<String,Object> getJarRepeat(String fileName);
}
