package com.huashi.common;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import com.huashi.common.util.LogUtils;

@SpringBootApplication
@ImportResource({ "classpath:spring.xml" })
public class HsCommonApplication {

	@Bean
	public CountDownLatch closeLatch() {
		return new CountDownLatch(1);
	}

	public static void main(String args[]) throws InterruptedException {
		ApplicationContext ctx = new SpringApplicationBuilder().sources(HsCommonApplication.class).web(false).run(args);

		LogUtils.info("华时融合公共服务项目启动!");

		CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
		closeLatch.await();
	}
}
