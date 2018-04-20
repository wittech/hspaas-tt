package com.huashi.sms.test.thread;


public class ThreadGroupTest {

    static class MyThread extends Thread {

        @Override
        public void run() {
            System.out.println("启动");
            try {
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        
    }
}
