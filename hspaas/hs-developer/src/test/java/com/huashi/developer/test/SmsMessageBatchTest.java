package com.huashi.developer.test;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.util.RandomUtil;
import com.huashi.common.util.SecurityUtil;
import com.huashi.constants.CommonContext.AppType;

public class SmsMessageBatchTest {

	public static void send() {
		
		String url = "http://dev.hspaas.cn:8080/sms/send";
//		String url = "http://localhost:8080/sms/send";
		String password = "8854d3a11f7fa220ef22896ef25cd213";
		String mobile = "158" + RandomUtil.getRandomNum(8);
		String time = System.currentTimeMillis() + "";

		String apptype = AppType.WEB.getCode() + "";

		String content = String.format("【华时科技】您的短信验证码为%s，请尽快完成后续操作。", RandomUtil.getRandomNum());

		System.out.println("短信内容：" + content);

		FormBody.Builder bulider = new FormBody.Builder().add("appkey", "b7064fe2115b46a09b39611520de305a")
				.add("appsecret", SecurityUtil.md5Hex(password + mobile + time)).add("timestamp", time)
				.add("mobile", mobile).add("content", content).add("attach", "test889")
				.add("callback", "http://localhost:9999/sms/status_report");

		Map<String, Object> result = JSON.parseObject(post(url, bulider, apptype),
				new TypeReference<Map<String, Object>>() {
				});

		System.out.println(result.get("code").toString());
	}

	@Before
	public void before() {
		// url = "http://dev.hspaas.cn:8080/sms/send";

	}

	public static void main(String[] args) {
		ExecutorService service = Executors.newFixedThreadPool(100);

		long time = System.currentTimeMillis();
		service.execute(new Runnable() {
			public void run() {

				for (int i = 0; i < 10; i++) {
					send();
				}
			}
		});
		System.out.println(System.currentTimeMillis() - time);
	}

	@Test
	public void test() {

		ExecutorService service = Executors.newFixedThreadPool(100);

		service.execute(new Runnable() {
			public void run() {

				for (int i = 0; i < 100; i++) {
					send();
				}
			}
		});

		service.shutdown();

	}

	// String post(String url, String content) {
	//
	// Request request = new Request.Builder()
	// .url(url)
	// .post(RequestBody.create(
	// MediaType.parse("text/html; charset=utf-8"), content))
	// .build();
	// try {
	// Response response = new OkHttpClient().newCall(request).execute();
	// if (response.isSuccessful())
	// return response.body().string();
	// else
	// System.err.println("URL:{}, 回执失败");
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// }
	// throw new RuntimeException(String.format("URL: %s 调用失败！", url));
	// }

	private static String post(String url, FormBody.Builder bulider, String apptype) {
		Request request = new Request.Builder().url(url).post(bulider.build()).addHeader("apptype", apptype).build();
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
