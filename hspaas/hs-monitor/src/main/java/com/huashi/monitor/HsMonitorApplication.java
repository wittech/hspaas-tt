package com.huashi.monitor;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
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

        ApplicationContext ctx = new SpringApplicationBuilder().sources(HsMonitorApplication.class).web(false).run(args);

        LogUtils.info("华时任务监管中心启动完成!");

        CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
        closeLatch.await();

    }
}
