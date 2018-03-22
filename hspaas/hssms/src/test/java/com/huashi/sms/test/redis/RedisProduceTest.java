package com.huashi.sms.test.redis;

import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisProduceTest extends RedisBasicTest{

	public static void main(String[] args) {

		Integer threadNum = 5;

		// public static ExecutorService newFixedThreadPool(int nThreads)
		ExecutorService pool = Executors.newFixedThreadPool(20);

		for (int i = 0; i < threadNum; i++) {
			pool.execute(new ProduceThread());
		}

		// 结束线程池
		pool.shutdown();
	}

	static class ProduceThread implements Runnable {

		@Override
		public void run() {
			
			Jedis jedis = getJedis();
			int length = 100000;
			String[] ss = new String[length];
			long start = System.currentTimeMillis();
			for(int i =0; i < length; i++) {
//				logger.info("----------" + i);
				ss[i] = Thread.currentThread().getName() + "_" + i + System.nanoTime();
//				jedis.rpush("test_list_queue_10", Thread.currentThread().getName() + "_" + i + System.currentTimeMillis());
			}

			jedis.rpush("test_list_queue_20", ss);

			jedis.close();
			logger.info(Thread.currentThread().getName() + "耗时：" + (System.currentTimeMillis() - start));

		}
	}

}
