package com.huashi.sms.test.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.huashi.sms.report.service.ISmsSubmitHourReportService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-dubbo-consumer.xml" })
public class SubmitHoureReportBornTest{
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

	private ISmsSubmitHourReportService smsSubmitHourReportService;
	private int hour;
	
	@Before
    public void init() {
	    hour = 240;
    }
    
    private void wireService() {
        String url = "dubbo://106.14.37.153:20881/com.huashi.sms.report.service.ISmsSubmitHourReportService";

        ReferenceBean<ISmsSubmitHourReportService> referenceBean = new ReferenceBean<ISmsSubmitHourReportService>();
        referenceBean.setApplicationContext(applicationContext);
        referenceBean.setInterface(ISmsSubmitHourReportService.class);
        referenceBean.setUrl(url);
        referenceBean.setTimeout(1000000);

        try {
            referenceBean.afterPropertiesSet();
             smsSubmitHourReportService = referenceBean.get();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 用于将bean关系注入到当前的context中
    @Autowired
    private ApplicationContext applicationContext;
	
	@Test
	public void test() {
	    wireService();
		int result = smsSubmitHourReportService.beBornSubmitHourReport(hour);
		
		logger.info("处理结果：{}", result);
	}
}
