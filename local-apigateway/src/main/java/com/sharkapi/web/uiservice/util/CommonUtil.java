package com.sharkapi.web.uiservice.util;


import com.alibaba.fastjson.JSONObject;
import com.sharkapi.web.uiservice.pojo.ResultException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class CommonUtil {

    private static String PORT;

    private static String DOMAIN;

    @Value("${server.port}")
    public void setPath(String port){
        CommonUtil.PORT = port;
    }

    @Value("${api.domain}")
    public void setDoamin(String name){
        CommonUtil.DOMAIN = name;
    }

    public static String getPort(){
        return PORT;
    }
    public static String getDOMAIN(){
        return DOMAIN;
    }

    /*json 转换成map
     * */
    public static Map<String, Object> JsonToMap(JSONObject json) {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> iterator = json.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value = json.get(key);
            map.put(key, value);
        }
        return map;
    }

    /* 获取主机的ip和hostName
     * */
    public static String[] getLocalAddress() throws ResultException {
        String[] str = new String[2];
        try {
            InetAddress addr = InetAddress.getLocalHost();
            str[0] = addr.getHostAddress();
            str[1] = addr.getHostName();
        } catch (Exception e) {
            throw new ResultException(98, "获取主机IP和主机名错误。");
        }
        return str;
    }

    /** 随机生成由数字、字母组成的N位验证码
     */
    public static String getCode(int n) {
        char arr[] = new char[n];
        int i = 0;
        while (i < n) {
            char ch = (char) (int) (Math.random() * 124);
            if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9') {
                arr[i++] = ch;
            }
        }
        return new String(arr);
    }





}
