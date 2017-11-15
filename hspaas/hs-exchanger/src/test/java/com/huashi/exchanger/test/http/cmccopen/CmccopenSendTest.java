package com.huashi.exchanger.test.http.cmccopen;

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
import com.huashi.exchanger.resolver.http.custom.cmccopen.CmccopenPassageResolver;
import com.huashi.sms.passage.domain.SmsPassageParameter;

public class CmccopenSendTest {

private Logger logger = LoggerFactory.getLogger(getClass());
	
	CmccopenPassageResolver resolver = null;
	SmsPassageParameter parameter = null;
	String mobile = null;
	String content = null;
	String extNumber = null;
	
	@Before
	public void init() {
		resolver = new CmccopenPassageResolver();
		parameter = new SmsPassageParameter();
		
		JSONObject pam = new JSONObject();
		
		pam.put("appkey", "e7f1614aa751463bb5e312ccd0394263");
		pam.put("password", "fbf6763ecc9c6084");
		pam.put("terminal_no", "106575261108090");
		
//		【可遇科技】您的验证码是{code}，{number}分钟有效。
		
		pam.put("custom", "cmccopen");
		
		parameter.setSmsTemplateId("57b1d41b-85c3-48b9-ba74-ce07183a26e4");
		parameter.setVariableParamNames(new String[]{"code", "number"});
		parameter.setVariableParamValues(new String[]{"666666", "5"});
		
		parameter.setUrl("http://aep.api.cmccopen.cn/sms/sendTemplateSms/v1");
		parameter.setParams(pam.toJSONString());
		parameter.setSuccessCode("000000");
		
		extNumber = "12";
		
		mobile = "15868193450";
//		content = "【云树科技】尊敬的客户，本次验证码为：125889，5分钟有效，请及时操作。";
	}
	
	@Test
	public void test() {
		List<ProviderSendResponse> list = resolver.send(parameter, mobile, content, extNumber);
		
		logger.info(JSON.toJSONString(list));
		
		Assert.assertTrue("回执数据失败", CollectionUtils.isNotEmpty(list));
		
	}
}
