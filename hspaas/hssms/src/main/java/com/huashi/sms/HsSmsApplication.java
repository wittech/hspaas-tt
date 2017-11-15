package com.huashi.sms;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.huashi.common.util.LogUtils;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
// @ServletComponentScan
@ImportResource({ "classpath:spring.xml" })
public class HsSmsApplication {
	
	public static int DUBBO_APPLICATION_PORT;

	public static void main(String args[]) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(HsSmsApplication.class, args);
		
		loadApplicationPort(applicationContext);
		
		LogUtils.info("华时短信服务项目已启动");
		
		release();
		
	}
	
	/**
	 * 
	   * TODO 输入并初始化端口号
	   * 
	   * @param applicationContext
	 */
	private static void loadApplicationPort(ConfigurableApplicationContext applicationContext) {
		Map<String, ProtocolConfig> beansOfType = applicationContext.getBeansOfType(ProtocolConfig.class);
		for (Entry<String, ProtocolConfig> item : beansOfType.entrySet()) {
			DUBBO_APPLICATION_PORT = NetUtils.getAvailablePort();
			item.getValue().setPort(DUBBO_APPLICATION_PORT);
		}
	}

	/**
	 * 
	   * TODO JVM结束后调用（目前仅测试，后续可能加入一些资源销毁处理）
	 */
	public static void release() {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				LogUtils.info("华时短信服务项目关闭!");
			}

		});
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}
	}

}
