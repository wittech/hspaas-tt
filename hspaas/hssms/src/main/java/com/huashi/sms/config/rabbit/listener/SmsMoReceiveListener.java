package com.huashi.sms.config.rabbit.listener;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.exchanger.constant.ParameterFilterContext;
import com.huashi.exchanger.service.ISmsProviderService;
import com.huashi.sms.config.rabbit.constant.ActiveMqConstant;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.passage.service.ISmsPassageAccessService;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.service.ISmsMoMessageService;

/**
 * 
 * TODO 短信上行监听
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月21日 下午2:13:56
 */
@Component
public class SmsMoReceiveListener implements MessageListener {

	@Reference
	private ISmsProviderService smsProviderService;
	@Autowired
	private MappingJackson2MessageConverter messageConverter;
	@Autowired
	private ISmsMoMessageService smsMoMessageService;
	@Autowired
	private ISmsPassageAccessService smsPassageAccessService;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	@JmsListener(destination = ActiveMqConstant.MQ_SMS_MO_RECEIVE)
	public void onMessage(Message message) {
		try {
		    
			Object object = messageConverter.fromMessage(message);
			// 处理待提交队列逻辑
			if (object == null) {
				logger.warn("上行报告解析失败：回执数据为空");
				return;
			}
			

			List<SmsMoMessageReceive> receives;
			if (object instanceof JSONObject) {
				receives = doReceiveMessage((JSONObject) object);
			} else if (object instanceof List) {
			    ObjectMapper mapper = new ObjectMapper();
			    
				receives = mapper.convertValue(object,new TypeReference<List<SmsMoMessageReceive>>() {});
			} else {
				logger.error("上行回执数据类型无法匹配");
				return;
			}

			if (CollectionUtils.isEmpty(receives)) {
				logger.error("上行报告解析为空");
				return;
			}

			// 处理回执完成后的持久操作
			smsMoMessageService.doFinishReceive(receives);

		} catch (Exception e) {
			try {
			    smsMoMessageService.doReceiveToException(message);
                logger.error("MQ消费网关上行数据失败： {}", messageConverter.fromMessage(message), e);
            } catch (MessageConversionException | JMSException e1) {
                e1.printStackTrace();
            }
			
		}
	}

	/**
	 * 
	 * TODO 上行报告推送处理逻辑
	 * 
	 * @param jsonObject
	 * @return
	 */
	private List<SmsMoMessageReceive> doReceiveMessage(JSONObject jsonObject) {
		// 提供商代码（通道）
		String providerCode = jsonObject
				.getString(ParameterFilterContext.PASSAGE_PROVIDER_CODE_NODE);
		if (StringUtils.isEmpty(providerCode)) {
			logger.warn("上行报告解析失败：回执码为空");
			jsonObject.put("reason", "上行报告解析失败：回执码为空");
			smsMoMessageService.doReceiveToException(jsonObject);
			return null;
		}

		SmsPassageAccess access = smsPassageAccessService.getByType(
				PassageCallType.SMS_MO_REPORT_WITH_PUSH, providerCode);
		if (access == null) {
			logger.warn("上行报告通道参数无法匹配：{}", jsonObject.toJSONString());
			jsonObject.put("reason", "状态回执报告通道参数无法匹配");
			smsMoMessageService.doReceiveToException(jsonObject);
			return null;
		}

		// 回执数据解析后的报文
		List<SmsMoMessageReceive> receives = smsProviderService.doMoReport(
				access, jsonObject);
		if (CollectionUtils.isEmpty(receives)) {
			return null;
		}

		return receives;
	}

}
