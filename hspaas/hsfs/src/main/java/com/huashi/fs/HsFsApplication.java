/**
 * 
 */
package com.huashi.fs;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import com.huashi.common.util.LogUtils;

/**
 * 
  * TODO 华时流量服务启动入口
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2016年7月20日 下午3:01:25
 */
@SpringBootApplication
@ImportResource({ "classpath:spring.xml" })
public class HsFsApplication {
	@Bean
	public CountDownLatch closeLatch() {
		return new CountDownLatch(1);
	}

	public static void main(String args[]) throws InterruptedException {

		ApplicationContext ctx = new SpringApplicationBuilder()
				.sources(HsFsApplication.class).web(false).run(args);

		LogUtils.info("----------------华时流量服务项目已启动----------------");

		CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
		closeLatch.await();
	}
}
