package com.huashi.listener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.huashi.common.util.LogUtils;

@SpringBootApplication
@EnableScheduling
@ImportResource({ "classpath:spring-dubbo-consumer.xml" })
public class HsListenerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(HsListenerApplication.class, args);
		LogUtils.info("----------------华时HTTP回调服务项目已启动-----------------");
	}

}