package com.huashi.listener.config;

import java.util.Collections;
import java.util.Map;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.huashi.common.util.IdGenerator;
import com.huashi.sms.task.context.MQConstant;

/**
 * 
 * TODO 短信消息队列配置信息
 * template必须为原型模式：如果需要在生产者需要消息发送后的回调，需要对rabbitTemplate设置ConfirmCallback对象，
 * 由于不同的生产者需要对应不同的ConfirmCallback，如果rabbitTemplate设置为单例bean，则所有的rabbitTemplate
 * 
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年10月3日 下午11:56:40
 */
@Configuration
@EnableRabbit
public class RabbitMqConfiguration {

	@Value("${mq.rabbit.host}")
	private String mqHost;
	@Value("${mq.rabbit.port}")
	private Integer mqPort;
	@Value("${mq.rabbit.username}")
	private String mqUsername;
	@Value("${mq.rabbit.password}")
	private String mqPassword;
	@Value("${mq.rabbit.vhost}")
	private String mqVhost;

	@Bean
	public IdGenerator idGenerator() {
		return new IdGenerator(1);
	}

	@Bean
	public Jackson2JsonMessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setHost(mqHost);
		connectionFactory.setPort(mqPort);
		connectionFactory.setUsername(mqUsername);
		connectionFactory.setPassword(mqPassword);
		connectionFactory.setVirtualHost(mqVhost);
		connectionFactory.setPublisherReturns(true); // enable confirm mode
		// 显性设置后才能进行回调函数设置
		connectionFactory.setPublisherConfirms(true);

		return connectionFactory;
	}

	@Bean
	public AmqpAdmin amqpAdmin(
			@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
		AmqpAdmin amqpAdmin = new RabbitAdmin(connectionFactory);
		DirectExchange exchange =  new DirectExchange(MQConstant.EXCHANGE_SMS, true, false);
		amqpAdmin.declareExchange(exchange);
		
		// 网关回执数据，带解析回执数据并推送
		Queue smsWaitReceiptQueue = new Queue(MQConstant.MQ_SMS_MT_WAIT_RECEIPT, true, false, false, setMaxPriority());
		Binding smsWaitReceiptBinding = BindingBuilder.bind(smsWaitReceiptQueue).to(exchange)
				.with(MQConstant.MQ_SMS_MT_WAIT_RECEIPT);
		amqpAdmin.declareQueue(smsWaitReceiptQueue);
		amqpAdmin.declareBinding(smsWaitReceiptBinding);
		
		return amqpAdmin;
	}

	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	RabbitTemplate rabbitTemplate(
			@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		// rabbitTemplate.setQueue(SmsQueueConstant.MQ_SMS_MT_WAIT_PROCESS);
		// rabbitTemplate.setRoutingKey(SmsQueueConstant.MQ_SMS_MT_WAIT_PROCESS);

		rabbitTemplate.setMessageConverter(messageConverter());

		setRetryTemplate(rabbitTemplate);

		return rabbitTemplate;
	}

	/**
	 * 
	 * TODO 设置重试模板
	 * 
	 * @param rabbitTemplate
	 */
	private void setRetryTemplate(RabbitTemplate rabbitTemplate) {
		// 重试模板
		RetryTemplate retryTemplate = new RetryTemplate();
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(500);
		backOffPolicy.setMultiplier(10.0);
		backOffPolicy.setMaxInterval(10000);
		retryTemplate.setBackOffPolicy(backOffPolicy);
		rabbitTemplate.setRetryTemplate(retryTemplate);
	}

	// @Bean
	// public StatefulRetryOperationsInterceptor interceptor() {
	// return RetryInterceptorBuilder.stateful().maxAttempts(5)
	// .backOffOptions(1000, 2.0, 10000) // initialInterval, multiplier,
	// maxInterval
	// .build();
	// }

	// 设置队列最大优先级
	private Map<String, Object> setMaxPriority() {
		return Collections.<String, Object> singletonMap("x-max-priority",
				Integer.valueOf(10));
	}

}
