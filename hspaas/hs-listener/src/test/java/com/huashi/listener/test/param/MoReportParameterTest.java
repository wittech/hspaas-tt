package com.huashi.listener.test.param;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MoReportParameterTest {

	public static void main(String[] args) throws UnsupportedEncodingException {
		// 下行Map方式解析
//		mid=235940751&type=1&msg=%E0%DE%E0%DE%E0%DE%E0%DE%E0%DE%E0%DE%E0%DE%E0%DE%C5%B6%C5%B6%C5%B6&mobile=18368031231&port=10690654167860061234&area=&city=
		Map<String, String[]> properties = new HashMap<String, String[]>();
//		properties.put("type", new String[]{"4"});
//		properties.put("msg", new String[]{"DELIVRD"});
//		properties.put("mobile", new String[]{"18368031231"});
//		properties.put("mid", new String[]{"235928749"});
//		properties.put("port", new String[]{"60061234"});
//		properties.put("area", new String[]{"%D5%E3%BD%AD"});
//		properties.put("city", new String[]{"%CE%B4%D6%AA"});
//		
//		JSONObject json = ParameterUtil.transform(properties, "g");
//		System.out.println(json.toJSONString());
		
		// 上行Map方式解析
		properties = new HashMap<String, String[]>();
		properties.put("mid", new String[]{"235940751"});
		properties.put("type", new String[]{"1"});
		properties.put("msg", new String[]{"%E0%DE%E0%DE%E0%DE%E0%DE%E0%DE%E0%DE%E0%DE%E0%DE%C5%B6%C5%B6%C5%B6"});
		properties.put("mobile", new String[]{"18368031231"});
		properties.put("port", new String[]{"10690654167860061234"});
		
//		JSONObject json = ParameterUtil.transform(properties, "g");
//		System.out.println(json.toJSONString());
		
//		String text = "%CE%B4%D6%AA";
//		System.out.println(java.net.URLDecoder.decode(text, "GBK"));
//		System.out.println(java.net.URLEncoder.encode("未知", "GBK"));
		
		String plain = "%E0%DE%E0%DE%E0%DE%E0%DE%E0%DE%E0%DE%E0%DE%E0%DE%C5%B6%C5%B6%C5%B6";
		System.out.println(java.net.URLDecoder.decode(plain, "GBK"));
		System.out.println(new String(plain.getBytes("ISO-8859-1"), "GBK") );
		
		String text = "����������������ŶŶŶ";
		System.out.println(new String(text.getBytes("UTF-8"), "GBK") );
		System.out.println(new String(text.getBytes("ISO-8859-1"), "UTF-8") );
		
		
		
		System.out.println(java.net.URLDecoder.decode(java.net.URLDecoder.decode(text, "UTF-8"),"UTF-8"));
	}
}
