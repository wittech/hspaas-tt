package com.huashi.exchanger.test;

import java.util.concurrent.CountDownLatch;

public class MultiLockInThread {

    static int            THREAD_NUM = 10;
    static CountDownLatch cdl        = new CountDownLatch(THREAD_NUM);

    private static int           i          = 0;

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
                
                synchronized (passageId) {
                    for(int j=0;j<10;j++) {
                        i++;
                        System.out.println(Thread.currentThread().getName() + "-------------"+ passageId + "--------" + i);
                    }
                }
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            super.run();
        }
    }

    public static void main(String[] args) {

        for (int i = 0; i < THREAD_NUM; i++) {
            Thread thread = new MyThread(new Integer(i));
            thread.start();
            cdl.countDown();
        }
    }
}
