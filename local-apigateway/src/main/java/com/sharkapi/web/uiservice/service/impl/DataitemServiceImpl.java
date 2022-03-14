package com.sharkapi.web.uiservice.service.impl;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.sharkapi.web.uiservice.mapper.DataitemMapper;
import com.sharkapi.web.uiservice.mapper.DatamartMapper;
import com.sharkapi.web.uiservice.pojo.ResultException;
import com.sharkapi.web.uiservice.service.IndexService;
import com.sharkapi.web.uiservice.service.jdbc.JdbcQueryService;
import com.sharkapi.web.uiservice.util.CommonUtil;
import com.sharkapi.web.uiservice.util.EncryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;

/* 数据项Service
* */
@Service("DataitemService")
public class DataitemServiceImpl implements IndexService {

    @Resource
    private DataitemMapper dataitemMapper;

    @Resource
    private DatamartMapper datamartMapper;

    @Resource
    private JdbcQueryService jdbcQueryService;

    @Override
    public Object service(String method, String paramData, MultipartFile  file) throws ResultException {

        //根据method方法，分发请求服务
        Object result = null;
        JSONObject json = null;
        try{
            json = JSONObject.parseObject(paramData);
        }catch(Exception e){
            throw new ResultException(101,"请求参数格式错误，请修改在重新提交。");
        }

        if("all".equals(method)){
            result = all(json);
        } else if("insert".equals(method)){
            result = insert(json);
        } else if("update".equals(method)){
            result = update(json);
        } else if("delete".equals(method)){
            result = delete(json);
        } else if("checkQuery".equals(method)){
            result = checkQuery(json);
        } else {
            throw new ResultException(94,"未实现的接口，请检查。");
        }

        return result;
    }

    /* 获取全部数据项
    * */
    public List<Map> all(JSONObject json) throws ResultException{

        if(!json.containsKey("interfaceId")){
            throw new ResultException(151,"请求数据项接口参数有误，请检查。");
        }
        return dataitemMapper.all(json.getInteger("interfaceId"));
    }

    /* 添加数据项
    * */
    public Map insert(JSONObject json) throws ResultException{

        if(!json.containsKey("name") || !json.containsKey("nodename") ||
                !json.containsKey("querysql") || !json.containsKey("state") ||
                !json.containsKey("datamartId") || !json.containsKey("interfaceId")){
            throw new ResultException(152,"添加数据项，节点名、名称和查询语句不能为空。");
        }
        if(StringUtils.isEmpty(json.getString("name")) || StringUtils.isEmpty(json.getString("nodename"))
            || StringUtils.isEmpty(json.getString("querysql")))
            throw new ResultException(162,"添加数据项，必要参数(名称，节点名，查询语句)不能为空。");
        Map map = CommonUtil.JsonToMap(json);
        checkNode(json);
        checkName(json);

        if(json.containsKey("querysql") && StringUtils.isNotEmpty(json.getString("querysql"))){
            if(json.getString("querysql").contains(" update") ||
                    json.getString("querysql").contains(" insert") ||
                    json.getString("querysql").contains(" delete"))
                throw new ResultException(163,"数据项查询语句中，不能包含增、删、改的条件。");
        }
        try{
            if(dataitemMapper.insertDataitem(map) != 1)
                throw new ResultException(155,"添加数据项失败，请稍后重试。");
            return dataitemMapper.checkName(json);
        }catch(Exception e){
            throw new ResultException(155,"添加数据项失败，请稍后重试。");
        }
    }

    /* 添加数据项,判断节点名是否重复
     * */
    public boolean checkNode(JSONObject json) throws ResultException{

        if(!json.containsKey("nodename") && !json.containsKey("interfaceId") &&
                StringUtils.isNotEmpty(json.getString("nodename"))){
            throw new ResultException(157,"添加数据项，参数节点名和接口id不能全部为空。");
        }
        Map map = CommonUtil.JsonToMap(json);
        Map<String, Object> dataitem = dataitemMapper.checkNodeName(map);
        if(MapUtil.isNotEmpty(dataitem)){
            throw new ResultException(158,"同一个接口中，数据项的节点名不能重复，请修改后，再添加。");
        }
        return true;
    }

    /* 添加数据项,判断名称是否重复
     * */
    public boolean checkName(JSONObject json) throws ResultException{

        if(!json.containsKey("name") && !json.containsKey("interfaceId") &&
                StringUtils.isNotEmpty(json.getString("name"))){
            throw new ResultException(157,"添加数据项，参数节点名和接口id不能全部为空。");
        }
        Map map = CommonUtil.JsonToMap(json);
        Map<String, Object> dataitem = dataitemMapper.checkName(map);
        if(MapUtil.isNotEmpty(dataitem)){
            throw new ResultException(161,"同一个接口中，数据项的名称不能重复，请修改后，再添加。");
        }
        return true;
    }


    /* 修改数据项
    * */
    public Map update(JSONObject json) throws ResultException{

        if((!json.containsKey("id") || json.get("id") == null) && !json.containsKey("interfaceId"))
            throw new ResultException(154,"修改数据项，请求参数id错误。");
        if(!json.containsKey("name") && !json.containsKey("nodename")
                && !json.containsKey("querysql") && !json.containsKey("state")){
            throw new ResultException(153,"修改数据项，节点名、名称和查询语句、状态不能全部为空。");
        }
        Map map = CommonUtil.JsonToMap(json);
        if(json.containsKey("name")) {
            Map<String, Object> name = dataitemMapper.checkWithSelfName(map);
            if(MapUtil.isNotEmpty(name) && !json.getString("id").equals(name.get("id").toString())){
                throw new ResultException(161,"同一个接口中，数据项的名称不能重复，请修改后，再添加。");
            }
        }
        if(json.containsKey("nodename")) {
            Map<String, Object> node = dataitemMapper.checkWithSelfNode(map);
            if(MapUtil.isNotEmpty(node) && !json.getString("id").equals(node.get("id").toString())){
                throw new ResultException(158,"同一个接口中，数据项的节点名不能重复，请修改后，再添加。");
            }
        }
        if(json.containsKey("querysql") && StringUtils.isNotEmpty(json.getString("querysql"))){
            if(json.getString("querysql").contains("update ") ||
                    json.getString("querysql").contains("insert ") ||
                    json.getString("querysql").contains("delete "))
                throw new ResultException(163,"数据项查询语句中，不能包含增、删、改的条件。");
        }
        int num = dataitemMapper.updateDataitem(map);
        if(num == 1){
            return dataitemMapper.getDataitemOne(json.getInteger("id"));
        } else{
            throw new ResultException(156,"修改数据项失败，请稍后重试。");
        }
    }

    /* 删除数据项
    * */
    public String delete(JSONObject json) throws ResultException {
        if(!json.containsKey("id") || json.get("id") == null)
            throw new ResultException(154,"修改数据项，请求参数id错误。");
        dataitemMapper.deleteDataitem(json.getInteger("id"));
        return "删除数据项成功";
    }


    /* 校验数据项中的查询条件
    * */
    public JSONObject checkQuery(JSONObject json) throws ResultException{

        JSONObject result = new JSONObject();
        if(!json.containsKey("datamartId") || !json.containsKey("querysql") || !json.containsKey("nodename")
            || StringUtils.isEmpty(json.getString("querysql"))){
            throw new ResultException(160,"查询校验sql，参数错误，请检查。");
        }
        Map base = datamartMapper.getDatamartOne(json.getInteger("datamartId"));
        if(MapUtil.isEmpty(base))
            throw new ResultException(164,"请选择正确的数据源。");
        if(MapUtil.isNotEmpty(base) && "0".equals(base.get("state")))
            throw new ResultException(165,"当前选择的数据源已被禁用，请更改。");

        if(json.containsKey("querysql") && StringUtils.isNotEmpty(json.getString("querysql"))){
            String sql = replaceQuerySql(json.getString("querysql"));

            base.put("sql",sql);
        }
        Map<String, Object> dataBase = datamartMapper.getDatabaseOne(Integer.valueOf(base.get("type").toString()));
        JSONObject jsonMap = JSONObject.parseObject(JSONObject.toJSONString(base));
        jsonMap.put("file",dataBase.get("file"));
        jsonMap.put("class",dataBase.get("class"));
        if(jsonMap.containsKey("url") && jsonMap.getString("url") != null)
            jsonMap.replace("url", EncryptUtil.decrypt(jsonMap.getString("url")));
        if(jsonMap.containsKey("username") && jsonMap.getString("username") != null)
            jsonMap.replace("username",EncryptUtil.decrypt(jsonMap.getString("username")));
        if(jsonMap.containsKey("password") && jsonMap.getString("password") != null)
            jsonMap.replace("password",EncryptUtil.decrypt(jsonMap.getString("password")));
        //进行数据源检查
        Object service = jdbcQueryService.service(jsonMap.toJSONString());
        if(service == null){
            throw new ResultException(127,"数据源无效，请修改后，再重试。");
        }
        Map<String, Object> resultMap = new HashMap<>();
        if(service instanceof  Map){
            resultMap = (Map<String, Object>)service;
            result.put(json.getString("nodename"),resultMap);
        }

        return result;
    }

    /* querysql中可变参数，进行转换(针对添加数据项)
     * 参数说明： {#a:1#}整体中不能出现空格
     * 这里只是替换查询，不需要变更参数整体
     * */
    public static String replaceQuerySql(String sql) throws ResultException{

        sql = sql.replaceAll( "\r\n", " ").replaceAll( "\n", " ");
        try{
            List<Integer> start = new ArrayList<>();
            List<Integer> end = new ArrayList<>();
            if (sql.indexOf("{#") > 0){
                for (int i = 0; i < sql.length(); i++) {
                    char str = sql.charAt(i);
                    if (str == '{')  start.add(i);
                    if (str == '}')  end.add(i);
                }
            }
            String finalSql = sql;
            for (int i = 0; i < start.size(); i++) {
                Integer a = start.get(i);
                Integer b = end.get(i);
                String subStr = sql.substring(a,b+1);
                String finalStr = subStr.substring(subStr.indexOf(":")+1,subStr.lastIndexOf("#"));

                String subSxf = sql.substring(a-1,a);
                String subStf = null;
                if(sql.length() <= b+2){
                    subStf = sql.substring(b+1);
                } else {
                    subStf = sql.substring(b+1,b+2);
                }
                if("%".equals(subSxf) || "%".equals(subStf)){
                    finalSql = finalSql.replace(subStr,""+finalStr.trim()+"");
                } else {
                    finalSql = finalSql.replace(subStr," '"+finalStr.trim()+"' ");
                }

            }
            return finalSql;
        }catch(Exception e){
            throw new ResultException(159,"请检查查询sql,参照提示填写。");
        }
    }


}
