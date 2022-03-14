package com.sharkapi.service.service.impl;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.sharkapi.web.uiservice.mapper.*;
import com.sharkapi.web.uiservice.pojo.ResultException;
import com.sharkapi.web.uiservice.service.IndexService;
import com.sharkapi.web.uiservice.service.jdbc.JdbcQueryService;
import com.sharkapi.web.uiservice.util.EncryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("OutsideService")
public class OutsideServiceImpl implements IndexService {

    @Resource
    private InterfaceMapper interfaceMapper;

    @Resource
    private DataitemMapper dataitemMapper;

    @Resource
    private JdbcQueryService jdbcQueryService;

    @Resource
    private HandlerMapper handlerMapper;

    @Resource
    private DatamartMapper datamartMapper;

    @Resource
    private ParamHanderMapper paramHanderMapper;

    @Override
    public Object service(String method, String paramData, MultipartFile file) throws ResultException {

        JSONObject resultJson = new JSONObject();
        String path = null;
        if (StringUtils.isNotEmpty(method))
            path = method.replace("/sharkapiservice/", "");
        JSONObject json = null;
        if (StringUtils.isNotBlank(paramData)) {
            try {
                json = JSONObject.parseObject(paramData);
            } catch (Exception e) {
                throw new ResultException(224, "请求参数必须为json格式，请检查。");
            }
        } else {
            json = new JSONObject();
        }
        Map inter = interfaceMapper.findInterfaceOne(path);
        if (MapUtil.isEmpty(inter))
            throw new ResultException(221, "当前请求路径未发现接口，请检查API网关配置。");
        if (inter.get("state") != null && "0".equals(inter.get("state").toString())) {
            throw new ResultException(222, "接口已被禁用，请先配置网关的API接口。");
        }

        /* 添加参数处理器
         * */
        //if(!json.isEmpty()){
        List<Map> paramHanders = paramHanderMapper.getHandlerListWithState(Integer.valueOf(inter.get("id").toString()));
        for (int i = 0; i < paramHanders.size(); i++) {
            Map<String, Object> handler = paramHanders.get(i);
            json = getInvokeParam(json, handler.get("processor").toString());
        }
        //}

        //获取全部启用的数据项
        List<Map> dataitems = dataitemMapper.allWithState(Integer.valueOf(inter.get("id").toString()));
        if (dataitems.size() < 1)
            throw new ResultException(223, "接口没有对应可用数据项，请前往接口列表检查数据项状态和关联数据项使用数据源的状态；或新添加数据项。");

        int size = dataitems.size();
        for (int i = 0; i < dataitems.size(); i++) {
            JSONObject datamart = new JSONObject();
            datamart.put("type", dataitems.get(i).get("type"));
            datamart.put("url", EncryptUtil.decrypt(dataitems.get(i).get("url")));
            datamart.put("username", (dataitems.get(i).containsKey("username") &&
                    StringUtils.isNotEmpty(dataitems.get(i).get("username").toString())) ? EncryptUtil.decrypt(dataitems.get(i).get("username")) : null);
            datamart.put("password", (dataitems.get(i).containsKey("password") &&
                    StringUtils.isNotEmpty(dataitems.get(i).get("password").toString())) ? EncryptUtil.decrypt(dataitems.get(i).get("password")) : null);
            Map<String, Object> repMap = queryRep(dataitems.get(i).get("querysql").toString(), json);
            datamart.put("sql", repMap.get("querysql"));
            datamart.put("params", repMap.get("params"));
            Map<String, Object> dataBase = datamartMapper.getDatabaseOne(Integer.valueOf(dataitems.get(i).get("type").toString()));
            datamart.put("file",dataBase.get("file"));
            datamart.put("class",dataBase.get("class"));
            Object service = jdbcQueryService.service(datamart.toJSONString());
            if (service != null) {
                resultJson.put(dataitems.get(i).get("nodename").toString(), service);
            } else {
                resultJson.put(dataitems.get(i).get("nodename").toString(), null);
            }
        }
        /* 最后添加数据处理器
         * */

        List<Map> handlers = handlerMapper.getHandlerListWithState(Integer.valueOf(inter.get("id").toString()));

        for (int i = 0; i < handlers.size(); i++) {
            Map<String, Object> handler = handlers.get(i);
            resultJson = getInvoke(resultJson, handler.get("processor").toString());
        }
        if (size == 1) {
            return resultJson.getJSONObject(dataitems.get(0).get("nodename").toString());
        } else {
            return resultJson;
        }
    }


    /* 根据参数替换查询sql中的可变参数
     * sql = select * from student where id = {#aa:1#} and name = {#bb:张三#}
     * json = {"aa":2,"bb":"李四"} 或 json = {"aa":2}
     * 返回sql和 array的参数集
     * */
    public static Map<String, Object> queryRep(String querysql, JSONObject json) throws ResultException {

        Map<String, Object> map = new HashMap<>();
        JSONArray array = new JSONArray();
        querysql = querysql.replaceAll("\r\n", " ").replaceAll("\n", " ");

        String param = json.toJSONString();
        try {
            List<Integer> start = new ArrayList<>();
            List<Integer> end = new ArrayList<>();
            if (querysql.indexOf("{#") > 0) {
                for (int i = 0; i < querysql.length(); i++) {
                    char str = querysql.charAt(i);
                    if (str == '{') start.add(i);
                    if (str == '}') end.add(i);
                }
            }
            String finalSql = querysql;
            for (int i = 0; i < start.size(); i++) {
                Integer a = start.get(i);
                Integer b = end.get(i);
                String subStr = querysql.substring(a, b + 1);
                String key = subStr.substring(subStr.indexOf("#") + 1, subStr.indexOf(":")).trim();
                String finalStr = null;
                String subSxf = querysql.substring(a-1,a);
                String subStf = null;
                if(querysql.length() <= b+2){
                    subStf = querysql.substring(b+1);
                } else {
                    subStf = querysql.substring(b+1,b+2);
                }

                /* 读取复杂json格式 */
                Object read = JSONPath.read(param, "$.." + key);
                if (read != null && read instanceof List) {
                    List<Object> value = (List<Object>) read;
                    JSONObject params = new JSONObject();
                    if (value.size() > 0) {
                        if (value.get(value.size() - 1) instanceof Integer) {
                            params.put("type", "java.lang.Integer");
                        } else {
                            params.put("type", "java.lang.String");
                        }
                        params.put("value", json.get(key));
                        array.add(params);

                        if("%".equals(subSxf) || "%".equals(subStf)){
                            finalSql = finalSql.replace(subStr, json.get(key).toString());
                        } else {
                            finalSql = finalSql.replace(subStr, " ? ");
                        }
                        //finalSql = finalSql.replace(subStr, " ? ");
                    } else {
                        finalStr = subStr.substring(subStr.indexOf(":") + 1, subStr.lastIndexOf("#"));
                        if("%".equals(subSxf) || "%".equals(subStf)){
                            finalSql = finalSql.replace(subStr,""+finalStr.trim()+"");
                        } else {
                            finalSql = finalSql.replace(subStr," '"+finalStr.trim()+"' ");
                        }
                        //finalSql = finalSql.replace(subStr, " '" + finalStr.trim() + "' ");
                    }
                } else {
                    finalStr = subStr.substring(subStr.indexOf(":") + 1, subStr.lastIndexOf("#"));
                    if("%".equals(subSxf) || "%".equals(subStf)){
                        finalSql = finalSql.replace(subStr,""+finalStr.trim()+"");
                    } else {
                        finalSql = finalSql.replace(subStr," '"+finalStr.trim()+"' ");
                    }

                    //finalSql = finalSql.replace(subStr, " '" + finalStr.trim() + "' ");
                }
            }
            map.put("querysql", finalSql);
            map.put("params", array);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResultException(159, "请检查查询sql,参照提示填写。");
        }

    }

    /* 动态加载jar包，其中jar包中类名必须是 com.sharkapi.server.Processor
     * 具体解析方法 方法名是：invoke  方法返回必须是json
     * */
    public static JSONObject getInvoke(JSONObject resultJson, String path) throws ResultException {

        JSONObject json = new JSONObject();
        String jarFile = "file:" + System.getProperty("user.dir") + path;   //得到jar包完整路径
        String className = "com.sharkapi.server.Processor";
        Object obj = null;
        try {

            URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL(jarFile)}, Thread.currentThread().getContextClassLoader());
            Class loader = classLoader.loadClass(className);
            Method[] methods = loader.getDeclaredMethods();
            for (Method method : methods) {
                if ("invoke".equals(method.getName())) {
                    obj = method.invoke(loader.newInstance(), resultJson);
                }
            }
        } catch (MalformedURLException e) {
            throw new ResultException(252, "未找到处理器jar包文件，请先上传。");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResultException(253, "加载jar包解析数据出错，请检查jar包中的解析方法，参照下载demo工程中说明。");
        }
        try {
            if (obj instanceof JSONObject) {
                json = (JSONObject) obj;
            }
        } catch (Exception e) {
            throw new ResultException(254, "加载jar包解析数据，数据返回必须是com.alibaba.fastjson.JSONObject类型。");
        }
        return json;
    }

    public static JSONObject getInvokeParam(JSONObject resultJson, String path) throws ResultException {

        JSONObject json = new JSONObject();
        String jarFile = "file:" + System.getProperty("user.dir") + path;   //得到jar包完整路径
        String className = "com.sharkapi.server.Processor";
        Object obj = null;
        try {

            URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL(jarFile)}, Thread.currentThread().getContextClassLoader());
            Class loader = classLoader.loadClass(className);
            Method[] methods = loader.getDeclaredMethods();
            for (Method method : methods) {
                if ("invoke".equals(method.getName())) {
                    obj = method.invoke(loader.newInstance(), resultJson);
                }
            }
        } catch (MalformedURLException e) {
            throw new ResultException(252, "未找到处理器jar包文件，请先上传。");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResultException(253, "加载jar包解析数据出错，请检查jar包中的解析方法，参照下载demo工程中说明。");
        }
        try {
            if (obj instanceof JSONObject) {
                json = (JSONObject) obj;
            }
        } catch (Exception e) {
            throw new ResultException(254, "加载jar包解析数据，数据返回必须是com.alibaba.fastjson.JSONObject类型。");
        }
        return json;
    }


}
