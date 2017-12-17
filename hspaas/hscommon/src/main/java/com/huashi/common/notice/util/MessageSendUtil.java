package com.huashi.common.notice.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.notice.vo.SmsResponse;
import com.huashi.common.util.SecurityUtil;
import com.huashi.constants.OpenApiCode;
import com.huashi.constants.OpenApiCode.CommonApiCode;

public class MessageSendUtil {
	
	private static Logger logger = LoggerFactory.getLogger(MessageSendUtil.class);
	
	/**
	 * @param url
	 *            ：必填--发送连接地址URL--比如>http://api.hspaas.cn:8080/sms/send
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
	public static SmsResponse sms(String url, String appkey, String password, String mobile, 
			String content) {
		String resultContent = "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;

		HttpPost httpPost = new HttpPost(url);
		List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
		long timestamp = System.currentTimeMillis();

		formparams.add(new BasicNameValuePair("appkey", appkey));
		formparams.add(new BasicNameValuePair("appsecret", SecurityUtil.md5Hex(password + mobile + timestamp)));
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
		} catch (IOException e) {
			logger.error("短信发送失败", e);
			return new SmsResponse(mobile, CommonApiCode.COMMON_SERVER_EXCEPTION.getCode(), 
					CommonApiCode.COMMON_SERVER_EXCEPTION.getMessage());
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
		return parse(mobile, resultContent);
	}
	
	/**
	 * 发送短信并返回TRUE或FALSE
	 * 
	 * @param url
	 * @param appkey
	 * @param appsecret
	 * @param mobile
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static boolean smsWithStatus(String url, String appkey, String appsecret, 
			String mobile, String content) throws Exception {
		try {
			SmsResponse response = sms(url, appkey, appsecret, mobile, content);
			return response != null && StringUtils.isNotEmpty(response.getCode()) 
					&& OpenApiCode.SUCCESS.equals(response.getCode());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 解析返回值
	 * @param mobile
	 * @param result
	 * @return
	 */
	public static SmsResponse parse(String mobile, String result) {
		if (StringUtils.isEmpty(result)) {
            {
                return null;
            }
        }

		Map<String, Object> m = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
		});
		
		logger.info("手机号码：{}， 回执信息：{}", mobile, m);
		Object o = m.get("code");
		if (o == null || StringUtils.isEmpty(o.toString())) {
            {
                return null;
            }
        }

		return new SmsResponse(mobile, m.get("code").toString(), m.get("message").toString());
	}
	
	public static void main(String[] args) throws Exception {
		String url = "http://dev.hspaas.cn:8080/sms/send";
//		url = "http://localhost:8080/sms/send";
		String appsecret = "7yQpKRhg2V7e";
		String mobile = "15868193450";
		String appkey = "1467735756927";
		String content = "【华时科技】您的短信验证码为256965，请尽快完成后续操作。";
		
		System.out.println(sms(url, appkey, appsecret, mobile, content));
	}
}
