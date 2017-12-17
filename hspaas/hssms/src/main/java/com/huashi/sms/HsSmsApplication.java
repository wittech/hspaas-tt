package com.huashi.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportResource;

import com.huashi.common.util.LogUtils;

@SpringBootApplication
@ServletComponentScan
// @EnableTransactionManagement
@ImportResource({ "classpath:spring-dubbo-provider.xml" })
public class HsSmsApplication {

	public static void main(String args[]) {
		SpringApplication.run(HsSmsApplication.class, args);
		LogUtils.info("华时短信服务项目已启动");
		
//		try {
//			Thread.sleep(5000);
//			System.exit(0);
//			
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

}
