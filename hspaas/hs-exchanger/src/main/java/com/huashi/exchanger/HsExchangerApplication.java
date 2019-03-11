package com.huashi.exchanger;

import java.util.concurrent.CountDownLatch;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;

import com.huashi.common.util.IdGenerator;
import com.huashi.common.util.LogUtils;

@SpringBootApplication
@EnableAsync
@EnableRabbit
@ImportResource({ "classpath:spring-dubbo-provider.xml" })
public class HsExchangerApplication {

    @Bean
    public CountDownLatch closeLatch() {
        return new CountDownLatch(1);
    }

    @Bean
    public IdGenerator idGenerator() {
        return new IdGenerator(1);
    }

    public static void main(String args[]) throws InterruptedException {
        ApplicationContext ctx = SpringApplication.run(HsExchangerApplication.class, args);

        LogUtils.info("----------------华时融合通道交换器项目已启动-----------------");

        CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
        closeLatch.await();

    }
}
