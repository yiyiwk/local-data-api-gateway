package com.sharkapi.web.uiservice.service.impl;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.sharkapi.web.uiservice.mapper.DatamartMapper;
import com.sharkapi.web.uiservice.pojo.ResultException;
import com.sharkapi.web.uiservice.service.IndexService;
import com.sharkapi.web.uiservice.service.jdbc.JdbcQueryService;
import com.sharkapi.web.uiservice.util.CommonUtil;
import com.sharkapi.web.uiservice.util.EncryptUtil;
import com.sharkapi.web.uiservice.util.JarUploadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* */
@Service("DatamartService")
public class DatamartServiceImpl implements IndexService {

    @Resource
    private DatamartMapper datamartMapper;

    @Resource
    private JdbcQueryService jdbcQueryService;

    @Override
    public Object service(String method, String paramData, MultipartFile file) throws ResultException {

        //根据method方法，分发请求服务
        Object result = null;
        JSONObject json = null;
        try{
            json = JSONObject.parseObject(paramData);
        }catch(Exception e){
            throw new ResultException(101,"请求参数格式错误，请修改在重新提交。");
        }

        if("all".equals(method)){
            result = all();
        } else if("insert".equals(method)){
            result = insert(json);
        } else if("update".equals(method)){
            result = update(json);
        } else if("delete".equals(method)){
            result = delete(json);
        } else if("getAllDatabase".equals(method)){
            result = getAllDatabase();
        } else if("upload".equals(method)){
            result = upload(file);
        } else {
            throw new ResultException(94,"未实现的接口，请检查。");
        }
        return result;
    }


    /* 获取全部数据驱动
    * */
    public List<Map> getAllDatabase(){

        List<Map> list = datamartMapper.allDatabase();
        return list;
    }

    /* 数据源查询，全部
    * */
    public List<Map> all(){
        try{
            List<Map> all = datamartMapper.all();
            for(Map p :all){
                if(p.containsKey("url") && StringUtils.isNotEmpty("url")){
                    p.replace("url", EncryptUtil.decrypt(p.get("url")));
                }
                if(p.containsKey("username") && StringUtils.isNotEmpty("username")){
                    p.replace("username",EncryptUtil.decrypt(p.get("username")));
                }
                if(p.containsKey("password") && StringUtils.isNotEmpty("password")){
                    p.replace("password",EncryptUtil.decrypt(p.get("password")));
                }
            }
            return all;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /* 数据源插入
    * */
    public Map insert(JSONObject json) throws ResultException{

        if(!json.containsKey("name") || !json.containsKey("type") ||
                !json.containsKey("url")){
            throw new ResultException(121,"添加数据源，名称、类型和url不能为空。");
        }

        Map map = CommonUtil.JsonToMap(json);
        //检查名称是否重复
        Map<String, Object> same = datamartMapper.getSameWithName(json.getString("name"));
        if(MapUtil.isNotEmpty(same)){
            throw new ResultException(128,"添加数据源名称不能重复，请重新提交。");
        }
        if(json.containsKey("type") && "0".equals(json.getString("type"))){
            if(!json.containsKey("class") || (json.containsKey("class") && StringUtils.isBlank(json.getString("class"))))
                throw new ResultException(166,"驱动类名不能为空。");
            if(!json.containsKey("file") || (json.containsKey("file") && StringUtils.isBlank(json.getString("file"))))
                throw new ResultException(167,"驱动jar包不能为空");
        }
        if(!json.containsKey("type") || (json.containsKey("type") && !"0".equals(json.getString("type")))){
            Map<String, Object> base = datamartMapper.getDatabaseOne(Integer.valueOf(json.getString("type")));
            json.put("file",base.get("file"));
            json.put("class",base.get("class"));
        }
        //进行数据源检查
        Object service = jdbcQueryService.service(json.toJSONString());
        if(service == null){
            throw new ResultException(127,"添加数据源无效，请修改后，在重试。");
        }
        int insertDatabaseId = 0;
        if(json.containsKey("type") && "0".equals(json.getString("type"))){
            insertDatabaseId = saveDataBase(json);
            map.replace("type",String.valueOf(insertDatabaseId));
        }
        map.replace("url", EncryptUtil.encrypt(json.getString("url")));
        if(json.containsKey("password") && StringUtils.isNotEmpty(json.getString("password"))){
            map.replace("password",EncryptUtil.encrypt(json.getString("password")));
        }
        if(json.containsKey("username") && StringUtils.isNotEmpty(json.getString("username"))){
            map.replace("username",EncryptUtil.encrypt(json.getString("username")));
        }
        if(datamartMapper.insertDatamart(map) == 1){
            Map<String, Object> name = datamartMapper.getSameWithName(json.getString("name"));
            if(name.containsKey("username")){
                name.replace("username",EncryptUtil.decrypt(name.get("username")));
            }
            if(name.containsKey("password")){
                name.replace("password",EncryptUtil.decrypt(name.get("password")));
            }
            name.replace("url",EncryptUtil.decrypt(name.get("url")));
            return name;
        }else {
            throw new ResultException(124,"添加数据源失败，请稍后重试。");
        }
    }

    /* 数据源修改
    * */
    public Map update(JSONObject json) throws ResultException{

        if(!json.containsKey("id") || StringUtils.isEmpty(json.getString("id")))
            throw new ResultException(129,"参数id错误，请检查。");
        if(!json.containsKey("type") && !json.containsKey("name") &&
                !json.containsKey("url") && !json.containsKey("username") &&
                !json.containsKey("password") && !json.containsKey("state")){
            throw new ResultException(122,"修改数据源，名称、类型和url、用户名、密码、状态不能全部为空。");
        }

        Map map = CommonUtil.JsonToMap(json);
        if(json.containsKey("name") && json.getString("name") != null){
            Map<String, Object> same = datamartMapper.getSameWithName(json.getString("name"));
            if(MapUtil.isNotEmpty(same)){
                if(!same.get("id").equals(json.getInteger("id"))){
                    throw new ResultException(128,"添加数据源名称不能重复，请重新提交。");
                }
            }
        }

        /* 修改，如果type = 0表示需要新建驱动信息
        * */
        int insertDatabaseId = 0;
        if(json.containsKey("type") && "0".equals(json.getString("type"))){

            if(!json.containsKey("class") || (json.containsKey("class") && StringUtils.isBlank(json.getString("class"))))
                throw new ResultException(166,"驱动类名不能为空。");
            if(!json.containsKey("file") || (json.containsKey("file") && StringUtils.isBlank(json.getString("file"))))
                throw new ResultException(167,"驱动jar包不能为空");
            Object service = jdbcQueryService.service(json.toJSONString());
            if(service == null){
                throw new ResultException(127,"添加数据源无效，请修改后，在重试。");
            }
            JSONObject p = new JSONObject();
            p.put("class",map.get("class"));
            p.put("file",map.get("file"));
            p.put("url",map.get("url"));
            p.put("dataBaseName",map.get("dataBaseName"));
            insertDatabaseId =saveDataBase(p);
            map.replace("type",String.valueOf(insertDatabaseId));

        } else {

            Map<String, Object> datamart = datamartMapper.getDatamartOne(json.getInteger("id"));
            if(MapUtil.isEmpty(datamart))
                throw new ResultException(130,"数据源不存在，请检查。");

            JSONObject jsonMap = JSONObject.parseObject(JSONObject.toJSONString(datamart));
            if(json.containsKey("url") && json.getString("url") != null)
                jsonMap.replace("url",json.getString("url"));
            if(json.containsKey("username") && json.getString("username") != null)
                jsonMap.replace("username",json.getString("username"));
            if(json.containsKey("password") && json.getString("password") != null)
                jsonMap.replace("password",json.getString("password"));

            Map<String, Object> base = datamartMapper.getDatabaseOne(Integer.valueOf(json.getString("type")));
            jsonMap.put("file",base.get("file"));
            jsonMap.put("class",base.get("class"));
            //进行数据源检查
            Object service = jdbcQueryService.service(jsonMap.toJSONString());
            if(service == null){
                throw new ResultException(127,"数据源无效，请修改后，在重试。");
            }
        }

        if(json.containsKey("password") && StringUtils.isNotEmpty(json.getString("password"))){
            map.replace("password",EncryptUtil.encrypt(json.getString("password")));
        }
        if(json.containsKey("username") && StringUtils.isNotEmpty(json.getString("username"))){
            map.replace("username",EncryptUtil.encrypt(json.getString("username")));
        }
        if(json.containsKey("url") && StringUtils.isNotEmpty(json.getString("url"))){
            map.replace("url",EncryptUtil.encrypt(json.getString("url")));
        }

        int num =  datamartMapper.updateDatamart(map);
        if(num == 1){
            Map<String, Object> datamart = datamartMapper.getDatamartOne(json.getInteger("id"));
            datamart.replace("url",EncryptUtil.decrypt(datamart.get("url")));
            datamart.replace("username",EncryptUtil.decrypt(datamart.get("username")));
            datamart.replace("password",EncryptUtil.decrypt(datamart.get("password")));
            return datamart;
        } else {
            throw new ResultException(123,"修改数据源失败，请稍后重试。");
        }

    }

    /* 删除数据源
    * */
    public String delete(JSONObject json) throws ResultException{
        if(!json.containsKey("id") || (json.containsKey("id") && json.getInteger("id") == null))
            throw new ResultException(125,"删除数据源，id不能为空。");

        //查看当前数据源是否被使用，如果使用，就不能删除
        List<Map> apis = datamartMapper.getApiUseList(json.getInteger("id"));
        if(apis.size() > 0 )
            throw new ResultException(131,"请删除此数据源的API所有接口后才能删除。");

        int num = datamartMapper.deleteDatamart(json.getInteger("id"));
        if(num == 1){
            return "删除数据源成功。";
        } else {
            throw new ResultException(126,"删除数据源失败，请稍后重试。");
        }
    }

    /* 根据id获取数据源的驱动信息
    * */
    public  Map getDatabaseDriver(JSONObject json){

        return datamartMapper.getDatabaseOne(json.getInteger("id"));
    }

    /*自定义添加一个数据源种类
    * */
    public int saveDataBase(JSONObject json) throws ResultException{

        Map<String,Object> map = new HashMap<String,Object>();
        int id = 0;
        List<Map> list = datamartMapper.allDatabase();
        if(list.size() >0 ){
            Map<String, Object> maxDataBase = datamartMapper.getMaxDataBase();
            if(!maxDataBase.isEmpty()){
                id = Integer.valueOf(maxDataBase.get("id").toString())+1;
            }
        }
        try{
            if(id > 0){
                map.put("id",id);
                map.put("type",id);
            } else {
                map.put("id",1);
                map.put("type",1);
                id = 1;
            }
            map.put("name",json.getString("dataBaseName"));
            map.put("defaultUrl",json.getString("url"));
            map.put("file",json.getString("file"));
            map.put("class",json.getString("class"));
            if(!map.isEmpty()){

                datamartMapper.insertDataBase(map);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return id;
    }

    /* 驱动包上传
     * */
    public String upload(MultipartFile file) throws ResultException{


        if(getJarRepeat(file.getOriginalFilename()))
            throw new ResultException(167,"驱动包已存在，不能重复上传。");
        String path = null;
        if(file == null || (file != null && file.getSize() < 100))
            throw new ResultException(132,"数据库驱动包文件不能为空。");

        if(file != null){
            String sxfName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
            if(!"jar".equals(sxfName.toLowerCase())){
                throw new ResultException(133,"上传的驱动包必须是打包的jar文件，请参照指引操作。");
            }
            path = JarUploadUtil.uploadJar(file);
        }
        return path;
    }

    public boolean getJarRepeat(String fileName){

        Map<String, Object> jarRepeat = datamartMapper.getJarRepeat(fileName);
        if(jarRepeat != null){
            return true;
        }
        return false;
    }

}
