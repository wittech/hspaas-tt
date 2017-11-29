package com.huashi.sms.config.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSON;

public abstract class BaseWorker {

	protected ApplicationContext applicationContext;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	// 批量扫描大小
	protected static final int DEFAULT_SCAN_SIZE = 2000;
	// 超时时间毫秒值
	protected static final int DEFAULT_TIMEOUT = 5000;

	// 当前时间计时
	protected final AtomicLong timer = new AtomicLong(0);

	/**
	 * 
	 * TODO 是否终止执行
	 * 
	 * @return
	 */
	protected boolean isStop() {
		return TaskExecutorConfiguration.isAppShutdown;
	}

	protected StringRedisTemplate getStringRedisTemplate() {
		return applicationContext.getBean(StringRedisTemplate.class);
	}
	
	public BaseWorker(ApplicationContext applicationContext) {
		super();
		this.applicationContext = applicationContext;
	}
	
	/**
	 * 
	   * TODO 执行具体操作
	   * 
	   * @param list
	 */
	@SuppressWarnings("rawtypes")
	protected abstract void operate(List list);
	
	/**
	 * 
	   * TODO 获取REDIS操作值
	   * @return
	 */
	protected abstract String redisKey();
	
	/**
	 * 
	   * TODO 每次扫描的总数量
	   * @return
	 */
	protected int scanSize(){
		return DEFAULT_SCAN_SIZE;
	}
	
	/**
	 * 
	   * TODO 截止超时时间（单位：毫秒）
	   * @return
	 */
	protected long timeout(){
		return DEFAULT_TIMEOUT;
	}
	
	/**
	 * 
	   * TODO 获取对象实例
	   * 
	   * @param clazz
	   * @return
	 */
	protected <T> T getInstance(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}
	
	/**
	 * 
	   * TODO 获取对象实例
	   * 
	   * @param name
	   * @param clazz
	   * @return
	 */
	protected <T> T getInstance(String name, Class<T> clazz) {
		return applicationContext.getBean(name, clazz);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> void execute() {
		List list = new ArrayList();
		while (!isStop()) {
			try {
				if (timer.get() == 0)
					timer.set(System.currentTimeMillis());

				// 如果本次量达到批量取值数据，则跳出
				if (list.size() >= scanSize()) {
					logger.info("-----------获取size:{}", list.size());
					operate(list);
					continue;
				}

				// 如果本次循环时间超过5秒则跳出
				if (System.currentTimeMillis() - timer.get() >= timeout()) {
					operate(list);
					continue;
				}
				
				Object o = getStringRedisTemplate().opsForList().leftPop(redisKey());
				// 执行到redis中没有数据为止
				if (o == null) {
					if (CollectionUtils.isNotEmpty(list)) {
						logger.info("-----------取完，获取size:{}, 耗时：{}ms", list.size(), System.currentTimeMillis() - timer.get());
						operate(list);
					}

					continue;
				}
				
				Object value = JSON.parseObject(o.toString());
				if(value instanceof List) {
					list.addAll((List) value);
				} else {
					list.add(value);
				}

			} catch (Exception e) {
				logger.error("数据入库失败，数据为：{}", JSON.toJSONString(list), e);
			}
		}
	}
	
	public void run() {
		execute();
	}

}
