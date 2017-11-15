package com.huashi.developer.test;

import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.util.SecurityUtil;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.constants.OpenApiCode.CommonApiCode;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SmsBalanceTest {

	String url;
	FormBody.Builder bulider;
	String apptype;

	@Before
	public void before() {
		url = "http://localhost:8080/sms/balance";
		
		String appkey = "hsjXxJ2gO75iOK1";
		String password = "e3293685e23847fce6a8afc532de6dac";
		
		String time = System.currentTimeMillis() + "";
		
//		1467735756927
		bulider = new FormBody.Builder().add("appkey", appkey)
				.add("appsecret", SecurityUtil.md5Hex(password + time))
				.add("timestamp", time);
		
		apptype = AppType.BOSS.getCode() + "";
	}

	@Test
	public void test() {
		Map<String, Object> result = JSON.parseObject(post(), new TypeReference<Map<String, Object>>(){});
		
		System.out.println(result);
		
		Assert.assertTrue(result.get("code").toString(), result.get("code").toString().equals(CommonApiCode.COMMON_SUCCESS.getCode()+""));
	}

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
