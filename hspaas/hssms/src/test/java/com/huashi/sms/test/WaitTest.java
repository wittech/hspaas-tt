package com.huashi.sms.test;

import com.huashi.common.user.domain.User;


public class WaitTest {
    
    static class MyThread implements Runnable {

        public void run() {
            System.out.println("helllo");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("test");
        User user = new User();
        synchronized (user) {
            user.wait(3000L);
        }
        System.out.println("test ok");
    }
}
