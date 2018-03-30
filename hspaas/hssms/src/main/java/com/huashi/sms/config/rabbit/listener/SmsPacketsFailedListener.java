package com.huashi.sms.config.rabbit.listener;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.stereotype.Component;

import com.huashi.sms.config.rabbit.constant.ActiveMqConstant;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.service.ISmsMtSubmitService;

/**
 * TODO 分包失败，待处理
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年12月25日 下午6:31:38
 */
@Component
public class SmsPacketsFailedListener implements MessageListener {

    @Autowired
    private ISmsMtSubmitService             smsMtSubmitService;
    @Autowired
    private MappingJackson2MessageConverter messageConverter;

    protected Logger                        logger = LoggerFactory.getLogger(getClass());

    @Override
    @JmsListener(destination = ActiveMqConstant.MQ_SMS_MT_PACKETS_EXCEPTION)
    public void onMessage(javax.jms.Message message) {
        try {
            SmsMtMessageSubmit submit = (SmsMtMessageSubmit) messageConverter.fromMessage(message);

            List<SmsMtMessageSubmit> submits = new ArrayList<>();
            submits.add(submit);

            smsMtSubmitService.doSmsException(submits);

        } catch (Exception e) {
            try {
                logger.error("MQ消费分包失败数据异常： {}", messageConverter.fromMessage(message), e);
            } catch (MessageConversionException | JMSException e1) {
                e1.printStackTrace();
            }
        }
    }

}
