package com.sharkapi.web.uiservice.service.impl;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.sharkapi.web.uiservice.mapper.HandlerMapper;
import com.sharkapi.web.uiservice.pojo.ResultException;
import com.sharkapi.web.uiservice.service.IndexService;
import com.sharkapi.web.uiservice.util.CommonUtil;
import com.sharkapi.web.uiservice.util.JarUploadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("HandlerService")
public class HandlerServiceImpl implements IndexService {

    @Resource
    private HandlerMapper handlerMapper;
    @Override
    public Object service(String method, String paramData, MultipartFile files) throws ResultException {
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
        } else if("upload".equals(method)){
            result = upload(files);
        } else if("sortUp".equals(method)){
            result = sortUp(json);
        } else if("delete".equals(method)){
            result = delete(json);
        } else if("sortLow".equals(method)){
            result = sortLow(json);
        } else if("insert".equals(method)){
            result = insert(json);
        } else if("update".equals(method)){
            result = update(json);
        } else {
            throw new ResultException(94,"未实现的接口，请检查。");
        }

        return result;
    }

    /*根据api的id获取全部处理器
    * */
    public List<Map> all(JSONObject json){
        return handlerMapper.getHandlerList(json.getInteger("interfaceId"));
    }

    /* jar包上传
    * */
    public String upload(MultipartFile file) throws ResultException{
        String path = null;
        if(file == null || (file != null && file.getSize() < 100))
            throw new ResultException(136,"上传处理器文件不能为空。");

        if(file != null){
            String sxfName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
            if(!"jar".equals(sxfName.toLowerCase())){
                throw new ResultException(145,"上传的处理器必须是打包的jar文件，请参照处理器指引操作。");
            }
            path = JarUploadUtil.uploadFile(file);
        }
        return path;
    }

    /* 处理器新增
    * */
    public Map insert(JSONObject json) throws ResultException{

        if(!json.containsKey("interfaceId")){
            throw new ResultException(135,"上传jar文件，接口id参数不能为空。");
        }
        if(!json.containsKey("name") || StringUtils.isEmpty(json.getString("name"))){
            throw new ResultException(137,"处理器的名称不能为空。");
        }
        if(!json.containsKey("processor") || (json.containsKey("processor") && StringUtils.isEmpty(json.getString("processor"))))
            throw new ResultException(136,"上传处理器文件不能为空。");

        checkName(json);
        //先查询当前接口有几个处理器
        List<Map> handlers = handlerMapper.getHandlerList(json.getInteger("interfaceId"));
        Map map = new HashMap();
        map.put("name",json.getString("name"));
        map.put("processor",json.getString("processor"));
        map.put("interfaceId",json.getInteger("interfaceId"));
        map.put("sort",handlers.size()+1);
        map.put("state",StringUtils.isNotBlank(json.getString("state"))?json.getString("state"):"1");
        if(handlerMapper.insertHandler(map) != 1)
            throw new ResultException(143,"新增处理器失败，请稍后再试。");
        return handlerMapper.checkName(json.getString("name"));
    }

    /* 处理器名称不能重复判断
    * */
    public boolean checkName(JSONObject json) throws ResultException{

        Map<String, Object> name = handlerMapper.checkName(json.getString("name"));
        if(MapUtil.isNotEmpty(name))
            throw new ResultException(140,"处理器的名称不能重复。");
        return true;
    }

    /* 删除处理器
    * */
    public String delete(JSONObject json) throws ResultException{

        if(!json.containsKey("id") || json.get("id") == null)
            throw new ResultException(138,"删除处理器，必须参数id不能为空。");
        handlerMapper.deleteHandler(json.getInteger("id"));
        return "删除处理器成功";

    }

    /* 处理器排序,向上
    * */
    @Transactional
    public List<Map> sortUp(JSONObject json) throws ResultException{

        //排序必须参数 id,
        Map map = handlerMapper.getHandlerOne(json.getInteger("id"));
        Integer sort = Integer.valueOf(map.get("sort").toString());
        Map sortMap = new HashMap();
        sortMap.put("interfaceId",map.get("interfaceId"));
        sortMap.put("sort", sort-1);
        handlerMapper.updateSortUp(sortMap);
        handlerMapper.updateSortUpWithId(map);
        return handlerMapper.getHandlerList(Integer.valueOf(map.get("interfaceId").toString()));
    }

    /* 处理器排序,向下
    * */
    public List<Map> sortLow(JSONObject json) throws ResultException{

        //排序必须参数 id,
        Map map = handlerMapper.getHandlerOne(json.getInteger("id"));
        int sort = Integer.valueOf(map.get("sort").toString());
        Map sortMap = new HashMap();
        sortMap.put("interfaceId",map.get("interfaceId"));
        sortMap.put("sort", sort+1);
        handlerMapper.updateSortLow(sortMap);
        handlerMapper.updateSortLowWithId(map);
        return handlerMapper.getHandlerList(Integer.valueOf(map.get("interfaceId").toString()));
    }

    public Map update(JSONObject json) throws ResultException{

        if(!json.containsKey("id"))
            throw new ResultException(141,"修改处理器，必要参数id不能为空。");

        if(!json.containsKey("processor") && !json.containsKey("state") && !json.containsKey("name")){
            throw new ResultException(142,"修改处理器，名称、处理器jar包、状态不能全部为空。");
        }
        Map map = handlerMapper.getHandlerOne(json.getInteger("id"));
        Map updateMap = CommonUtil.JsonToMap(json);
        updateMap.put("sort",map.get("sort"));
        if(handlerMapper.update(updateMap) !=1 )
            throw new ResultException(144,"处理器修改失败，请稍后重试。");
        return handlerMapper.getHandlerOne(json.getInteger("id"));
    }

}
