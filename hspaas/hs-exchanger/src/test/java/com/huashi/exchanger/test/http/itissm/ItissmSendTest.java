package com.huashi.exchanger.test.http.itissm;

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
import com.huashi.exchanger.resolver.http.custom.itissm.ItissmPassageResolver;
import com.huashi.sms.passage.domain.SmsPassageParameter;

public class ItissmSendTest {

private Logger logger = LoggerFactory.getLogger(getClass());
	
	ItissmPassageResolver resolver = null;
	SmsPassageParameter parameter = null;
	String mobile = null;
	String content = null;
	String extNumber = null;
	
	@Before
	public void init() {
		resolver = new ItissmPassageResolver();
		parameter = new SmsPassageParameter();
		
		JSONObject pam = new JSONObject();
		
		pam.put("account", "HSKJYX");
		pam.put("password", "gHZAc2578");
		pam.put("channel_no", "605");
		
//		【可遇科技】您的验证码是{code}，{number}分钟有效。
		
		pam.put("custom", "itissm");
		
		parameter.setUrl("http://yes.itissm.com/api/MsgSend.asmx/SendMsg");
		parameter.setParams(pam.toJSONString());
		parameter.setSuccessCode("0");
		
		extNumber = "12";
		
		mobile = "15868193450";
		content = "尊敬的客户，恭喜获得中信白金卡申请资格！额度高，随时取现，最长50天免息还款。官网申请 0x7.me/Et4yN 退订回T【中信银行信用卡】";
	}
	
	@Test
	public void test() {
		List<ProviderSendResponse> list = resolver.send(parameter, mobile, content, extNumber);
		
		logger.info(JSON.toJSONString(list));
		
		Assert.assertTrue("回执数据失败", CollectionUtils.isNotEmpty(list));
		
	}
}
