package com.sharkapi.web.uiservice.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface HandlerMapper {

    List<Map> all();

    public int insertHandler(Map map);

    public Map<String,Object> getHandlerOne(int id);

    public void deleteHandler(int id);

    public List<Map> getHandlerList(Integer id);

    public int updateSortLowWithId(Map map);

    public int updateSortUp(Map sortMap);

    void updateSortLow(Map sortMap);

    public int updateSortUpWithId(Map map);

    List<Map> getHandlerListWithState(Integer id);

    public Map<String,Object> checkName(String name);

    public int update(Map updateMap);
}
