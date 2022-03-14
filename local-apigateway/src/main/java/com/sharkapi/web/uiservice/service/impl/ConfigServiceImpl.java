package com.sharkapi.web.uiservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sharkapi.web.uiservice.mapper.UserMapper;
import com.sharkapi.web.uiservice.pojo.ResultException;
import com.sharkapi.web.uiservice.service.IndexService;
import com.sharkapi.web.uiservice.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

@Service("ConfigService")
public class ConfigServiceImpl implements IndexService {

    @Resource
    private UserMapper userMapper;

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

        if("update".equals(method)){
            result = update(json);
        } else if("get".equals(method)){
            result = get();
        } else{
            throw new ResultException(94,"未实现的接口，请检查。");
        }
        return result;
    }

    public String update(JSONObject json) throws ResultException{

        if(!json.containsKey("vcode") && !json.containsKey("domainName") && !json.containsKey("ts")){
            throw new ResultException(102,"接口请求不能全部为空。");
        }

        Map map = CommonUtil.JsonToMap(json);
        if(!json.containsKey("vcode") || (json.containsKey("vcode") && StringUtils.isBlank(json.getString("vcode")))){
            map.put("vcode",null);
        }
        if(userMapper.saveConfig(map) != 1)
            throw new ResultException(103,"修改网关配置失败，请稍后重试。");

        return "修改网关配置成功";
    }

    public Map get() throws ResultException{

        return userMapper.getConfig();
    }


}
