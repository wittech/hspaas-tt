package com.huashi.bill;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import com.huashi.common.util.LogUtils;

@SpringBootApplication
@ImportResource({ "classpath:spring.xml" })
public class HsBillApplication {

	@Bean
	public CountDownLatch closeLatch() {
		return new CountDownLatch(1);
	}

	public static void main(String args[]) throws InterruptedException {
		ApplicationContext ctx = new SpringApplicationBuilder().sources(HsBillApplication.class).web(false).run(args);

		LogUtils.info("----------------华时融合计费中心项目已启动-----------------");

		CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
		closeLatch.await();
	}

}