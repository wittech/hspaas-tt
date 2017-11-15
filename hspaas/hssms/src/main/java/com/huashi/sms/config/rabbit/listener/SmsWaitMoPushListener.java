package com.huashi.sms.config.rabbit.listener;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.passage.context.PassageContext.PushStatus;
import com.huashi.sms.record.domain.SmsMoMessagePush;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.task.context.MQConstant;
import com.rabbitmq.client.Channel;

/**
 * 
  * TODO 上行短信推送监听
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2016年12月1日 下午2:32:35
 */
@Component
public class SmsWaitMoPushListener extends BaseListener implements ChannelAwareMessageListener {
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private Jackson2JsonMessageConverter messageConverter;
	
	private void doTranslateHttpReport(SmsMoMessageReceive report) {
		Long startTime = System.currentTimeMillis();
		RetryResponse retryResponse = null;
		String pushContent = null;
		try {
			pushContent = JSON.toJSONString(report, new SimplePropertyPreFilter("sid", "mobile", "content", "destnationNo","receiveTime"), 
					SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
			int retryTimes = report.getRetryTimes() == 0 ? PUSH_RETRY_TIMES : report.getRetryTimes();
			
			retryResponse = post(report.getPushUrl(), pushContent, retryTimes,  1);
			
			if(logger.isDebugEnabled())
				logger.debug("处理结果：{}, 尝试次数 : {}, 异常信息: {}", retryResponse.getResult(), retryResponse.getAttemptTimes(), 
						retryResponse.getLastThrowable().getMessage());
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			doPushPersistence(report, pushContent, retryResponse, System.currentTimeMillis() - startTime);
		}
		
		
	}
	
	@Override
	@RabbitListener(queues = MQConstant.MQ_SMS_MO_WAIT_PUSH)
	public void onMessage(Message message, Channel channel) throws Exception {
		Object object = messageConverter.fromMessage(message);
		try {
			if(object == null) {
				logger.info("上行推送报告数据为空，推送解析失败");
				return;
			}
			
			SmsMoMessageReceive report = (SmsMoMessageReceive) object;
			
			// 发送数据包
			doTranslateHttpReport(report);
			
		} catch (Exception e) {
			logger.error("上行推送用户消息监听失败，消息：{}", JSON.toJSONString(object), e);
		} finally {
			// 确认消息成功消费
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		}
	}
	
	/**
	 * 
	   * TODO 推送持久化
	   * 
	   * @param report
	   * @param responseContent
	   * @param timeCost
	 */
	private void doPushPersistence(SmsMoMessageReceive report, String content, RetryResponse retryResponse, long timeCost) {
		SmsMoMessagePush push = new SmsMoMessagePush();
		push.setMsgId(report.getMsgId());
		push.setMobile(report.getMobile());
		if(retryResponse == null) {
			push.setStatus(PushStatus.FAILED.getValue());
			push.setResponseContent("回执数据为空");
			push.setRetryTimes(0);
		} else {
			push.setStatus(retryResponse.isSuccess() ? PushStatus.SUCCESS.getValue() : PushStatus.FAILED.getValue());
			push.setResponseContent(retryResponse.getResult());
			push.setRetryTimes(retryResponse.getAttemptTimes());
		}
		push.setResponseMilliseconds(timeCost);
		push.setContent(content);
		push.setCreateTime(new Date());
		stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_DB_MESSAGE_MO_PUSH_LIST, JSON.toJSONString(push));
	}

}
