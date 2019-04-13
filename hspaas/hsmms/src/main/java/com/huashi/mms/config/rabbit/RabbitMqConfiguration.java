package com.huashi.mms.config.rabbit;

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
import org.springframework.core.annotation.Order;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.huashi.common.util.IdGenerator;
import com.huashi.mms.config.converter.FastJsonMessageConverter;
import com.huashi.mms.config.rabbit.constant.RabbitConstant;

/**
 * TODO RabbitMQ消息队列配置
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月12日 下午3:52:17
 */
@Configuration
@EnableRabbit
@Order(4)
public class RabbitMqConfiguration {

    @Value("${mq.rabbit.host}")
    private String  mqHost;
    @Value("${mq.rabbit.port}")
    private Integer mqPort;
    @Value("${mq.rabbit.username}")
    private String  mqUsername;
    @Value("${mq.rabbit.password}")
    private String  mqPassword;
    @Value("${mq.rabbit.vhost}")
    private String  mqVhost;

    /**
     * 初始化消费者数量
     */
    @Value("${mq.rabbit.consumers}")
    private int     concurrentConsumers;

    /**
     * 消费者最大数量（当队列堆积过量，则会自动增加消费者，直至达到最大）
     */
    @Value("${mq.rabbit.maxconsumers}")
    private int     maxConcurrentConsumers;

    /**
     * 每次队列中取数据个数，如果为1则保证消费者间平均消费
     */
    @Value("${mq.rabbit.prefetch:1}")
    private int     rabbitPrefetchCount;

    /**
     * TODO 消息ID生成器（全局单例）
     * 
     * @return
     */
    @Bean
    public IdGenerator idGenerator() {
        return new IdGenerator(1);
    }

    /**
     * TODO 队列数据序列化，采用jackson2
     * 
     * @return
     */
    @Bean(name = "messageConverter")
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * TODO 消息序列化，fastjson(暂未使用)
     * 
     * @return
     */
    @Bean
    public FastJsonMessageConverter fastjsonMessageConverter() {
        return new FastJsonMessageConverter();
    }

    /**
     * TODO rabbitmq连接配置
     * 
     * @return
     */
    @Bean(name = "rabbitConnectionFactory")
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(mqHost);
        connectionFactory.setPort(mqPort);
        connectionFactory.setUsername(mqUsername);
        connectionFactory.setPassword(mqPassword);
        connectionFactory.setVirtualHost(mqVhost);

        // 显性设置后才能进行回调函数设置 enable confirm mode
        // confirm 是为了保障数据发送到队列的必答性（对于发送者）
        connectionFactory.setPublisherReturns(true);
        connectionFactory.setPublisherConfirms(true);

        // 默认 connectionFactory.setCacheMode(CacheMode.CHANNEL), ConnectionCacheSize无法设置
        connectionFactory.setChannelCacheSize(100);
        // connectionFactory.setConnectionCacheSize(5);

        connectionFactory.setRequestedHeartBeat(60);

        // 设置超时时间15秒
        connectionFactory.setConnectionTimeout(15000);

        // 断开重连接，保证数据无丢失,默认为true
        // connectionFactory.setAutomaticRecoveryEnabled(true);

        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(@Qualifier("rabbitConnectionFactory") ConnectionFactory rabbitConnectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitConnectionFactory);
        DirectExchange exchange = new DirectExchange(RabbitConstant.EXCHANGE_MMS, true, false);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.setAutoStartup(true);

        // 待分包处理队列
        Queue mmsWaitProcessQueue = new Queue(RabbitConstant.MQ_MMS_MT_WAIT_PROCESS, true, false, false,
                                              setQueueFeatures());
        Binding smsWaitProcessBinding = BindingBuilder.bind(mmsWaitProcessQueue).to(exchange).with(RabbitConstant.MQ_MMS_MT_WAIT_PROCESS);
        rabbitAdmin.declareQueue(mmsWaitProcessQueue);
        rabbitAdmin.declareBinding(smsWaitProcessBinding);

        // 网关回执数据，带解析回执数据并推送
        Queue mmsWaitReceiptQueue = new Queue(RabbitConstant.MQ_MMS_MT_WAIT_RECEIPT, true, false, false,
                                              setQueueFeatures());
        Binding smsWaitReceiptBinding = BindingBuilder.bind(mmsWaitReceiptQueue).to(exchange).with(RabbitConstant.MQ_MMS_MT_WAIT_RECEIPT);
        rabbitAdmin.declareQueue(mmsWaitReceiptQueue);
        rabbitAdmin.declareBinding(smsWaitReceiptBinding);

        // 用户上行回复记录
        Queue moReceiveQueue = new Queue(RabbitConstant.MQ_MMS_MO_RECEIVE, true, false, false, setQueueFeatures());
        Binding moReceiveBinding = BindingBuilder.bind(moReceiveQueue).to(exchange).with(RabbitConstant.MQ_MMS_MO_RECEIVE);
        rabbitAdmin.declareQueue(moReceiveQueue);
        rabbitAdmin.declareBinding(moReceiveBinding);

        // 用户推送数据，带解析推送数据并推送(上行)
        Queue moWaitPushQueue = new Queue(RabbitConstant.MQ_MMS_MO_WAIT_PUSH, true, false, false, setQueueFeatures());
        Binding moWaitPushBinding = BindingBuilder.bind(moWaitPushQueue).to(exchange).with(RabbitConstant.MQ_MMS_MO_WAIT_PUSH);
        rabbitAdmin.declareQueue(moWaitPushQueue);
        rabbitAdmin.declareBinding(moWaitPushBinding);

        // 下行分包异常，如黑名单数据
        Queue packetsExceptionQueue = new Queue(RabbitConstant.MQ_MMS_MT_PACKETS_EXCEPTION, true, false, false,
                                                setQueueFeatures());
        Binding packetsExceptionBinding = BindingBuilder.bind(packetsExceptionQueue).to(exchange).with(RabbitConstant.MQ_MMS_MT_PACKETS_EXCEPTION);
        rabbitAdmin.declareQueue(packetsExceptionQueue);
        rabbitAdmin.declareBinding(packetsExceptionBinding);

        // 模板报备回执
        Queue modelReceiveQueue = new Queue(RabbitConstant.MQ_MMS_MODEL_RECEIVE, true, false, false, setQueueFeatures());
        Binding modelReceiveBinding = BindingBuilder.bind(modelReceiveQueue).to(exchange).with(RabbitConstant.MQ_MMS_MODEL_RECEIVE);
        rabbitAdmin.declareQueue(modelReceiveQueue);
        rabbitAdmin.declareBinding(modelReceiveBinding);

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
        // args .put("x-message-ttl", 60000);
        // 30分钟，单位毫秒
        // args.put("x-expires", 1800000);
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
        // factory.setConsumerTagStrategy(consumerTagStrategy);

        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(messageConverter());

        // factory.setMissingQueuesFatal(missingQueuesFatal);

        // ExecutorService service = Executors.newFixedThreadPool(500);
        // factory.setTaskExecutor(service);

        factory.setAutoStartup(true);

        return factory;
    }

}
