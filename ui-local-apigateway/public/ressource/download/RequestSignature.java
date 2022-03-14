package com.sharkapi.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;

/**
 * 请求签名示例JAVA代码 运行时须加参数-Dfile.encoding=utf-8，否则签名结果会不正确
 * 
 * @author shark
 */
public class RequestSignature {

	public static void main(String[] args) throws InterruptedException {
		// 示例调用
		String url = "http://127.0.0.1:56888/sharkapiservice/sale/daily_report";
		String password = "hsdA437Se2p3hjdh";
		String postBody = "{\"name\":\"中国%\"}";
		// 签名请求数据
		Map<String, String> urlMap = signature(password, postBody);
		List<String> params = new ArrayList<String>();
		for (String key : urlMap.keySet()) {
			params.add(key + "=" + urlMap.get(key));
		}
		url = url + "?" + String.join("&", params);
		// 模拟签名后延迟时间调用
		Thread.sleep(1000);
		// 调用api服务
		System.out.println(request(url, postBody));
	}

	/*
	 * 请求API网关服务
	 */
	public static String request(String url, String postBody) {
		HttpRequest httpRequest = HttpUtil.createPost(url);
		String result = httpRequest.body(postBody).execute().body();
		return result;
	}

	/*
	 * 对请求进行签名
	 */
	public static Map<String, String> signature(String securityCode, String postBody) {
		Map<String, String> urlMap = new HashMap<String, String>();
		if (securityCode == null || securityCode.length() < 1) {
			// 无安全码，不需要签名
			return urlMap;
		}
		// 得到当前时间截
		String ts = String.valueOf(new Date().getTime());
		// 得到签名的md5值
		String md5Str = "";
		try {
			String postBodyStr = postBody == null ? "" : postBody;
			md5Str = SecureUtil.md5(ts + securityCode + postBodyStr);
		} catch (Exception ex) {
			throw ex;
		}
		// 返回签名数据，需要作为URL请求参数附加上
		urlMap.put("signature", md5Str);
		urlMap.put("signaturebyts", ts);
		return urlMap;
	}
}
