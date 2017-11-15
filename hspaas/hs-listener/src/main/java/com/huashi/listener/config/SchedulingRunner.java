package com.huashi.listener.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class SchedulingRunner implements CommandLineRunner, SchedulingConfigurer {
	
	public void run(String... args) throws Exception {
//		smsPassagePrervice.doPassageStatusPulling();
//		logger.info("通道状态回执监听已开启...");
//		
//		smsPassagePrervice.doPassageMoPulling();
//		logger.info("通道上行回执监听已开启...");
	}

	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}

	@Bean(destroyMethod = "shutdown")
	public Executor taskExecutor() {
		return Executors.newScheduledThreadPool(32);
	}

}
