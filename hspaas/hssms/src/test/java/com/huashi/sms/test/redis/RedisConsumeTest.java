package com.huashi.sms.test.redis;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisConsumeTest extends RedisBasicTest{

	public static void main(String[] args) {

		Integer threadNum = 64;

		// public static ExecutorService newFixedThreadPool(int nThreads)
		ExecutorService pool = Executors.newFixedThreadPool(200);
		// 可以执行Runnable对象或者Callable对象代表的线程

		for (int i = 0; i < threadNum; i++) {
			pool.execute(new ConsumeThread());

//			pool.submit(new ProduceThread(jedis));
		}

		// 结束线程池
		pool.shutdown();
	}

	static class ConsumeThread implements Runnable {

		@Override
		public void run() {
			Jedis jedis = getJedis();
			long start = System.currentTimeMillis();
			while (true) {

				String value = jedis.lpop("test_list_queue_20");
				if(StringUtils.isEmpty(value)) {
					logger.info(Thread.currentThread().getName() + "耗时：" + (System.currentTimeMillis() - start));
					jedis.close();
					break;
				}

				logger.info(Thread.currentThread().getName() + "_value:" + value);
			}

		}
	}
}
