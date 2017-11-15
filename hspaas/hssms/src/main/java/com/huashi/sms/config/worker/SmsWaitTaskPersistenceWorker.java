package com.huashi.sms.config.worker;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSON;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.task.domain.SmsMtTask;
import com.huashi.sms.task.domain.SmsMtTaskPackets;
import com.huashi.sms.task.service.ISmsMtTaskService;

/**
 * 
 * TODO 短信主任务待持久线程
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月10日 下午6:03:39
 */
public class SmsWaitTaskPersistenceWorker implements Runnable {

	private StringRedisTemplate stringRedisTemplate;
	private ISmsMtTaskService smsMtTaskService;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public SmsWaitTaskPersistenceWorker(StringRedisTemplate stringRedisTemplate, ISmsMtTaskService smsMtTaskService) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.smsMtTaskService = smsMtTaskService;
	}

	// 批量扫描大小
	private static final int SCAN_BATCH_SIZE = 1000;

	@Override
	public void run() {
		while (true) {
			try {
				stringRedisTemplate.watch(SmsRedisConstant.RED_DB_MESSAGE_TASK_LIST);
				// SCAN_BATCH_SIZE - 1， 是因为REDIS LRANGE 默认从0索引算起，0-999 则为1000个
				List<String> list = stringRedisTemplate.opsForList().range(SmsRedisConstant.RED_DB_MESSAGE_TASK_LIST, 0,
						SCAN_BATCH_SIZE - 1);
				
				if(CollectionUtils.isEmpty(list))
					continue;

				List<SmsMtTask> tasks = new ArrayList<>();
				List<SmsMtTaskPackets> taskPackets = new ArrayList<>();
				for (String t : list) {
					SmsMtTask task = JSON.parseObject(t, SmsMtTask.class);
					tasks.add(task);
					if(CollectionUtils.isNotEmpty(task.getPackets()))
						taskPackets.addAll(task.getPackets());
				}

				doTaskPersistence(list, tasks, taskPackets);
				
				// 保留自本次的SCAN_BATCH_SIZE至最后数据，清除本次处理的1000条
				stringRedisTemplate.opsForList().trim(SmsRedisConstant.RED_DB_MESSAGE_TASK_LIST, list.size(), -1);
				stringRedisTemplate.unwatch();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * TODO 持久化主任务
	 * 
	 * @param list
	 * @param tasks
	 * @param taskPackets
	 */
	private void doTaskPersistence(List<String> list, List<SmsMtTask> tasks, List<SmsMtTaskPackets> taskPackets) {
		try {
			smsMtTaskService.batchSave(tasks, taskPackets);
			logger.info("主任务持久同步完成，共处理 主任务 {} 条，子任务：{} 条", tasks.size(), taskPackets.size());
		} catch (Exception e) {
			logger.error("主任务异步持久化失败", e);
			// 处理失败放置处理失败队列中
			stringRedisTemplate.opsForList().rightPushAll(SmsRedisConstant.RED_DB_MESSAGE_TASK_FAILED_LIST, list);
		}
	}

}
