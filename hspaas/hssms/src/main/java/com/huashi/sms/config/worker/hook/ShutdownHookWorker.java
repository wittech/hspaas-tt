package com.huashi.sms.config.worker.hook;

import com.alibaba.fastjson.TypeReference;
import com.huashi.sms.config.worker.config.SmsDbPersistenceRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

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
	
//	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	ApplicationContext applicationContext;

	public ShutdownHookWorker(ApplicationContext applicationContext,
			ThreadPoolTaskExecutor threadPoolTaskExecutor) {
		
		this.applicationContext = applicationContext;
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
	}

	@Override
	public void run() {
		synchronized (startupShutdownMonitor) {
			SmsDbPersistenceRunner.shutdownSignal = true;
			
//			Map<String, SimpleMessageListenerContainer> containers = applicationContext.getBeansOfType(SimpleMessageListenerContainer.class);
//			if(MapUtils.isNotEmpty(containers)) {
//				logger.info("共获取RABBIT监听{} 个， 名称为：{}", containers.size(), JSON.toJSONString(containers.keySet()));
//				
//				for(SimpleMessageListenerContainer container : containers.values()) {
//					container.shutdown();
//				}
//			}
			
			threadPoolTaskExecutor.shutdown();
		}
		
	}

	public static void main(String[] args) {
		
		
		System.out.println(new TypeReference<List<String>>(){}.getType());
	}
}
