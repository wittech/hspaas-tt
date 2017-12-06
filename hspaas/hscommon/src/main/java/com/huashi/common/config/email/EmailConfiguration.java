package com.huashi.common.config.email;

import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class EmailConfiguration {
	
	@Value("${notice.email.host}")
	private String host;
	@Value("${notice.email.port}")
	private int port;
	@Value("${notice.email.username}")
	private String username;
	@Value("${notice.email.password}")
	private String password;
	@Value("${notice.email.encoding}")
	private String defaultEncoding;

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
		javaMailSenderImpl.setHost(host);
		javaMailSenderImpl.setPort(port);
		javaMailSenderImpl.setUsername(username);
		javaMailSenderImpl.setPassword(password);
		javaMailSenderImpl.setDefaultEncoding(defaultEncoding);
		
		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mmail.debug", "false");
		javaMailProperties.put("mail.smtp.auth", "true");
		javaMailProperties.put("mail.transport.protocol", "smtp");
		javaMailProperties.put("mail.smtp.starttls.enable", "false");
		javaMailSenderImpl.setJavaMailProperties(javaMailProperties);
		
		return javaMailSenderImpl;
	}
	
	/**
	 * 配置线程池
	 * 
	 * @return
	 */
	@Bean(name = "emailPoolTaskExecutor")
	ThreadPoolTaskExecutor threadPoolTaskExecutor() {

		ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();

		// 线程池所使用的缓冲队列
		poolTaskExecutor.setQueueCapacity(25); // 队列大一点，以应对峰值压力
		// 线程池维护线程的最少数量
		poolTaskExecutor.setCorePoolSize(5); // 处理签名合成，并处理邮件和短信发送,pdf图片转换
		// 线程池维护线程的最大数量
		poolTaskExecutor.setMaxPoolSize(10); // 该值不能太大，tomcat 默认最大线程为200.
		// 线程池维护线程所允许的空闲时间
		poolTaskExecutor.setKeepAliveSeconds(10000); // 10分

		// rejection-policy：当pool已经达到max size的时候，如何处理新任务
		// CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
		poolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

		poolTaskExecutor.initialize();

		return poolTaskExecutor;
	}
}
