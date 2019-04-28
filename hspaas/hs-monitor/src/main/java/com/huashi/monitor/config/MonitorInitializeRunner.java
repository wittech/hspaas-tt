package com.huashi.monitor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.huashi.monitor.passage.service.IPassageMonitorService;
import com.huashi.monitor.passage.thread.hook.ShutdownHookWorker;

@Component
public class MonitorInitializeRunner implements CommandLineRunner {
	
	@Autowired
	private IPassageMonitorService passageMonitorService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
    public void run(String... args) throws Exception {
		
		// 开启通道轮训
		passageMonitorService.startPassagePull();
		
		logger.info("===通道轮训抓取报告 + 抓取上行... 已开启");
		
		registShutdownHook();
		
		logger.info("===JVM钩子函数已启动");
	}


	/**
     * 
       * TODO 注册JVM关闭钩子函数
     */
    private void registShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHookWorker()));
    }

}
