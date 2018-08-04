package com.huashi.exchanger.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class MultiLockInThread {

    static int                          THREAD_NUM     = 10;
    static CountDownLatch               cdl            = new CountDownLatch(THREAD_NUM);

    private static Map<Integer, Object> passageMonitor = new ConcurrentHashMap<>();

    private static class MyThread extends Thread {

        private Integer passageId;

        public MyThread(Integer passageId) {
            super();
            this.passageId = passageId;
        }

        @Override
        public void run() {
            try {
                cdl.await();

                lockMonitor(passageId);

                print(passageId);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            super.run();
        }
    }

    private static int counter = 0;

    private static void lockMonitor(Integer passage) {
        passageMonitor.putIfAbsent(passage, new Object());
    }

    private static void print(Integer passageId) {
        synchronized (passageMonitor.get(passageId)) {
            System.out.println(Thread.currentThread().getName() + "------" + passageId + "_____" + counter);
            counter++;
        }

    }

    public static void main(String[] args) {
        for (int i = 0; i < THREAD_NUM; i++) {
            Thread thread = new MyThread(new Integer(1));
            thread.start();
            cdl.countDown();
        }
    }
}
