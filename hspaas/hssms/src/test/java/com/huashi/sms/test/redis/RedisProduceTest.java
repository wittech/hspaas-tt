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
			long start = System.currentTimeMillis();
			Jedis jedis = getJedis();
			String[] ss = new String[1000];
			for(int i =0; i < 1000; i++) {
				logger.info("----------" + i);
				ss[i] = Thread.currentThread().getName() + "_" + i + System.currentTimeMillis();
//				jedis.rpush("test_list_queue_009", Thread.currentThread().getName() + "_" + i + System.currentTimeMillis());
			}

			jedis.rpush("test_list_queue_010", ss);

			jedis.close();
			logger.info(Thread.currentThread().getName() + "耗时：" + (System.currentTimeMillis() - start));

		}
	}

}
