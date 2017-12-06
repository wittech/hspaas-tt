package com.huashi.sms.config.worker.config;

import javax.annotation.Resource;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.huashi.sms.config.worker.db.SmsDeliverPersistenceWorker;
import com.huashi.sms.config.worker.db.SmsMoPersistenceWorker;
import com.huashi.sms.config.worker.db.SmsMoPushPersistenceWorker;
import com.huashi.sms.config.worker.db.SmsMtPushPersistenceWorker;
import com.huashi.sms.config.worker.db.SmsSubmitPersistenceWorker;
import com.huashi.sms.config.worker.db.SmsTaskPersistenceWorker;
import com.huashi.sms.config.worker.fork.MtReportFailoverPushWorker;
import com.huashi.sms.config.worker.hook.ShutdownHookWorker;

/**
 * 
 * TODO 短信异步数据库操作初始化
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月15日 上午11:02:30
 */
@Configuration
@Order(3)
public class SmsDbPersistenceRunner implements CommandLineRunner {

	@Value("${thread.poolsize:5}")
	private int threadPoolSize;
	
	// 自定义线程关闭标记（用于钩子回调）
	public static volatile boolean isCustomThreadShutdown = false;

	@Autowired
	private ApplicationContext applicationContext;
	@Resource
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void run(String... arg0) throws Exception {
		for (int i = 0; i < threadPoolSize; i++) {
			
			// 任务持久化线程
			threadPoolTaskExecutor.execute(new SmsTaskPersistenceWorker(applicationContext));
			// 处理完成提交数据（下行）
			threadPoolTaskExecutor.execute(new SmsSubmitPersistenceWorker(applicationContext));
			// 上家状态回执数据（下行）
			threadPoolTaskExecutor.execute(new SmsDeliverPersistenceWorker(applicationContext));
			// 推送开发者调用数据（下行）
			threadPoolTaskExecutor.execute(new SmsMtPushPersistenceWorker(applicationContext));
			// 上家上行短信内容数据（上行）
			threadPoolTaskExecutor.execute(new SmsMoPersistenceWorker(applicationContext));
			// 推送开发者上行数据（上行）
			threadPoolTaskExecutor.execute(new SmsMoPushPersistenceWorker(applicationContext));
			
			// 用户下行状态延迟推送（针对上家下行状态报告回复过快而短信提交记录未入库情况，后续延迟推送）
			threadPoolTaskExecutor.execute(new MtReportFailoverPushWorker(applicationContext));
		}

//		threadPoolTaskExecutor.shutdown();
		
		logger.info("===数据库异步持久线程监听已启动");
		
		registShutdownHook();
		logger.info("===JVM钩子函数已启动");		
	}

	/**
	 * 
	   * TODO 注册JVM关闭钩子函数
	 */
	private void registShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHookWorker(applicationContext,
						threadPoolTaskExecutor)));
	}

}
