package com.huashi.sms.config.rabbit.listener;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.sms.task.context.MQConstant;
import com.rabbitmq.client.Channel;

/**
 * 
  * TODO 分包失败，待处理
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2016年12月25日 下午6:31:38
 */
@Component
public class SmsPacketsFailedListener implements ChannelAwareMessageListener {

	@Autowired
	private ISmsMtSubmitService smsMtSubmitService;
	@Autowired
	private Jackson2JsonMessageConverter messageConverter;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	@RabbitListener(queues = MQConstant.MQ_SMS_MT_PACKETS_EXCEPTION)
	public void onMessage(Message message, Channel channel) throws Exception {
		try {
			SmsMtMessageSubmit submit = (SmsMtMessageSubmit) messageConverter.fromMessage(message);
			
			List<SmsMtMessageSubmit> submits = new ArrayList<>();
			submits.add(submit);
			
			smsMtSubmitService.doSmsException(submits);

		} catch (Exception e) {
			logger.error("MQ消费分包失败数据异常： {}", messageConverter.fromMessage(message), e);
		} finally {
			// 确认消息成功消费
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		}
	}

}
