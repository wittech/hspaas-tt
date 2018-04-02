package com.huashi.sms.config.rabbit;

import javax.jms.ConnectionFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.huashi.common.util.IdGenerator;

@Configuration
@EnableJms
public class ActivemqConfiguration {

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
    public DefaultJmsListenerContainerFactory jmsListenerContainerTopic(ConnectionFactory activeMQConnectionFactory) {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setPubSubDomain(true);
        bean.setConnectionFactory(activeMQConnectionFactory);
        
        bean.setConcurrency("10-50");
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
