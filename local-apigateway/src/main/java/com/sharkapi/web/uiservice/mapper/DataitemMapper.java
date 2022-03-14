package com.sharkapi.web.uiservice.mapper;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataitemMapper {

    List<Map> all(int id);

    public int insertDataitem(Map map);

    public Map<String,Object> getDataitemOne(int id);

    public void deleteDataitem(int id);

    public int updateDataitem(Map map);

    public Map<String,Object> checkNodeName(Map map);

    public List<Map> allWithState(Integer id);

    Map<String, Object> checkName(Map map);

    Map<String, Object> checkWithSelfName(Map map);

    Map<String, Object> checkWithSelfNode(Map map);
}
