package com.huashi.listener.prervice;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import org.apache.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.constant.ParameterFilterContext;
import com.huashi.exchanger.service.IMmsProviderService;
import com.huashi.listener.constant.RabbitConstant;
import com.huashi.mms.passage.service.IMmsPassageAccessService;
import com.huashi.mms.record.service.IMmsMoMessageService;
import com.huashi.mms.record.service.IMmsMtDeliverService;

@Service
public class MmsPassagePrervice {

    @Resource(name = "mmsRabbitTemplate")
    private RabbitTemplate           mmsRabbitTemplate;
    @Reference
    private IMmsProviderService      mmsProviderService;
    @Reference
    private IMmsMtDeliverService     mmsMtDeliverService;
    @Reference
    private IMmsMoMessageService     mmsMoMessageService;
    @Reference
    private IMmsPassageAccessService mmsPassageAccessService;

    private final Logger             logger = LoggerFactory.getLogger(MmsPassagePrervice.class);

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

        logger.info("彩信通道简码：{}，彩信状态报告数据: {}", provider, jsonObject.toJSONString());

        jsonObject.put(ParameterFilterContext.PASSAGE_PROVIDER_CODE_NODE, provider);

        // 发送异步消息
        mmsRabbitTemplate.convertAndSend(RabbitConstant.MQ_MMS_MT_WAIT_RECEIPT, jsonObject);
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

        logger.info("彩信通道简码：{}，彩信上行报告数据: {}", provider, jsonObject.get(ParameterFilterContext.PARAMETER_NAME_IN_STREAM));

        jsonObject.put(ParameterFilterContext.PASSAGE_PROVIDER_CODE_NODE, provider);

        // 发送异步消息
        mmsRabbitTemplate.convertAndSend(RabbitConstant.MQ_MMS_MO_RECEIVE, jsonObject);
    }

    public void doTemplateModelReport(String provider, JSONObject jsonObject) {
        if (StringUtils.isEmpty(provider) || jsonObject == null) {
            logger.warn("回执数据无效: [Provider: {}, ParametersMap : {}]", provider, jsonObject.toJSONString());
            return;
        }

        logger.info("彩信通道简码：{}，彩信模板数据数据: {}", provider, jsonObject.get(ParameterFilterContext.PARAMETER_NAME_IN_STREAM));

        jsonObject.put(ParameterFilterContext.PASSAGE_PROVIDER_CODE_NODE, provider);

        // 发送异步消息
        mmsRabbitTemplate.convertAndSend(RabbitConstant.MQ_MMS_MODEL_RECEIVE, jsonObject);
    }

}
