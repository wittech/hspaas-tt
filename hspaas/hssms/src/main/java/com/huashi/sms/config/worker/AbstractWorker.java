package com.huashi.sms.config.worker;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSON;
import com.huashi.sms.config.worker.config.SmsDbPersistenceRunner;

/**
 * 
  * TODO 抽象进程基础类
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年11月30日 上午9:59:26
 */
public abstract class AbstractWorker<E extends Object> implements Runnable {

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
		return SmsDbPersistenceRunner.isCustomThreadShutdown;
	}

	protected StringRedisTemplate getStringRedisTemplate() {
		return applicationContext.getBean(StringRedisTemplate.class);
	}
	
	public AbstractWorker(ApplicationContext applicationContext) {
		super();
		this.applicationContext = applicationContext;
	}
	
	/**
	 * 
	   * TODO 执行具体操作
	   * 
	   * @param list
	 */
	protected abstract void operate(List<E> list);
	
	/**
	 * 
	   * TODO 获取REDIS操作值
	   * @return
	 */
	protected abstract String redisKey();
	
	/**
	 * 
	   * TODO 数据失败后持久化REDIS
	   * @param failedRedisKey
	   * @param failedSize
	   * 		本次失败数量
	   * 
	 */
	protected void backupIfFailed(String failedRedisKey, List<E> list) {
		try {
			getStringRedisTemplate().opsForList().rightPushAll(failedRedisKey, JSON.toJSONString(list));
			logger.error("源数据队列：{} 处理失败，加入失败队列完成：{}，共{}条", redisKey(), failedRedisKey, list.size());
		} catch (Exception e) {
			logger.error("源数据队列：{} 处理失败，加入失败队列异常：{}，共{}条", redisKey(), failedRedisKey, list.size(), e);
		}
	}
	
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
	   * TODO 清除资源，重新开始
	   * @param list
	 */
	@SuppressWarnings("rawtypes")
	protected void clear(List list) {
		timer.set(0);
		list.clear();
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
	
	@SuppressWarnings("unchecked")
	private Class<E> getClildType() {
		Class<? extends Object> clazz = this.getClass();
        ParameterizedType type = (ParameterizedType) clazz.getGenericSuperclass();
        // 3返回实际参数类型(泛型可以写多个)
        Type[] types = type.getActualTypeArguments();
        // 4 获取第一个参数(泛型的具体类) Person.class
        return (Class<E>) types[0];
	}
	
	@Override
	public void run() {
		List<E> list = new ArrayList<E>();
		while (true) {
			if(isStop()) {
				logger.info("JVM关闭事件已发起，执行自定义线程池停止...");
				if(CollectionUtils.isNotEmpty(list)) {
					logger.info("JVM关闭事件---当前线程处理数据不为空，执行最后一次后关闭线程...");
					operate(list);
				}
				
				break;
			}
			
			try {
                 Thread.sleep(1L);//先释放资源，避免cpu占用过高
            } catch (Exception e1) {
                 e1.printStackTrace();
            }
			
			try {
				if (timer.get() == 0) {
                    timer.set(System.currentTimeMillis());
                }

				// 如果本次量达到批量取值数据，则跳出
				if (list.size() >= scanSize()) {
					logger.info("-----------获取size:{}", list.size());
					operate(list);
					continue;
				}

				// 如果本次循环时间超过5秒则跳出
				if (CollectionUtils.isNotEmpty(list) && System.currentTimeMillis() - timer.get() >= timeout()) {
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
				
				Object value = JSON.parse(o.toString());
				if(value instanceof List) {
					list.addAll(JSON.parseArray(o.toString(), getClildType()));
				} else {
					list.add(JSON.parseObject(o.toString(), getClildType()));
				}

			} catch (Exception e) {
				logger.error("自定义监听线程过程处理失败，数据为：{}", JSON.toJSONString(list), e);
			}
		}
	}

}
