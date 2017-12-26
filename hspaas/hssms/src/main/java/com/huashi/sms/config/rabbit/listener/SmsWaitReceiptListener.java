package com.huashi.sms.config.rabbit.listener;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.exchanger.constant.ParameterFilterContext;
import com.huashi.exchanger.service.ISmsProviderService;
import com.huashi.sms.config.rabbit.constant.RabbitConstant;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.passage.service.ISmsPassageAccessService;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.service.ISmsMtDeliverService;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.rabbitmq.client.Channel;

/**
 * 
 * TODO 短信下行状态报告回执处理
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年11月24日 下午11:04:22
 */
@Component
public class SmsWaitReceiptListener implements ChannelAwareMessageListener {

	@Autowired
	private ISmsMtSubmitService smsMtSubmitService;
	@Reference
	private ISmsProviderService smsProviderService;
	@Autowired
	private ISmsMtDeliverService smsMtDeliverService;
	@Autowired
	private Jackson2JsonMessageConverter messageConverter;
	@Autowired
	private ISmsPassageAccessService smsPassageAccessService;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	@RabbitListener(queues = RabbitConstant.MQ_SMS_MT_WAIT_RECEIPT)
	public void onMessage(Message message, Channel channel) throws Exception {
		try {
			JSONObject object =(JSONObject) messageConverter.fromMessage(message);
			// 处理待提交队列逻辑
			if (object == null) {
				logger.warn("状态回执报告解析失败：回执数据为空");
				return;
			}
			
			List<SmsMtMessageDeliver> delivers = doDeliverMessage(object);
			
			if(CollectionUtils.isEmpty(delivers)) {
				logger.error("状态回执报告解析为空 : {}", messageConverter.fromMessage(message));
				return;
			}

			// 处理回执完成后的持久操作
			smsMtDeliverService.doFinishDeliver(delivers);

		} catch (Exception e) {
			// 需要做重试判断
			logger.error("MQ消费网关状态回执数据失败： {}", messageConverter.fromMessage(message), e);
			if (message != null) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("reason", "上家回执状态报告失败:" + e.getMessage());
				jsonObject.put("message", messageConverter.fromMessage(message));
				smsMtDeliverService.doDeliverToException(jsonObject);
			}
//			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
		} finally {
			// 确认消息成功消费
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		}
	}
	
	/**
	 * 
	   * TODO 状态报告推送处理逻辑
	   * @param jsonObject
	   * @return
	 */
	private List<SmsMtMessageDeliver> doDeliverMessage(JSONObject jsonObject) {
		// 提供商代码（通道）
		String providerCode = jsonObject.getString(ParameterFilterContext.PASSAGE_PROVIDER_CODE_NODE);
		if (StringUtils.isEmpty(providerCode)) {
			logger.warn("上家推送状态回执报告解析失败：回执码为空");
			jsonObject.put("reason", "上家推送状态回执报告解析失败：回执码为空");
			smsMtDeliverService.doDeliverToException(jsonObject);
			return null;
		}

		SmsPassageAccess access = smsPassageAccessService.getByType(PassageCallType.STATUS_RECEIPT_WITH_PUSH, providerCode);
		if (access == null) {
			logger.warn("上家推送状态回执报告通道参数无法匹配：{}", jsonObject.toJSONString());
			jsonObject.put("reason", "上家推送状态回执报告通道参数无法匹配");
			smsMtDeliverService.doDeliverToException(jsonObject);
			return null;
		}

		// 回执数据解析后的报文
		List<SmsMtMessageDeliver> delivers = smsProviderService.doStatusReport(access, jsonObject);
		if (CollectionUtils.isEmpty(delivers)) {
            return null;
        }
		
		fillMobileWhenMobileMissed(delivers);
		
		return delivers;
	}
	
	/**
	 * 
	   * TODO 如果报文回执中手机号码为空，则需要根据MSG_ID去数据库查询提交数据中对应的手机号码填充
	   * PS：此处可能存在上家报文回执过快，我方未入库情况，这时候需要异常处理
	   * @param delivers
	 */
	private void fillMobileWhenMobileMissed(List<SmsMtMessageDeliver> delivers) {
		SmsMtMessageSubmit submit = null;
		for(SmsMtMessageDeliver deliver : delivers) {
			if(StringUtils.isNotBlank(deliver.getMobile())) {
                continue;
            }
			
			submit = smsMtSubmitService.getByMsgid(deliver.getMsgId());
			deliver.setMobile(submit.getMobile());
			deliver.setCmcp(CMCP.local(submit.getMobile()).getCode());
		}
	}
}
