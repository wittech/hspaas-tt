package com.huashi.sms.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.huashi.sms.HsSmsApplication;
import com.huashi.sms.template.service.ISmsTemplateService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HsSmsApplication.class, webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class SmsTemplateTest{

	@Autowired
	ISmsTemplateService smsTemplateService;
	
	@Test
	public void test() {
		
		for(int i=0; i<1000; i++) {
			smsTemplateService.getByContent(82, "ssss");
		}
	}
}
