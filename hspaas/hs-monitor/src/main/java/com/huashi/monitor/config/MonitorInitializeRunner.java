package com.huashi.monitor.config;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.monitor.passage.service.IPassageMonitorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MonitorInitializeRunner implements CommandLineRunner {
	
	@Reference
	private IPassageMonitorService passageMonitorService;

	@Override
    public void run(String... args) throws Exception {
		
		// 开启通道轮训
		passageMonitorService.startPassagePull();
	}



}
