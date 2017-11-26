package com.huashi.sms.config.runner;

import javax.annotation.Resource;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.huashi.sms.config.worker.SmsWaitMoPersistenceWorker;
import com.huashi.sms.config.worker.SmsWaitMoPushPersistenceWorker;
import com.huashi.sms.config.worker.SmsWaitMtPushPersistenceWorker;
import com.huashi.sms.config.worker.SmsWaitReceiptPersistenceWorker;
import com.huashi.sms.config.worker.SmsWaitSubmitPersistenceWorker;
import com.huashi.sms.config.worker.SmsWaitTaskPersistenceWorker;
import com.huashi.sms.config.worker.WaitMtAppendPushWorker;
import com.huashi.sms.record.service.ISmsMoMessageService;
import com.huashi.sms.record.service.ISmsMoPushService;
import com.huashi.sms.record.service.ISmsMtDeliverService;
import com.huashi.sms.record.service.ISmsMtPushService;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.sms.task.service.ISmsMtTaskService;

/**
 * 
  * TODO 短信异步数据库操作初始化
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2016年12月15日 上午11:02:30
 */
@Configuration
@Order(50)
public class SmsDbPersistenceRunner implements CommandLineRunner {

	@Value("${db.persistence.threadnum:5}")
	private int persistenceThreadNum;

	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private ISmsMtTaskService smsMtTaskService;
	@Autowired
	private ISmsMtSubmitService smsMtSubmitService;
	@Autowired
	private ISmsMtDeliverService smsMtDeliverService;
	@Autowired
	private ISmsMtPushService smsMtPushService;
	@Autowired
	private ISmsMoMessageService smsMoMessageService;
	@Autowired
	private ISmsMoPushService smsMoPushService;
	
	@Autowired
	private ThreadPoolTaskExecutor poolTaskExecutor;

	private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void run(String... arg0) throws Exception {
		// 线程池所使用的缓冲队列
		// poolTaskExecutor.setQueueCapacity(200); // 队列可以大一点，以应对峰值压力
		// // 线程池维护线程的最少数量
		// poolTaskExecutor.setCorePoolSize(5);
		// // 线程池维护线程的最大数量
		// poolTaskExecutor.setMaxPoolSize(50); // 该值不可太大，否则对系统运行性能有影响
		// // 线程池维护线程所允许的空闲时间
		// poolTaskExecutor.setKeepAliveSeconds(10000);// 10分钟
		// poolTaskExecutor.initialize();

		// 开启主任务单线程
		Thread taskThread = new Thread(new SmsWaitTaskPersistenceWorker(
				stringRedisTemplate, smsMtTaskService));
		taskThread.start();

		// 开启提交短信单线程
//		Thread submitThread = new Thread(new SmsWaitSubmitPersistenceWorker(
//				stringRedisTemplate, smsMtSubmitService));
//		submitThread.start();
		
		for (int i=0; i < persistenceThreadNum; i++) {
			// 开启提交短信单线程
			poolTaskExecutor.execute(new SmsWaitSubmitPersistenceWorker(stringRedisTemplate, smsMtSubmitService));
			poolTaskExecutor.execute(new WaitMtAppendPushWorker(stringRedisTemplate, smsMtPushService));
		}

		// 开启回执短信单线程
		Thread receiptThread = new Thread(new SmsWaitReceiptPersistenceWorker(
				stringRedisTemplate, smsMtDeliverService));
		receiptThread.start();

		// 开启推送短信单线程
		Thread pushThread = new Thread(new SmsWaitMtPushPersistenceWorker(
				stringRedisTemplate, smsMtPushService));
		pushThread.start();
		
		// 开启上行短信单线程
		Thread moThread = new Thread(new SmsWaitMoPersistenceWorker(
				stringRedisTemplate, smsMoMessageService));
		moThread.start();
		
		// 开启推送短信上行推送单线程
		Thread moPushThread = new Thread(new SmsWaitMoPushPersistenceWorker(
				stringRedisTemplate, smsMoPushService));
		moPushThread.start();

		logger.info("===待数据库持久线程监听已启动");

		// // 合同处理并发线程数量
		// for (int i = 0; i < persistenceThreadNum; i++) {
		//
		// poolTaskExecutor.execute(new
		// SmsWaitTaskPersitenceThread(stringRedisTemplate));//
		// 产生5个线程处理短信主任务入库过程
		// }
	}

}
