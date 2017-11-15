package com.huashi.sms.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class MessageDeliverTest extends BaseTest{
	
	int counter = 0;
	
	@Test
	public void test() throws InterruptedException {
		// 创建一个固定大小的线程池
		ExecutorService service = Executors.newCachedThreadPool();
		for (int i = 0; i < 64; i++) {
			// 在未来某个时间执行给定的命令
			service.execute(new PrintThread());
		}
		// 关闭启动线程
		service.shutdown();
		// 等待子线程结束，再继续执行下面的代码
		service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		System.out.println("all thread complete");
	}
	
	class PrintThread implements Runnable {

		@Override
		public void run() {
			counter ++;
			logger.info(String.format("计数器值为：%d, 线程：%s", counter, Thread.currentThread().getName()));
		}
		
	}
}
