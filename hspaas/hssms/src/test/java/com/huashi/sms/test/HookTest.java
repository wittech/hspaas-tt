package com.huashi.sms.test;



public class HookTest {

	public static void start() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Execute Hook.....");
			}
		}));
	}

	public static void main(String[] args) {
		start();
		System.out.println("The Application is doing something");
		
        int i =0;
		
		while(true) {
			System.out.println(System.currentTimeMillis());
			i++;
			if(i >=10)
			    break;
		}
		
		
		
//		try {
//			TimeUnit.MILLISECONDS.sleep(5000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
}
