package com.huashi.sms.test;

import java.util.concurrent.CountDownLatch;

public class WaitTest {

    static class MyThread extends Thread {

        private CountDownLatch cdl;
        private int            index;

        public MyThread(CountDownLatch cdl, int index) {
            this.cdl = cdl;
            this.index = index;
        }

        public void run() {
            System.out.println("shutdown hook index :[" + index + "] executing");
            cdl.countDown();
        }
    }

    public static void main(String[] args) {
        int size = 2;
        CountDownLatch cdl = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            Runtime.getRuntime().addShutdownHook(new MyThread(cdl, i));
        }

        System.out.println("执行完毕");
        
//        try {
//            cdl.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        
        System.exit(1);

    }
}
