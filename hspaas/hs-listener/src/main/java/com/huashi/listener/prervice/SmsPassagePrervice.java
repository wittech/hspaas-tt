package com.huashi.listener.prervice;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.constant.ParameterFilterContext;
import com.huashi.exchanger.service.ISmsProviderService;
import com.huashi.listener.constant.RabbitConstant;
import com.huashi.sms.passage.service.ISmsPassageAccessService;
import com.huashi.sms.record.service.ISmsMoMessageService;
import com.huashi.sms.record.service.ISmsMtDeliverService;

@Service
public class SmsPassagePrervice {

    @Resource
    private RabbitTemplate           rabbitTemplate;
    @Reference
    private ISmsProviderService      smsProviderService;
    @Reference
    private ISmsMtDeliverService     smsMtDeliverService;
    @Reference
    private ISmsMoMessageService     smsMoMessageService;
    @Reference
    private ISmsPassageAccessService smsPassageAccessService;

    private Logger                   logger = LoggerFactory.getLogger(SmsPassagePrervice.class);

    /**
     * TODO 根据通道优先级
     */
    public void doPickupUserPassage() {
        // 查找所有用户信息

        // 根据用户信息找到相关通道组

        // 根据通道组的路由类型，找到相关三网的通道信息

        // 三网内单独判断通道（结合通道状态和优先级），如果 用户不包含某个运营商的通道，则以全网通道 顶替

        // *******************************异常处理

        // 针对通道故障，则由通道组内其他备用通道更新进来，如果没有备用通道，则直接告警（所有数据流向最终大的备用通道，保证数据正常发送）

        /****************************************************/
        // 用户ID 通道组ID 通道ID 路由类型 运营商

        // 1.修改用户通道组信息
        // 2.修改通道组
        // 3.修改通道
        // 4.轮询通道错误修改
    }

    /**
     * TODO 处理通道下行状态回执信息
     * 
     * @param provider
     * @param jsonObject
     */
    public void doPassageStatusReport(String provider, JSONObject jsonObject) {
        if (StringUtils.isEmpty(provider) || jsonObject == null) {
            logger.warn("回执数据无效: [Provider: {}, ParametersMap : {}]", provider, jsonObject.toJSONString());
            return;
        }

        logger.info("通道简码：{}，短信状态报告数据: {}", provider, jsonObject.toJSONString());

        jsonObject.put(ParameterFilterContext.PASSAGE_PROVIDER_CODE_NODE, provider);

        // 发送异步消息
        rabbitTemplate.convertAndSend(RabbitConstant.MQ_SMS_MT_WAIT_RECEIPT, jsonObject);
    }

    /**
     * TODO 处理通道上行回复信息
     * 
     * @param provider
     * @param jsonObject
     */
    public void doPassageMoReport(String provider, JSONObject jsonObject) {
        if (StringUtils.isEmpty(provider) || jsonObject == null) {
            logger.warn("回执数据无效: [Provider: {}, ParametersMap : {}]", provider, jsonObject.toJSONString());
            return;
        }

        logger.info("通道简码：{}，短信上行报告数据: {}", provider, jsonObject.get(ParameterFilterContext.PARAMETER_NAME_IN_STREAM));

        jsonObject.put(ParameterFilterContext.PASSAGE_PROVIDER_CODE_NODE, provider);

        // 发送异步消息
        rabbitTemplate.convertAndSend(RabbitConstant.MQ_SMS_MO_RECEIVE, jsonObject);
    }

}
