package com.huashi.sms.config.worker.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSON;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.worker.AbstractWorker;
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
public class SmsTaskPersistenceWorker extends AbstractWorker<SmsMtTask>{

	public SmsTaskPersistenceWorker(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	@Override
	public void run() {
		List<SmsMtTask> tasks = new ArrayList<>();
		List<SmsMtTaskPackets> taskPackets = new ArrayList<>();
		while (true) {
			if(isStop()) {
				logger.info("JVM关闭事件已发起，执行自定义线程池停止...");
				if(CollectionUtils.isNotEmpty(tasks)) {
					logger.info("JVM关闭事件---当前线程处理数据不为空，执行最后一次后关闭线程...");
					operate(tasks, taskPackets);
				}
				break;
			}
			
			try {
				//先释放资源，避免cpu占用过高
                Thread.sleep(1L);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
			
			try {
				if (timer.get() == 0) {
                    timer.set(System.currentTimeMillis());
                }

				// 如果本次量达到批量取值数据，则跳出
				if (tasks.size() >= scanSize()) {
					logger.info("-----------获取size:{}", tasks.size());
					operate(tasks, taskPackets);
					continue;
				}

				// 如果本次循环时间超过5秒则跳出
				if (CollectionUtils.isNotEmpty(tasks) && System.currentTimeMillis() - timer.get() >= timeout()) {
					operate(tasks, taskPackets);
					continue;
				}
				
				Object o = getStringRedisTemplate().opsForList().leftPop(redisKey());
				// 执行到redis中没有数据为止
				if (o == null) {
					if (CollectionUtils.isNotEmpty(tasks)) {
						logger.info("-----------取完，获取size:{}, 耗时：{}ms", tasks.size(), System.currentTimeMillis() - timer.get());
						operate(tasks, taskPackets);
					}

					continue;
				}
				
				SmsMtTask task = JSON.parseObject(o.toString(), SmsMtTask.class);
				tasks.add(task);
				if(CollectionUtils.isNotEmpty(task.getPackets())) {
                    taskPackets.addAll(task.getPackets());
                }

			} catch (Exception e) {
				logger.error("任务数据入库失败，数据为：{}", getStringRedisTemplate().opsForList().leftPop(redisKey()), e);
			}
		}

	}

	/**
	 * 
	 * TODO 持久化主任务
	 * 
	 * @param tasks
	 * @param tasks
	 * @param taskPackets
	 */
	private void operate(List<SmsMtTask> tasks, List<SmsMtTaskPackets> taskPackets) {
		if (CollectionUtils.isEmpty(tasks)) {
            return;
        }
		
		try {
			getInstance(ISmsMtTaskService.class).batchSave(tasks, taskPackets);
			logger.info("主任务持久同步完成，共处理 主任务 {} 条，子任务：{} 条", tasks.size(), taskPackets.size());
		} catch (Exception e) {
			logger.error("主任务异步持久化失败", e);
			getStringRedisTemplate().opsForList().rightPushAll(redisBackupKey(), JSON.toJSONString(tasks));
		} finally {
			super.clear(tasks);
			taskPackets.clear();
		}
	}

	@Override
	protected void operate(List<SmsMtTask> list) {}

	@Override
	protected String redisKey() {
		return SmsRedisConstant.RED_DB_MESSAGE_TASK_LIST;
	}

	@Override
	protected String jobTitle() {
		return "短信分包任务持久化";
	}

}
