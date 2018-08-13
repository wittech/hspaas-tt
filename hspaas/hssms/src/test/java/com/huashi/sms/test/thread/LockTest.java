package com.huashi.sms.test.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

public class LockTest {

    CountDownLatch cdl = new CountDownLatch(1);
    ExecutorService pool = Executors.newFixedThreadPool(200);
    static ReentrantLock slock = new ReentrantLock();

    private static class LockThread implements Runnable {

        public void run() {
            for(int i = 0; i<5; i++) {
                lockProcess(i);
//                try {
//                    Thread.sleep(1000l);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
            }
            
        }
    }

    private static void lockProcess(int i) {
//        Lock lock = new ReentrantLock();
        
        Lock lock = slock;
        // 分布式锁开启
        lock.lock();
        try {
            System.out.println("I am thread-" + Thread.currentThread().getName() + "["+i+"]");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("I am thread-" + Thread.currentThread().getName() + "["+i+"] 释放锁");
        }
    }

    @Test
    public void test() {
        for (int i = 0; i < 4; i++) {
            pool.execute(new LockThread());
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        pool.shutdown();
    }
}
