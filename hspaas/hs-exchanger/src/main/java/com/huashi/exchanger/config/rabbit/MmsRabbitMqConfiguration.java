package com.huashi.exchanger.config.rabbit;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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

/**
 * TODO 彩信消息队列配置信息 template必须为原型模式：如果需要在生产者需要消息发送后的回调，需要对rabbitTemplate设置ConfirmCallback对象，
 * 由于不同的生产者需要对应不同的ConfirmCallback，如果rabbitTemplate设置为单例bean，则所有的rabbitTemplate
 * 
 * @author zhengying
 * @version V1.0.0
 * @date 2016年10月3日 下午11:56:40
 */
@Configuration
public class MmsRabbitMqConfiguration {

    /*-----------------------------------彩信下行队列-----------------------------------------*/
    // 彩信下行 已完成上家通道调用，待网关回执队列
    public static final String MQ_MMS_MT_WAIT_RECEIPT = "mq_mms_mt_wait_receipt";

    /*-----------------------------------彩信上行队列----------------------------------------*/
    // 彩信上行回执数据
    public static final String MQ_MMS_MO_RECEIVE      = "mq_mms_mo_receive";

    @Value("${mq.rabbit.host}")
    private String             mqHost;
    @Value("${mq.rabbit.port}")
    private Integer            mqPort;
    @Value("${mq.rabbit.username}")
    private String             mqUsername;
    @Value("${mq.rabbit.password}")
    private String             mqPassword;
    @Value("${mq.rabbit.vhost.mms}")
    private String             mqVhost;

    @Bean("mmsConnectionFactory")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(mqHost);
        connectionFactory.setPort(mqPort);
        connectionFactory.setUsername(mqUsername);
        connectionFactory.setPassword(mqPassword);
        connectionFactory.setVirtualHost(mqVhost);
        connectionFactory.setPublisherReturns(true);
        // 显性设置后才能进行回调函数设置
        connectionFactory.setPublisherConfirms(true);

        return connectionFactory;
    }

    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean("mmsRabbitTemplate")
    RabbitTemplate mmsRabbitTemplate(@Qualifier("mmsConnectionFactory") ConnectionFactory mmsConnectionFactory,
                                     Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate mmsRabbitTemplate = new RabbitTemplate(mmsConnectionFactory);

        mmsRabbitTemplate.setMessageConverter(messageConverter);

        setRetryTemplate(mmsRabbitTemplate);

        return mmsRabbitTemplate;
    }

    /**
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

}
