package com.huashi.monitor.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * TODO 线程池配置
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年9月20日 下午5:58:30
 */
@Configuration
@Order(1)
public class ExecutorConfiguration {

    /**
     * 配置短信线程池
     * 
     * @return
     */
    @Bean(name = "smsThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor smsThreadPoolTaskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("smsTaskExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 配置彩信线程池
     * 
     * @return
     */
    @Bean(name = "mmsThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor mmsThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("mmsTaskExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

}
