package com.huashi.monitor;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.huashi.common.util.LogUtils;

@SpringBootApplication
@EnableScheduling
@ImportResource({ "classpath:spring-dubbo-provider.xml" })
public class HsMonitorApplication {

    /**
     * 应用控制器（保证程序继续运行）
     */
    private static final CountDownLatch APPLICATION_CDL = new CountDownLatch(1);

    public static void main(String args[]) throws InterruptedException {

        SpringApplication.run(HsMonitorApplication.class, args);

        // ApplicationContext ctx = new
        // SpringApplicationBuilder().sources(HsMonitorApplication.class).web(false).run(args);

        LogUtils.info("华时任务监管中心启动完成!");

        // CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
        // closeLatch.await();

        // 主进程守候，防止进程启动后自动断开
        APPLICATION_CDL.await();

    }
}
