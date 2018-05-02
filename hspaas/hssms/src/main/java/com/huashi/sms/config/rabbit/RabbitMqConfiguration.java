package com.huashi.sms.config.rabbit;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
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
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.huashi.common.util.IdGenerator;
import com.huashi.sms.config.converter.FastJsonMessageConverter;
import com.huashi.sms.config.rabbit.constant.RabbitConstant;

/**
 * TODO 短信消息队列配置信息
 * template必须为原型模式：如果需要在生产者需要消息发送后的回调，需要对rabbitTemplate设置ConfirmCallback对象，
 * 由于不同的生产者需要对应不同的ConfirmCallback，如果rabbitTemplate设置为单例bean，则所有的rabbitTemplate
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

    @Value("${mq.rabbit.consumers}")
    private int concurrentConsumers;
    @Value("${mq.rabbit.maxconsumers}")
    private int maxConcurrentConsumers;

    @Value("${mq.rabbit.prefetch:1}")
    private int rabbitPrefetchCount;

    @Bean
    public IdGenerator idGenerator() {
        return new IdGenerator(1);
    }

    @Bean(name = "messageConverter")
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public FastJsonMessageConverter fastjsonMessageConverter() {
        return new FastJsonMessageConverter();
    }

    @Bean(name = "rabbitConnectionFactory")
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(mqHost);
        connectionFactory.setPort(mqPort);
        connectionFactory.setUsername(mqUsername);
        connectionFactory.setPassword(mqPassword);
        connectionFactory.setVirtualHost(mqVhost);

        // 显性设置后才能进行回调函数设置 enable confirm mode
        connectionFactory.setPublisherReturns(true);
        connectionFactory.setPublisherConfirms(true);

        // 默认 connectionFactory.setCacheMode(CacheMode.CHANNEL), ConnectionCacheSize无法设置
        connectionFactory.setChannelCacheSize(100);
//		connectionFactory.setConnectionCacheSize(5);

        connectionFactory.setRequestedHeartBeat(60);

        // 设置超时时间15秒
        connectionFactory.setConnectionTimeout(15000);

        // 断开重连接，保证数据无丢失,默认为true
//		connectionFactory.setAutomaticRecoveryEnabled(true);

        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(@Qualifier("rabbitConnectionFactory") ConnectionFactory rabbitConnectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitConnectionFactory);
        DirectExchange exchange = new DirectExchange(RabbitConstant.EXCHANGE_SMS, true, false);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.setAutoStartup(true);


        // 待分包处理队列
        Queue smsWaitProcessQueue = new Queue(RabbitConstant.MQ_SMS_MT_WAIT_PROCESS, true, false, false, setQueueFeatures());
        Binding smsWaitProcessBinding = BindingBuilder.bind(smsWaitProcessQueue).to(exchange).with(RabbitConstant.MQ_SMS_MT_WAIT_PROCESS);
        rabbitAdmin.declareQueue(smsWaitProcessQueue);
        rabbitAdmin.declareBinding(smsWaitProcessBinding);

        // 待点对点短信分包处理队列
        Queue smsP2PWaitProcessQueue = new Queue(RabbitConstant.MQ_SMS_MT_P2P_WAIT_PROCESS, true, false, false, setQueueFeatures());
        Binding smsP2PWaitProcessBinding = BindingBuilder.bind(smsP2PWaitProcessQueue).to(exchange).with(RabbitConstant.MQ_SMS_MT_P2P_WAIT_PROCESS);
        rabbitAdmin.declareQueue(smsP2PWaitProcessQueue);
        rabbitAdmin.declareBinding(smsP2PWaitProcessBinding);

        // 分包处理完成，待提交网关（上家通道）队列
//		Queue smsWaitSubmitQueue = new Queue(RabbitConstant.MQ_SMS_MT_WAIT_SUBMIT, true, false, false, setQueueFeatures());
//		Binding smsWaitSubmitBinding = BindingBuilder.bind(smsWaitSubmitQueue).to(exchange)
//				.with(RabbitConstant.MQ_SMS_MT_WAIT_SUBMIT);
//		rabbitAdmin.declareQueue(smsWaitSubmitQueue);
//		rabbitAdmin.declareBinding(smsWaitSubmitBinding);

        // 网关回执数据，带解析回执数据并推送
        Queue smsWaitReceiptQueue = new Queue(RabbitConstant.MQ_SMS_MT_WAIT_RECEIPT, true, false, false, setQueueFeatures());
        Binding smsWaitReceiptBinding = BindingBuilder.bind(smsWaitReceiptQueue).to(exchange).with(RabbitConstant.MQ_SMS_MT_WAIT_RECEIPT);
        rabbitAdmin.declareQueue(smsWaitReceiptQueue);
        rabbitAdmin.declareBinding(smsWaitReceiptBinding);

        // 用户上行回复记录
        Queue moReceiveQueue = new Queue(RabbitConstant.MQ_SMS_MO_RECEIVE, true, false, false, setQueueFeatures());
        Binding moReceiveBinding = BindingBuilder.bind(moReceiveQueue).to(exchange).with(RabbitConstant.MQ_SMS_MO_RECEIVE);
        rabbitAdmin.declareQueue(moReceiveQueue);
        rabbitAdmin.declareBinding(moReceiveBinding);

        // 用户推送数据，带解析推送数据并推送(上行)
        Queue moWaitPushQueue = new Queue(RabbitConstant.MQ_SMS_MO_WAIT_PUSH, true, false, false, setQueueFeatures());
        Binding moWaitPushBinding = BindingBuilder.bind(moWaitPushQueue).to(exchange).with(RabbitConstant.MQ_SMS_MO_WAIT_PUSH);
        rabbitAdmin.declareQueue(moWaitPushQueue);
        rabbitAdmin.declareBinding(moWaitPushBinding);

        // 下行分包异常，如黑名单数据
        Queue packetsExceptionQueue = new Queue(RabbitConstant.MQ_SMS_MT_PACKETS_EXCEPTION, true, false, false, setQueueFeatures());
        Binding packetsExceptionBinding = BindingBuilder.bind(packetsExceptionQueue).to(exchange).with(RabbitConstant.MQ_SMS_MT_PACKETS_EXCEPTION);
        rabbitAdmin.declareQueue(packetsExceptionQueue);
        rabbitAdmin.declareBinding(packetsExceptionBinding);

        return rabbitAdmin;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    RabbitTemplate rabbitTemplate(@Qualifier("rabbitConnectionFactory") ConnectionFactory rabbitConnectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        rabbitTemplate.setRetryTemplate(retryTemplate());
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setReceiveTimeout(60000);

        return rabbitTemplate;
    }

    /**
     * 设置重试模板
     */
    @Bean
    public RetryTemplate retryTemplate() {
        // 重试模板
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMultiplier(10.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        // 尝试最大重试次数
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    /**
     * TODO 设置队列属性
     */
    private Map<String, Object> setQueueFeatures() {
        Map<String, Object> args = new HashMap<>();
        // 最大优先级定义
        args.put("x-max-priority", 10);
        // 过期时间，单位毫秒
//		args .put("x-message-ttl", 60000);
        // 30分钟，单位毫秒
//		args.put("x-expires", 1800000); 
        // 集群高可用，数据复制
        args.put("x-ha-policy", "all");
        return args;
    }

    // @Bean
    // MessageListenerAdapter listenerAdapter(SmsQueueService smsQueueService) {
    // return new MessageListenerAdapter(smsQueueService, "receiveMessage");

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(@Qualifier("rabbitConnectionFactory") ConnectionFactory rabbitConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitConnectionFactory);

        factory.setConcurrentConsumers(concurrentConsumers);
        factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
        // 消息失败重试后 设置 RabbitMQ把消息分发给有空的cumsuer，同 channel.basicQos(1);
        factory.setPrefetchCount(rabbitPrefetchCount);
//		factory.setConsumerTagStrategy(consumerTagStrategy);


        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(messageConverter());

//		factory.setMissingQueuesFatal(missingQueuesFatal);

//		ExecutorService service = Executors.newFixedThreadPool(500);
//		factory.setTaskExecutor(service);
        
        factory.setAutoStartup(true);
        
        return factory;
    }

//	@Bean
//	public SimpleMessageListenerContainer simpleMessageListenerContainer(
//			@Qualifier("rabbitConnectionFactory") ConnectionFactory rabbitConnectionFactory) {
//		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(rabbitConnectionFactory);
//		
//		container.setConcurrentConsumers(concurrentConsumers);
//		container.setMaxConcurrentConsumers(maxConcurrentConsumers);
//		
//		// 设置公平分发，同 channel.basicQos(1);
//		container.setPrefetchCount(rabbitPrefetchCount);
//		container.setMessageConverter(messageConverter());
//		
//		container.setQueueNames(RabbitConstant.MQ_SMS_MT_WAIT_PROCESS, RabbitConstant.MQ_SMS_MT_WAIT_RECEIPT, RabbitConstant.MQ_SMS_MO_RECEIVE, 
//				RabbitConstant.MQ_SMS_MO_WAIT_PUSH, RabbitConstant.MQ_SMS_MT_PACKETS_EXCEPTION);
//
//		
//		// 设置是否自动启动
//		container.setAutoStartup(true);
//		// 设置拦截器信息
//		// container.setAdviceChain(adviceChain);
//
//		// 设置事务
//		// container.setChannelTransacted(true);
//
//		// 设置优先级
////		container.setConsumerArguments(Collections.<String, Object> singletonMap("x-priority", Integer.valueOf(10)));
//		container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置确认模式手工确认
//		
//		return container;
//	}
}
