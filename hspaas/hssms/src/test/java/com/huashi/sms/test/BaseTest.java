package com.huashi.sms.test;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.huashi.common.util.IdGenerator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-dubbo-consumer.xml"})
public class BaseTest {

	private String mqHost = "139.196.240.42";
	private Integer mqPort = 5672;
	private String mqUsername = "hspaas";
	private String mqPassword = "hspaas";
	private String mqVhost = "/sms_host";
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public IdGenerator idGenerator() {
		return new IdGenerator(1);
	}

	public Jackson2JsonMessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setHost(mqHost);
		connectionFactory.setPort(mqPort);
		connectionFactory.setUsername(mqUsername);
		connectionFactory.setPassword(mqPassword);
		connectionFactory.setVirtualHost(mqVhost);
//		connectionFactory.setPublisherReturns(true); // enable confirm mode
		// 显性设置后才能进行回调函数设置
//		connectionFactory.setPublisherConfirms(true);

		return connectionFactory;
	}

	RabbitTemplate rabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
		// rabbitTemplate.setQueue(SmsQueueConstant.MQ_SMS_MT_WAIT_PROCESS);
		// rabbitTemplate.setRoutingKey(SmsQueueConstant.MQ_SMS_MT_WAIT_PROCESS);

		rabbitTemplate.setMessageConverter(messageConverter());

		return rabbitTemplate;
	}
	
	
}
