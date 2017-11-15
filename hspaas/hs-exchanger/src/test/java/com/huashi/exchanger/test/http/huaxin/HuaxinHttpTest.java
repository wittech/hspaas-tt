package com.huashi.exchanger.test.http.huaxin;

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
import com.huashi.exchanger.resolver.http.custom.huaxin.HuaxinPassageResolver;
import com.huashi.sms.passage.domain.SmsPassageParameter;

public class HuaxinHttpTest {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	HuaxinPassageResolver resolver = null;
	SmsPassageParameter parameter = null;
	String mobile = null;
	String content = null;
	String extNumber = null;
	
	@Before
	public void init() {
		resolver = new HuaxinPassageResolver();
		parameter = new SmsPassageParameter();
		
		JSONObject pam = new JSONObject();
		
		
		pam.put("userid", "1");
		pam.put("account", "hs657");
		pam.put("password", "123456111");
		pam.put("action", "send");
		pam.put("custom", "huaxin");
		
		parameter.setUrl("http://114.55.53.120/sms.aspx");
		parameter.setParams(pam.toJSONString());
		parameter.setSuccessCode("Success");
		
		mobile = "15868193450";
		content = "【华时科技】您的短息验证码为1234452";
	}
	
	@Test
	public void test() {
		List<ProviderSendResponse> list = resolver.send(parameter, mobile, content, extNumber);
		
		logger.info(JSON.toJSONString(list));
		
		Assert.assertTrue("回执数据失败", CollectionUtils.isNotEmpty(list));
		
	}
}
