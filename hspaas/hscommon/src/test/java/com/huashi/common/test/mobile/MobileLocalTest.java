package com.huashi.common.test.mobile;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.huashi.common.settings.domain.ProvinceLocal;
import com.huashi.common.third.model.MobileCatagory;
import com.huashi.common.third.service.IMobileLocalService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring-dubbo-consumer.xml")
public class MobileLocalTest {
	
	// 全局手机归属号码（与REDIS 同步采用广播模式）
	public static Map<String, ProvinceLocal> GLOBAL_MOBILE_LOCATION = new HashMap<String, ProvinceLocal>();

	String mobile = "";
	
	@Autowired
	IMobileLocalService mobileLocalService;
	
	@Before
	public void init() {
		mobile = "13396247810,13341572609,13036871249,13710962384,13089124675,13235619087,13839541672,13130572684,13379062415,13347582061";
		
	}
	
	
	@Test
	public void test() {
		
		MobileCatagory catagory = mobileLocalService.doCatagory(mobile);
		
		System.out.println(JSON.toJSONString(catagory));
		
		Assert.assertTrue("归属地划分失败", catagory.isSuccess());
		
	}
}
