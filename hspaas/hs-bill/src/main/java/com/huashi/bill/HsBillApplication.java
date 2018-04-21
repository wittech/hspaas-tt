package com.huashi.bill;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import com.huashi.common.util.LogUtils;

@SpringBootApplication
@ImportResource({ "classpath:spring-dubbo.xml" })
public class HsBillApplication {

	@Bean
	public CountDownLatch closeLatch() {
		return new CountDownLatch(1);
	}

	public static void main(String args[]) throws InterruptedException {
	    ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(HsBillApplication.class, args);
        configurableApplicationContext.registerShutdownHook();

		LogUtils.info("----------------华时融合计费中心项目已启动-----------------");
		
		keepApplicationAlived(configurableApplicationContext);
	}
	
	/**
	 * 
	   * TODO 保持应用阻塞，程序存活
	   * 
	   * @param configurableApplicationContext
	 */
	private static void keepApplicationAlived(ConfigurableApplicationContext configurableApplicationContext) {
	    CountDownLatch closeLatch = configurableApplicationContext.getBean(CountDownLatch.class);
        try {
            closeLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

}