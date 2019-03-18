package com.huashi.developer;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import com.huashi.common.util.LogUtils;

@SpringBootApplication
@EnableRabbit
@ImportResource({ "classpath:spring-dubbo-consumer.xml" })
public class HsDeveloperApplication {

	public static void main(String[] args) {
		SpringApplication.run(HsDeveloperApplication.class, args);
		LogUtils.info("----------------华时开发者服务项目已启动-----------------");
	}
}
