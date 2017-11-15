package com.huashi.developer.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.util.SecurityUtil;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.constants.OpenApiCode.CommonApiCode;

public class SmsP2PTemplateMessageTest {

	String url;
	FormBody.Builder bulider;
	String apptype;

	@Before
	public void before() {
		url = "http://dev.hspaas.cn:8080/sms/p2p_template";
//		url = "http://127.0.0.1:8080/sms/p2p_template";
		
//		String appkey = "hsjXxJ2gO75iOK";
//		String password = "e3293685e23847fce6a8afc532de6dac";
		
		String appkey = "hsjXxJ2gO75iOK";
		String password = "468134467dac9849da8902ff953b2d6c";
		
		
//		String mobile = "158" + RandomUtil.getRandomNum(8);
//		String mobile = "18368031231";
		
		String content = "【华时科技】#args#，您的#args#工资信息，实际工作天数：#args#天，应发放工资：#args#元，实发工资#args#元。";
		String time = System.currentTimeMillis() + "";
		
		List<JSONObject> list = new ArrayList<>();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("mobile", "15868193450");
		jsonObject.put("args", new Object[]{"张三", "2017年2月", 22, 5000, 5000});
		list.add(jsonObject);
		
		jsonObject = new JSONObject();
		jsonObject.put("mobile", "15817819265");
		jsonObject.put("args", new Object[]{"李四", "2017年2月", 20, 6700, 6500});
		list.add(jsonObject);
		
		apptype = AppType.DEVELOPER.getCode() + "";
		
		System.out.println("报文内容："+ JSON.toJSONString(list));
		
//		1467735756927
		bulider = new FormBody.Builder().add("appkey", appkey)
				.add("appsecret", SecurityUtil.md5Hex(password + time))
				.add("timestamp", time)
				.add("content", content)
				.add("body", JSON.toJSONString(list))
				.add("attach", "test889")
				.add("extNumber", "333")
				.add("callback", "http://localhost:9999/sms/status_report");
		
	}

	@Test
	public void test() {
		Map<String, Object> result = JSON.parseObject(post(), new TypeReference<Map<String, Object>>(){});
		
		System.out.println(JSON.toJSONString(result));
		
		Assert.assertTrue(result.get("code").toString(), result.get("code").toString().equals(CommonApiCode.COMMON_SUCCESS.getCode()+""));
	}

//	String post(String url, String content) {
//
//		Request request = new Request.Builder()
//				.url(url)
//				.post(RequestBody.create(
//						MediaType.parse("text/html; charset=utf-8"), content))
//				.build();
//		try {
//			Response response = new OkHttpClient().newCall(request).execute();
//			if (response.isSuccessful())
//				return response.body().string();
//			else
//				System.err.println("URL:{}, 回执失败");
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//		throw new RuntimeException(String.format("URL: %s 调用失败！", url));
//	}

	private String post() {
		Request request = new Request.Builder().url(url).post(bulider.build()).addHeader("apptype", apptype)
				.build();
		try {
			Response response = new OkHttpClient().newCall(request).execute();
			if (response.isSuccessful())
				return response.body().string();
			else
				System.err.println("URL:{}, 回执失败");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		throw new RuntimeException(String.format("URL: %s 调用失败！", url));
	}

}
