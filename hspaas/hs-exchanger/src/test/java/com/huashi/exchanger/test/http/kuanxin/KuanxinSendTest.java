package com.huashi.exchanger.test.http.kuanxin;

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
import com.huashi.exchanger.resolver.http.custom.kuanxin.KuanxinPassageResolver;
import com.huashi.sms.passage.domain.SmsPassageParameter;

public class KuanxinSendTest {

private Logger logger = LoggerFactory.getLogger(getClass());
	
	KuanxinPassageResolver resolver = null;
	SmsPassageParameter parameter = null;
	String mobile = null;
	String content = null;
	String extNumber = null;
	
	@Before
	public void init() {
		resolver = new KuanxinPassageResolver();
		parameter = new SmsPassageParameter();
		
		JSONObject pam = new JSONObject();
		
		pam.put("userid", "387700");
		pam.put("apikey", "96ae42f3934a43c5b615d3aebdc5f777");
		pam.put("custom", "yescloudtree");
		
		
		parameter.setUrl("http://118.178.35.191:8808/api/sms/send");
		parameter.setParams(pam.toJSONString());
		parameter.setSuccessCode("0");
		
//		extNumber = "12";
		
		mobile = "15868193450";
		content = "【借点钱】您的验证码是123123，如非本人发送请忽略";
	}
	
	@Test
	public void test() {
		List<ProviderSendResponse> list = resolver.send(parameter, mobile, content, extNumber);
		
		logger.info(JSON.toJSONString(list));
		
		Assert.assertTrue("回执数据失败", CollectionUtils.isNotEmpty(list));
		
	}
}
