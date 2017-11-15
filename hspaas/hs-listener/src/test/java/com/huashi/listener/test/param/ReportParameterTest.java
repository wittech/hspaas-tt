package com.huashi.listener.test.param;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.huashi.listener.util.ParameterUtil;

public class ReportParameterTest {

	public static void main(String[] args) throws UnsupportedEncodingException {
		Map<String, String[]> properties = new HashMap<>();
		String[] mid = {"222"};
		String[] mobile = {"158222"};
		
		System.out.println(java.net.URLEncoder.encode("你好，华时", "GBK"));
		System.out.println(java.net.URLEncoder.encode("你好，华时", "UTF-8"));
		String[] msg = {java.net.URLEncoder.encode("你好，华时", "GBK")};
		
		properties.put("mid", mid);
		properties.put("mobile",mobile);
		properties.put("msg", msg);
		
		JSONObject o = ParameterUtil.transform(properties, "g");
		System.out.println(o.toJSONString());
	}
}
