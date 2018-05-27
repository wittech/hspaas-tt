package com.huashi.sms.test.redis;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisConsumeTest extends RedisBasicTest{

	public static void main(String[] args) throws InterruptedException {
	    Integer threadNum = 64;
	    CountDownLatch cdl = new CountDownLatch(threadNum);

		// public static ExecutorService newFixedThreadPool(int nThreads)
		ExecutorService pool = Executors.newFixedThreadPool(200);
		// 可以执行Runnable对象或者Callable对象代表的线程

		for (int i = 0; i < threadNum; i++) {
			pool.execute(new ConsumeThread(cdl));

//			pool.submit(new ProduceThread(jedis));
		}
		
		long start = System.currentTimeMillis();
		cdl.await();
		
		System.out.println("共耗时：" + (System.currentTimeMillis()- start) + "ms");
		

		// 结束线程池
		pool.shutdown();
	}

	static class ConsumeThread implements Runnable {
	    
	    private CountDownLatch cdl;
	    
		public ConsumeThread(CountDownLatch cdl) {
            super();
            this.cdl = cdl;
        }

        @Override
		public void run() {
			Jedis jedis = getJedis();
			long start = System.currentTimeMillis();
			while (true) {

				String value = jedis.lpop("test_list_queue_20");
				if(StringUtils.isEmpty(value)) {
				    cdl.countDown();
					logger.info(Thread.currentThread().getName() + "耗时：" + (System.currentTimeMillis() - start));
					jedis.close();
					break;
				}

//				logger.info(Thread.currentThread().getName() + "_value:" + value);
			}

		}
	}
}
