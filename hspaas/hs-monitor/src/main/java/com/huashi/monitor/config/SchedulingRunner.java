package com.huashi.monitor.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import com.huashi.monitor.passage.service.IPassageMonitorService;

@Component
@EnableScheduling
public class SchedulingRunner implements CommandLineRunner, SchedulingConfigurer {
	
	@Autowired
	private IPassageMonitorService passageMonitorService;

	public void run(String... args) throws Exception {
		
		// 开启通道轮训
		passageMonitorService.startPassagePull();
	}

	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}

	@Bean(destroyMethod = "shutdown")
	public Executor taskExecutor() {
		return Executors.newScheduledThreadPool(32);
	}

}
