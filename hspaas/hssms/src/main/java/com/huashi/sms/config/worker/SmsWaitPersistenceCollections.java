package com.huashi.sms.config.worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.huashi.sms.task.domain.SmsMtTask;

/**
 * 
  * TODO 待持久化
  * @author zhengying
  * @version V1.0   
  * @date 2017年2月22日 下午2:51:47
 */
public class SmsWaitPersistenceCollections {

	// 待入库任务数据
	public static BlockingQueue<SmsMtTask> GLOBAL_SMS_MT_TASK_QUEUE = new LinkedBlockingQueue<SmsMtTask>();
	
	
	
}
