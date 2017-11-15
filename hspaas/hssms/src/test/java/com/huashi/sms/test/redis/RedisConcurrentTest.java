package com.huashi.sms.test.redis;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class RedisConcurrentTest {

	private Jedis jedis;
	Integer messageCount = 0;
	Integer threadNum = 0;

	@Before
	public void setup() {
		// 连接redis服务器，192.168.0.100:6379
		jedis = new Jedis("106.14.37.153", 6379);
		// 权限认证
		jedis.auth("huashi_redis_99088@");

		messageCount = 100000;
		threadNum = 5;
	}

	@Test
	public void produce() throws IOException {

		// 创建一个线程池对象，控制要创建几个线程对象。
		// public static ExecutorService newFixedThreadPool(int nThreads)
		// ExecutorService pool = Executors.newFixedThreadPool(2);
		// 可以执行Runnable对象或者Callable对象代表的线程
		// pool.submit(new MyRunnable());
		// pool.submit(new MyRunnable());

		// 结束线程池
		// pool.shutdown();

		Pipeline pipeline = jedis.pipelined();
		for (int i = 0; i < 100; i++) {

			pipeline.lpush("test_list", (i + 1) + "");
		}
		pipeline.sync();
		jedis.close();
	}

	public static void main(String[] args) {

		Jedis jedis = new Jedis("106.14.37.153", 6379);
		// 权限认证
		jedis.auth("huashi_redis_99088@");

		// 创建一个线程池对象，控制要创建几个线程对象。
		// public static ExecutorService newFixedThreadPool(int nThreads)
		ExecutorService pool = Executors.newFixedThreadPool(20);
		// 可以执行Runnable对象或者Callable对象代表的线程

		for (int i = 0; i < 2; i++) {
			pool.submit(new ProduceThread(jedis));
		}

		// 结束线程池
		pool.shutdown();
	}

	static class ProduceThread implements Runnable {

		private AtomicInteger counter = new AtomicInteger(0);
		private Jedis jedis;

		public ProduceThread(Jedis jedis) {
			this.jedis = jedis;
		}

		@Override
		public void run() {
			while (true) {
				String value = jedis.lpop("test_list");
				if(StringUtils.isEmpty(value))
					break;
				
				System.out.println("线程号：[" + Thread.currentThread().getName() + "] 当前数据：" + value + "计数器：" + counter.incrementAndGet());
			}

		}
	}
}
