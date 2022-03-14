package com.sharkapi.web.uiservice.service.impl;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.sharkapi.web.uiservice.mapper.InterfaceMapper;
import com.sharkapi.web.uiservice.mapper.UserMapper;
import com.sharkapi.web.uiservice.pojo.ResultException;
import com.sharkapi.web.uiservice.service.IndexService;
import com.sharkapi.web.uiservice.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("InterfaceService")
public class InterfaceServiceImpl implements IndexService {

    @Resource
    private InterfaceMapper interfaceMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public Object service(String method, String paramData, MultipartFile file) throws ResultException {

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
        } else if("checkPath".equals(method)){
            result = checkPath(json);
        } else if("path".equals(method)){
            result = path(json);
        } else {
            throw new ResultException(94,"未实现的接口，请检查。");
        }

        return result;
    }



    /* 获取全部API接口
    * */
    public List<Map> all() throws ResultException{

        //展示列表：需要将路径改成全路径返回，方便使用
        List<Map> all = interfaceMapper.all();
        Map config = userMapper.getConfig();
        String domainName = null;
        if(config.containsKey("domainName") && config.get("domainName") != null){
            String path = config.get("domainName").toString();
            while("/".equals(path.substring(path.length()-1))){
                path = path.substring(0,path.length()-1);
            }
            domainName = path;
        }
        String param = null;
        if(config.get("vcode") != null ){
            String vcode = config.get("vcode").toString();
            param = "?signature={sg#"+vcode+"#sg}";
        }

        for(int i=0;i<all.size();i++){
            Map p = all.get(i);
            StringBuffer buffer = new StringBuffer();
            if(StringUtils.isEmpty(domainName)){
                buffer.append("http://")
                        .append(CommonUtil.getLocalAddress()[0])
                        .append(":")
                        .append(CommonUtil.getPort());
            } else {
                buffer.append(domainName);
            }
            buffer.append("/sharkapiservice");
            if(p.containsKey("path") && p.get("path").toString().startsWith("/")){
                buffer.append(p.get("path"));
            } else {
                buffer.append("/");
                buffer.append(p.get("path"));
            }
            if(StringUtils.isNotEmpty(param)){
                buffer.append(param);
            }
            p.put("fullUrl",buffer.toString());
            p.put("dataitemNum",interfaceMapper.getSumDataitem(Integer.valueOf(all.get(i).get("id").toString())));
            p.put("handlerNum",interfaceMapper.getSumHandler(Integer.valueOf(all.get(i).get("id").toString())));
            p.put("paramHandlerNum",interfaceMapper.getSumParamHandler(Integer.valueOf(all.get(i).get("id").toString())));
        }
        return all;
    }

    /* 获取path的全路径
    * */
    public String path(JSONObject json) throws ResultException{

        if(!json.containsKey("path") || StringUtils.isEmpty(json.getString("path"))){
            throw new ResultException(190,"path路径不能为空。");
        }
        Map config = userMapper.getConfig();
        String domainName = null;
        if(config.containsKey("domainName") && config.get("domainName") != null){
            String path = config.get("domainName").toString();
            while("/".equals(path.substring(path.length()-1))){
                path = path.substring(0,path.length()-1);
            }
            domainName = path;
        }
        String param = null;
        if(config.get("vcode") != null ){
            String vcode = config.get("vcode").toString();
            param = "?signature={sg#"+vcode+"#sg}";
        }
        StringBuffer buffer = new StringBuffer();
        if(StringUtils.isEmpty(domainName)){
            buffer.append("http://")
                    .append(CommonUtil.getLocalAddress()[0])
                    .append(":")
                    .append(CommonUtil.getPort());
        } else {
            buffer.append(domainName);
        }
        buffer.append("/sharkapiservice");
        if(json.containsKey("path") && json.get("path").toString().startsWith("/")){
            buffer.append(json.getString("path"));
        } else if(json.containsKey("path") && json.get("path").toString().startsWith("\\")){
            buffer.append(json.getString("path").replace("\\","/"));
        } else {
            buffer.append("/");
            buffer.append(json.getString("path"));
        }

        if(StringUtils.isNotEmpty(param)){
            buffer.append(param);
        }

        return buffer.toString();
    }

    /* 新建API接口
    * */
    public Map insert(JSONObject json) throws ResultException{
        if(!json.containsKey("path") || StringUtils.isEmpty(json.getString("path")))
            throw new ResultException(181,"新增API接口path路径不为空。");

        if(!json.containsKey("name") || StringUtils.isEmpty(json.getString("name")))
            throw new ResultException(183,"新增API接口名称不为空。");

        String path = json.getString("path");  //如果是 /aa/bb,替换成aa/bb
        if(path.startsWith("/")){
            json.replace("path",path.replaceFirst("/",""));
        }
        json.replace("path",json.getString("path").trim());
        //首先查询path路径不重复
        Map map = CommonUtil.JsonToMap(json);
        checkPath(json);
        checkName(json);

        try{
            if(interfaceMapper.insertInterface(map) != 1)
                throw new ResultException(185,"新增API接口失败，请重试。");

            Map one = interfaceMapper.getNameSame(json.getString("name"));
            one.put("fullUrl",path(json));
            return  one;

        }catch(Exception e){
            throw new ResultException(185,"新增API接口失败，请重试。");
        }
    }

    /* 检查path路径不重复
    * */
    public boolean checkPath(JSONObject json) throws ResultException{

        Map path = interfaceMapper.getPathSame(json.getString("path"));
        if(MapUtil.isNotEmpty(path)){
            throw new ResultException(182,"API接口path不能重复，请检查。");
        }
        return true;
    }

    /* 检查名称不重复
     * */
    public boolean checkName(JSONObject json) throws ResultException{

        Map name = interfaceMapper.getNameSame(json.getString("name"));
        if(MapUtil.isNotEmpty(name)){
            throw new ResultException(186,"API接口名称不能重复，请检查。");
        }
        return true;
    }

    /* 修改API接口
    * */
    public Map<String, Object> update(JSONObject json) throws ResultException{
        if(!json.containsKey("id") || StringUtils.isEmpty(json.getString("id")))
            throw new ResultException(188,"修改API接口，必要参数不为空。");

        if(!json.containsKey("path") && !json.containsKey("name")
         && !json.containsKey("represent") && !json.containsKey("state") )
            throw new ResultException(184,"修改API，路径、名称、描述和状态不能全部为空。");
        Map map = CommonUtil.JsonToMap(json);

        if(json.containsKey("path")){
            Map path = interfaceMapper.getPathSame(json.getString("path"));
            if(path != null && !json.getInteger("id").equals(path.get("id"))){
                throw new ResultException(182,"API接口path不能重复，请检查。");
            }
        }
        if(json.containsKey("name")){
            Map name = interfaceMapper.getNameSame(json.getString("name"));
            if(name != null && !json.getInteger("id").equals(name.get("id"))){
                throw new ResultException(186,"API接口名称不能重复，请检查。");
            }
        }
        int num = interfaceMapper.updateInterface(map);
        if(num == 1){
            return interfaceMapper.getInterfaceOne(json.getInteger("id"));
        }else {
            throw new ResultException(187,"API接口修改失败，请检查。");
        }
    }

    /* 删除API接口
    * */
    public String delete(JSONObject json) throws ResultException{

        if(!json.containsKey("interfaceId") || StringUtils.isEmpty(json.getString("interfaceId")))
            throw new ResultException(185,"删除API接口参数错误。");
        if(interfaceMapper.deleteInterface(json.getInteger("interfaceId")) == 1){
            return "删除API接口成功";
        } else {
            throw new ResultException(189,"删除API接口失败，请稍后重试。");
        }
    }

}
