package com.sharkapi.service.controller;

import com.alibaba.fastjson.JSONObject;
import com.sharkapi.web.uiservice.config.SpringContextConfiguration;
import com.sharkapi.web.uiservice.pojo.ResultException;
import com.sharkapi.web.uiservice.pojo.ResultModel;
import com.sharkapi.web.uiservice.service.IndexService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/* 统一的外部请求
* */
@RestController
public class OutSideController {

    /* 请求 vcode验证  url
     * url中时间戳(signaturebyts)+vcode+参数提body  MD5  整个为签名（signature）
     * */
    @PostMapping(path = "/sharkapiservice/**")
    @ResponseBody
    public ResultModel handlePost(@RequestBody(required=false) String paramData,
                                  HttpServletResponse response){

        ResultModel result = new ResultModel();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String url = request.getRequestURI();
        String timestamp = request.getParameter("signaturebyts");
        String signature = request.getParameter("signature");   //vcode签名之后的字符串
        ResultModel vcodecheck = getVcodeSheck(timestamp,signature,paramData);
        boolean flag = (boolean)vcodecheck.getData();
        if(!flag){
            result.setCode(91);
            result.setMessage("接口签名校验错误，请联系管理员。");
            return result;
        }
        int date = (int)getTimeStamp().getData();
        if(StringUtils.isEmpty(timestamp) && date != 1){
            result.setCode(89);
            result.setMessage("时间戳不能为空。");
            return result;
        }
        try{
            if(StringUtils.isNotBlank(timestamp)){
                if(!getTimeCheck(Long.valueOf(timestamp).longValue(),date)){
                    result.setCode(92);
                    result.setMessage("时间戳格式校验错误，请检查网关时间戳配置。");
                    return result;
                }
            }
        }catch(Exception e){
            result.setCode(92);
            result.setMessage("时间戳格式错误，请检查。");
            return result;
        }

        String serviceName = new StringBuffer().append("OutsideService").toString();
        IndexService service = SpringContextConfiguration.getBean(serviceName,IndexService.class);
        if(service == null){
            result.setCode(95);
            result.setMessage("服务（bean="+serviceName+"）未实现");
            return result;
        }
        try{
            result.setData(service.service(url,paramData,null));
        } catch(ResultException ex){
            result.setCode(ex.getState());
            result.setMessage(ex.getMessage());
        } catch(Exception e){
            result.setCode(99);
            result.setMessage("系统异常，请联系管理员或者重启服务。");
        }
        result.setTimestamp(new Date().getTime());
        return result;
    }

    public static ResultModel getVcodeSheck(String timestamp,String  vcode,String param){

        ResultModel result = new ResultModel();
        IndexService service = SpringContextConfiguration.getBean("UserService",IndexService.class);
        JSONObject json = new JSONObject();
        try{
            json.put("vcode",StringUtils.isBlank(vcode)?null:vcode);
            json.put("time",StringUtils.isBlank(timestamp)?null:timestamp);
            json.put("param",param);
            Object flag = service.service("vcodeCheck", json.toJSONString(), null);
            result.setData((boolean)flag);
        }catch(Exception e){
            result.setData(false);
            result.setCode(91);
            result.setMessage("接口签名校验错误，请联系管理员。");
        }
        return result;
    }


    public static ResultModel getTimeStamp(){

        ResultModel result = new ResultModel();
        IndexService service = SpringContextConfiguration.getBean("UserService",IndexService.class);
        JSONObject json = new JSONObject();
        try{
            Object flag = service.service("time", json.toJSONString(), null);
            result.setData((int)flag);
        }catch(Exception e){
            result.setData(false);
            result.setCode(90);
            result.setMessage("时间戳格式校验错误，请检查网关时间戳配置。");
        }
        return result;
    }

    public boolean getTimeCheck(long timestamp,int type){

        if(type == 1){
           return true;
        }
        Date stamp = new Date(timestamp);
        Date now = new Date();
        Date start = null;
        Date end = null;
        if(type == 2){
            start = new Date(now.getTime() - 1*60*1000l);
            end = new Date(now.getTime() + 1*60*1000l);
        } else if(type == 3){
            start = new Date(now.getTime() - 5*60*1000l);
            end = new Date(now.getTime() + 5*60*1000l);
        } else if(type == 4){
            start = new Date(now.getTime() - 10*60*1000l);
            end = new Date(now.getTime() + 10*60*1000l);
        } else if(type == 5){
            start = new Date(now.getTime() - 30*60*1000l);
            end = new Date(now.getTime() + 30*60*1000l);
        } else if(type == 6){
            start = new Date(now.getTime() - 60*60*1000l);
            end = new Date(now.getTime() + 60*60*1000l);
        } else if(type == 7){
            start = new Date(now.getTime() - 12*60*60*1000l);
            end = new Date(now.getTime() + 12*60*60*1000l);
        } else if(type == 8){
            start = new Date(now.getTime() - 24*60*60*1000l);
            end = new Date(now.getTime() + 24*60*60*1000l);
        }
        Calendar time = Calendar.getInstance();
        time.setTime(stamp);

        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(start);

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(end);

        if(stamp.getTime() == start.getTime() || stamp.getTime() == end.getTime()){
            return true;
        }

        if (time.after(beginTime) && time.before(endTime)) {
            return true;
        } else {
            return false;
        }
    }


}
