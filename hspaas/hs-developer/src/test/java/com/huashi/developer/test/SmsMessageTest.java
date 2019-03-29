package com.huashi.developer.test;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.util.RandomUtil;
import com.huashi.common.util.SecurityUtil;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.constants.OpenApiCode.CommonApiCode;

public class SmsMessageTest {

	String url;
	FormBody.Builder bulider;
	String apptype;

	@Before
	public void before() {
//		url = "http://dev.hspaas.cn:8080/sms/send";
		url = "http://127.0.0.1:8080/mms/send";

//		String appkey = "hsjXxJ2gO75iOK";
//		String password = "468134467dac9849da8902ff953b2d6c";

		String appkey = "hsjXxJ2gO75iOK";
		String password = "e3293685e23847fce6a8afc532de6dac";
		//华时boss
//		String appkey = "hsXBrANoesxLLu";
//		String password = "df2b6e29685a422bd2a9117786280e8f";


//		String mobile = "158" + RandomUtil.getRandomNum(8);
//		String mobile = "18368031231";
		String mobile = "15868193450";

		String time = System.currentTimeMillis() + "";

		apptype = AppType.WEB.getCode() + "";

		String content = String.format("【华时科技】您的短信验证码为：%s", RandomUtil.getRandomNum());

		System.out.println("短信内容："+content);

		bulider = new FormBody.Builder().add("appkey", appkey)
				.add("appsecret", SecurityUtil.md5Hex(password + mobile + time))
				.add("timestamp", time)
				.add("mobile", mobile)
				.add("content", content)
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
