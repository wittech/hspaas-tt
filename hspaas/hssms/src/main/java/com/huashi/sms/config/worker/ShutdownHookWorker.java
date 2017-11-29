package com.huashi.sms.config.worker;

import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 
  * TODO 钩子回调线程
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年11月29日 下午6:41:16
 */
public class ShutdownHookWorker implements Runnable {
	
	private final Object startupShutdownMonitor = new Object();

	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	private ApplicationContext applicationContext;

	public ShutdownHookWorker(ApplicationContext applicationContext,
			ThreadPoolTaskExecutor threadPoolTaskExecutor) {
		
		this.applicationContext = applicationContext;
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
	}

	@Override
	public void run() {
		synchronized (startupShutdownMonitor) {
			TaskExecutorConfiguration.isAppShutdown = true;
			
//			RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
			threadPoolTaskExecutor.shutdown();
		}
		
	}

}
