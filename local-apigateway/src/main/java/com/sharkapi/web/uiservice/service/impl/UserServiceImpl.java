package com.sharkapi.web.uiservice.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.sharkapi.web.uiservice.pojo.ResultException;
import com.sharkapi.web.uiservice.service.IndexService;
import com.sharkapi.web.uiservice.util.EncryptUtil;
import com.sharkapi.web.uiservice.mapper.UserMapper;
import com.sharkapi.web.uiservice.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("UserService")
public class UserServiceImpl implements IndexService {

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

        if("getUser".equals(method)){
            result = getUser(json);
        } else if("passwordModify".equals(method)){
            result = passwordModify(json);
        } else if("goOut".equals(method)){
            result = goOut(json);
        } else if("tokenCheck".equals(method)){
            result = tokenCheck(json.getString("token"));
        } else if("vcodeCheck".equals(method)){
            result = vcodeCheck(json);
        } else if("time".equals(method)){
            result = timeCheck();
        }
        else{
            throw new ResultException(94,"未实现的接口，请检查。");
        }

        return result;
    }


    /* 用户密码修改
    * */
    public String passwordModify(JSONObject json) throws ResultException{

        if(StringUtils.isEmpty(json.getString("username")) ||
                StringUtils.isEmpty(json.getString("password"))){
            throw new ResultException(111,"修改用户密码，密码不能为空。");
        }
        Map map = CommonUtil.JsonToMap(json);
        String password = json.get("password").toString();
        map.replace("password", EncryptUtil.encryptMD5String(password));
        int num = userMapper.updatePassword(map);
        if(num != 1){
            throw new ResultException(112,"修改密码失败，请稍后再试。");
        }
        if(json.containsKey("firstIn") && 0 == json.getInteger("firstIn")){
            Map tokenUser = createToken(1);
            return tokenUser.get("token").toString();
        } else {
            return "密码修改成功";
        }
    }

    /*用户登录，获取用户
    * */
    public Map getUser(JSONObject json) throws ResultException {
        Map map = CommonUtil.JsonToMap(json);
        Map user = userMapper.getUser(map);
        if(user != null && user.containsKey("password") && user.get("password") != null){
            //用户已经修改过密码
            if(!json.containsKey("password") || json.getString("password") == null)
                throw new ResultException(101,"用户输入密码不正确，请重试。");

            String password = EncryptUtil.encryptMD5String(map.get("password").toString());
            if(password.equals(user.get("password"))){
                //用户输入密码正确
                //登录成功，需要返回token
                Map tokenUser = createToken(Integer.valueOf(user.get("id").toString()));
                map.put("token",tokenUser.get("token"));
                map.put("firstIn",1);
                map.remove("password");
                //登录成功，去查询是否有数据源和API列表
                //根据有无，首页跳转至不同的导航
                map.put("datamartNum",userMapper.getDataAndInterNum());
                return map;
            } else {
                throw new ResultException(101,"用户输入密码不正确，请重试。");
            }
        } else if(user != null && (!user.containsKey("password") || user.get("password") == null)){
            //用户登录没有修改过密码，需要首先重置密码
            map.remove("password");
            map.put("token",null);
            map.put("firstIn",0);
            return map;
        } else{
            throw new ResultException(102,"默认用户为dsadmin，不存在其他用户。");
        }
    }

    /* 用户退出登录，销毁token
    * */
    public String goOut(JSONObject json) throws ResultException{
        if(userMapper.deleteToken(json.getString("token")) != 1)
            throw new ResultException(109,"用户退出系统失败。");
        return "退出系统成功";
    }

    /* token的验证
     * */
    public boolean tokenCheck(String token){
        List<Map> list = userMapper.checkToken(token);
        if(list.size() > 0){
            return true;
        }
        return false;
    }

    /* 生成token
    * */
    public Map createToken(int userId) throws ResultException{
        Map tokenMap = new HashMap();
        tokenMap.put("createTime",new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        tokenMap.put("token",CommonUtil.getCode(32));
        tokenMap.put("userId",userId);
        try{
            int tokenId = userMapper.insertToken(tokenMap);
            tokenMap.put("id",tokenId);
        }catch(Exception e){
           throw new ResultException(110,"用户登录令牌生成错误，请稍后再试。");
        }

        return tokenMap;
    }

    public  boolean vcodeCheck(JSONObject json){

        Map config = userMapper.getConfig();
        String code = null;
        if(config.containsKey("vcode") && config.get("vcode") != null){
            code = config.get("vcode").toString();
        } else {
            return true;
        }
        String md5Str = json.getString("time")+code+json.getString("param");
        String md5 = SecureUtil.md5(md5Str);
        if(md5.equals(json.getString("vcode"))){
            return true;
        }
        return false;
    }

    public int timeCheck(){

        Map config = userMapper.getConfig();
        return (int)config.get("ts");
    }


}
