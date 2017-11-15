package com.huashi.developer.test.demo;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class MessageSendTest {

	public static String md5(String src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] bs = md5.digest(src.getBytes());
			return new String(new Hex().encode(bs));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param url
	 *            ：必填--发送连接地址URL--比如>http://139.129.128.71:8086/msgHttp/json/mt
	 * 
	 * @param appkey
	 *            ：必填--用户帐号
	 * @param password
	 *            ：必填--数字签名：(接口密码、时间戳32位MD5加密生成)
	 * @param mobile
	 *            ：必填--发送的手机号码，多个可以用逗号隔比如>13512345678,13612345678
	 * @param content
	 *            ：必填--实际发送内容
	 * @return 返回发送之后收到的信息
	 */
	public static String sendSms(String url, String appkey, String password, String mobile, String content) {
		String resultContent = "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;

		HttpPost httpPost = new HttpPost(url);
		List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
		long timestamp = System.currentTimeMillis();

		formparams.add(new BasicNameValuePair("appkey", appkey));
		formparams.add(new BasicNameValuePair("appsecret", md5(password + mobile + timestamp)));
		formparams.add(new BasicNameValuePair("mobile", mobile));
		formparams.add(new BasicNameValuePair("content", content));
		formparams.add(new BasicNameValuePair("timestamp", timestamp + ""));

		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httpPost.setEntity(uefEntity);
			CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				resultContent = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultContent;
	}
	
	public static void main(String[] args) {
		String url = "http://api.hspaas.cn:8080/sms/send";
		String appkey = "接口账号";
		String password = "接口密码";
		String mobile = "158****3450";
		String content = "【华时科技】您的验证码是:036138!";
		
		System.out.println(sendSms(url, appkey, password, mobile, content));
	}
}
