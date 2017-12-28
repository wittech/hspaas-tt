package com.huashi.exchanger.test.http.xinchi;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.http.custom.xinchi.XinchiPassageResolver;
import com.huashi.sms.passage.domain.SmsPassageParameter;

public class XinchiHttpTest {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	XinchiPassageResolver resolver = null;
	SmsPassageParameter parameter = null;
	String mobile = null;
	String content = null;
	String extNumber = null;
	
	@Before
	public void init() {
		resolver = new XinchiPassageResolver();
		parameter = new SmsPassageParameter();
		
		JSONObject pam = new JSONObject();
		pam.put("key", "1396d65453a2d");
		pam.put("password", "100817122700010109");
		pam.put("custom", "xunchi");
		
		parameter.setUrl("http://www.xcapi.net:18018/xcapi/smsApiSends.do");
		parameter.setParams(pam.toJSONString());
		
		mobile = "15868193450";
		content = "【怀瑾科技】您的验证码是123123如非本人发送请忽略该信息。";
	}
	
	@Test
	public void test() {
		List<ProviderSendResponse> list = resolver.send(parameter, mobile, content, extNumber);
		
		logger.info(JSON.toJSONString(list));
		
		Assert.assertTrue("回执数据失败", CollectionUtils.isNotEmpty(list));
		
	}
}
