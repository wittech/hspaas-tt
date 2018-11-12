package com.huashi.sms.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.huashi.sms.report.service.ISmsSubmitHourReportService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubmitHoureReportBornTest{
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	ISmsSubmitHourReportService smsSubmitHourReportService;
	
	@Test
	public void testQ() {
		int result = smsSubmitHourReportService.beBornSubmitHourReport(72);
		
		logger.info("处理结果：{}", result);
	}
}
