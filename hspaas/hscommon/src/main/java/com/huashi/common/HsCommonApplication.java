package com.huashi.common;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;

import com.huashi.common.util.LogUtils;

@EnableAsync
@SpringBootApplication
@ImportResource({ "classpath:spring-dubbo-provider.xml" })
public class HsCommonApplication {

    @Bean
    public CountDownLatch closeLatch() {
        return new CountDownLatch(1);
    }

    public static void main(String args[]) throws InterruptedException {
        ApplicationContext ctx = SpringApplication.run(HsCommonApplication.class, args);

        LogUtils.info("华时融合公共服务项目启动!");

        CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
        closeLatch.await();
    }
}
