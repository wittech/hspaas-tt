package com.huashi.sms.test.redis;

import redis.clients.jedis.Jedis;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisProduceTest extends RedisBasicTest {

    public static void main(String[] args) throws InterruptedException {

        Integer threadNum = 10;

        CountDownLatch cdl = new CountDownLatch(threadNum);

        // public static ExecutorService newFixedThreadPool(int nThreads)
        ExecutorService pool = Executors.newFixedThreadPool(20);

        for (int i = 0; i < threadNum; i++) {
            pool.execute(new ProduceThread(cdl));
        }

        long start = System.currentTimeMillis();
        cdl.await();

        System.out.println("共耗时：" + (System.currentTimeMillis() - start) + "ms");

        // 结束线程池
        pool.shutdown();
    }

    static class ProduceThread implements Runnable {

        private CountDownLatch cdl;

        public ProduceThread(CountDownLatch cdl) {
            super();
            this.cdl = cdl;
        }

        @Override
        public void run() {

            Jedis jedis = getJedis();
            int length = 1000;
            String[] ss = new String[length];
            long start = System.currentTimeMillis();
            for (int i = 0; i < length; i++) {
                // logger.info("----------" + i);
                ss[i] = Thread.currentThread().getName() + "_" + i + System.nanoTime();
                // jedis.rpush("test_list_queue_10", Thread.currentThread().getName() + "_" + i +
                // System.currentTimeMillis());
            }

            jedis.rpush("test_list_queue_20", ss);

            jedis.close();
            cdl.countDown();
            logger.info(Thread.currentThread().getName() + "耗时：" + (System.currentTimeMillis() - start));

        }
    }

}
