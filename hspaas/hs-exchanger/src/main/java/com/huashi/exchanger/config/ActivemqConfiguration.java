package com.huashi.exchanger.config;

import javax.jms.ConnectionFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.huashi.common.util.IdGenerator;

@Configuration
@EnableJms
public class ActivemqConfiguration {
    
    /*-----------------------------------短信下行队列-----------------------------------------*/
    // 短信下行 已完成上家通道调用，待网关回执队列
    public static final String MQ_SMS_MT_WAIT_RECEIPT = "mq_sms_mt_wait_receipt";

    /*-----------------------------------短信上行队列----------------------------------------*/
    // 短信上行回执数据
    public static final String MQ_SMS_MO_RECEIVE      = "mq_sms_mo_receive";

    @Bean
    public IdGenerator idGenerator() {
        return new IdGenerator(1);
    }

    /**
     * topic模式的ListenerContainer
     * 
     * @param activeMQConnectionFactory
     * @return
     */
    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ConnectionFactory activeMQConnectionFactory) {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setPubSubDomain(true);
        bean.setConnectionFactory(activeMQConnectionFactory);
        /**
         * 使用消息转换器
         */
        bean.setMessageConverter(jacksonJmsMessageConverter());
        return bean;
    }

    /**
     * 消息转换器
     * 
     * @return
     */
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setEncoding("UTF-8");
        return converter;
    }

}
