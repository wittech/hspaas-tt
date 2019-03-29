package com.huashi.listener.config.rabbit;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.support.RetryTemplate;

/**
 * TODO 短信消息队列配置信息 template必须为原型模式：如果需要在生产者需要消息发送后的回调，需要对rabbitTemplate设置ConfirmCallback对象，
 * 由于不同的生产者需要对应不同的ConfirmCallback，如果rabbitTemplate设置为单例bean，则所有的rabbitTemplate
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年10月3日 下午11:56:40
 */
@Configuration
public class SmsRabbitMqConfiguration {

    @Value("${mq.rabbit.host}")
    private String             mqHost;
    @Value("${mq.rabbit.port}")
    private Integer            mqPort;
    @Value("${mq.rabbit.username}")
    private String             mqUsername;
    @Value("${mq.rabbit.password}")
    private String             mqPassword;
    @Value("${mq.rabbit.vhost.sms}")
    private String             mqVhost;

    @Bean("smsConnectionFactory")
    @Primary
    public ConnectionFactory smsConnectionFactory() {
        CachingConnectionFactory smsConnectionFactory = new CachingConnectionFactory();
        smsConnectionFactory.setHost(mqHost);
        smsConnectionFactory.setPort(mqPort);
        smsConnectionFactory.setUsername(mqUsername);
        smsConnectionFactory.setPassword(mqPassword);
        smsConnectionFactory.setVirtualHost(mqVhost);
        smsConnectionFactory.setPublisherReturns(true);
        // 显性设置后才能进行回调函数设置
        smsConnectionFactory.setPublisherConfirms(true);

        return smsConnectionFactory;
    }

    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean("smsRabbitTemplate")
    RabbitTemplate smsRabbitTemplate(@Qualifier("smsConnectionFactory") ConnectionFactory smsConnectionFactory,
                                     Jackson2JsonMessageConverter messageConverter, RetryTemplate retryTemplate) {
        RabbitTemplate smsRabbitTemplate = new RabbitTemplate(smsConnectionFactory);

        smsRabbitTemplate.setMessageConverter(messageConverter);

        smsRabbitTemplate.setRetryTemplate(retryTemplate);

        return smsRabbitTemplate;
    }

}
