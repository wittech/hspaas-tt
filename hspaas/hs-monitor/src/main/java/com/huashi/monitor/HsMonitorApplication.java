package com.huashi.monitor;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.huashi.common.util.LogUtils;

@SpringBootApplication
@EnableScheduling
@ImportResource({ "classpath:spring-dubbo-provider.xml" })
public class HsMonitorApplication {

    @Bean
    public CountDownLatch closeLatch() {
        return new CountDownLatch(1);
    }

    public static void main(String args[]) throws InterruptedException {

        ConfigurableApplicationContext ctx = SpringApplication.run(HsMonitorApplication.class, args);

        LogUtils.info("华时任务监管中心启动完成!");

        CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
        closeLatch.await();

    }
}
