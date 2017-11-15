package com.huashi.monitor.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.huashi.sms.record.service.ISmsMtDeliverService;

public class SubmitReportBornTest {
	
	//用于将bean关系注入到当前的context中  
    @Autowired
    private ApplicationContext applicationContext; 

	
	public void test() {
		String url = "dubbo://106.14.37.153:20881/com.huashi.sms.record.service.ISmsMtDeliverService?anyhost=true&application=hssms-provider&default.retries=0&default.timeout=100000&dubbo=2.8.4&generic=false&interface=com.huashi.sms.record.service.ISmsMtDeliverService&logger=slf4j&methods=doFinishDeliver,batchInsert,doDeliverToException,saveDeliverLog,findByMobileAndMsgid&pid=28352&serialization=kryo&side=provider×tamp=1494991242127";
		  
        ReferenceBean<ISmsMtDeliverService> referenceBean = new ReferenceBean<ISmsMtDeliverService>();  
        referenceBean.setApplicationContext(applicationContext);  
        referenceBean.setInterface(ISmsMtDeliverService.class);  
        referenceBean.setUrl(url);  
  
        try {
            referenceBean.afterPropertiesSet();  
//            smsMtDeliverService = referenceBean.get();
           
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		
	}
}
