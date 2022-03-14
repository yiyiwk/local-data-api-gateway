package com.sharkapi.web.uiservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.sharkapi.web.uiservice.config.SpringContextConfiguration;
import com.sharkapi.web.uiservice.pojo.ResultException;
import com.sharkapi.web.uiservice.pojo.ResultModel;
import com.sharkapi.web.uiservice.service.IndexService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;

/* 统一的入口
 * */
@RestController
public class IndexController {

    @PostMapping(path = "/sharkapiweb/uiservice/{module}/{method}", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ResultModel handlePost(
            @PathVariable(value = "module", required = true) String module,
            @PathVariable(value = "method", required = true) String method,
            @RequestBody(required = false) String bodyData,
            @RequestParam(value = "sharkdata", required = false) String paramData,
            @RequestHeader(value = "token", required = true) String token,
            MultipartFile file) {

        ResultModel result = new ResultModel();
        String data = StringUtils.isEmpty(bodyData) ? paramData : bodyData;
        //接口请求，判断是否需要token
        if (!"User".equals(module) && StringUtils.isEmpty(token)) {
            result.setCode(97);
            result.setMessage("用户身份校验失败，请退出重新登录。");
            return result;
        } else if (!"User".equals(module) || ("User".equals(module) && "goOut".equals(method))) {
            //token的校验
            JSONObject json = new JSONObject();
            json.put("token", token);
            ResultModel resultModel = tokenCheck(json.toJSONString());
            if (!(boolean) resultModel.getData()) {
                result.setCode(93);
                result.setMessage("用户令牌校验错误，请退出重新登录。");
                return result;
            }
            if ("goOut".equals(method)) {
                data = json.toJSONString();
            }
        }

        //得到处理服务
        String serviceName = new StringBuffer().append(module).append("Service").toString();
        IndexService service = SpringContextConfiguration.getBean(serviceName, IndexService.class);
        if (service == null) {
            result.setCode(95);
            result.setMessage("服务（bean=" + serviceName + "）未实现");
            return result;
        }
        try {
            result.setData(service.service(method, data, file));
        } catch (ResultException ex) {
            result.setCode(ex.getState());
            result.setMessage(ex.getMessage());
        } catch (Exception e) {
            result.setCode(99);
            result.setMessage("系统异常，请联系管理员或者重启服务。");
        }
        result.setTimestamp(new Date().getTime());
        return result;
    }

    /* token校验
     * */
    public static ResultModel tokenCheck(String data) {
        ResultModel result = new ResultModel();
        IndexService service = SpringContextConfiguration.getBean("UserService", IndexService.class);
        Object flag = null;
        try {
            flag = service.service("tokenCheck", data, null);
            result.setData((boolean) flag);
        } catch (Exception e) {
            result.setData(false);
            result.setCode(93);
            result.setMessage("用户令牌校验错误，请退出重新登录。");
        }
        return result;
    }

}
