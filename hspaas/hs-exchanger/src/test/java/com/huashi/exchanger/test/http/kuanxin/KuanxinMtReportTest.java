package com.huashi.exchanger.test.http.kuanxin;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.resolver.http.custom.kuanxin.KuanxinPassageResolver;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

public class KuanxinMtReportTest {

private Logger logger = LoggerFactory.getLogger(getClass());
	
	KuanxinPassageResolver resolver = null;
	String report = null;
	String successCode = null;
	
	@Before
	public void init() {
		resolver = new KuanxinPassageResolver();
		
		List<JSONObject> jsonArray = new ArrayList<>();
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("time", "20171105235736");
		jsonObject.put("taskid", "1105235732100000101973");
		jsonObject.put("code", "DELIVRD");
		jsonObject.put("msg", "提交成功");
		jsonObject.put("mobile", "18368031231");
		jsonArray.add(jsonObject);
		
		
		report = JSON.toJSONString(jsonArray);
		successCode = "DELIVRD";
		
//		http://www.yescloudtree.cn:28001/?Action=MoSms&UserName=hzhs&Password=43f7c7f5fb0fc7fd1d503b95969e4be3
	}
	
	@Test
	public void test() {
		System.out.println("-----------" + report);
		List<SmsMtMessageDeliver> list = resolver.mtDeliver(report, successCode);
		
		logger.info(JSON.toJSONString(list));
		
//		Assert.assertTrue("回执数据失败", CollectionUtils.isNotEmpty(list));
		
	}
}
