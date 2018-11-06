package com.huashi.sms.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huashi.sms.report.service.ISmsSubmitHourReportService;
import com.huashi.sms.test.BaseTest;

public class SubmitHoureReportBornTest extends BaseTest{

	@Autowired
	ISmsSubmitHourReportService smsSubmitHourReportService;
	
	@Test
	public void testQ() {
		int result = smsSubmitHourReportService.beBornSubmitHourReport(72);
		
		logger.info("处理结果：{}", result);
	}
}
