package com.huashi.listener.prervice;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.exchanger.constant.ParameterFilterContext;
import com.huashi.exchanger.service.IMmsProviderService;
import com.huashi.listener.constant.RabbitConstant;
import com.huashi.listener.task.mms.MmsPassageMoReportPullTask;
import com.huashi.listener.task.mms.MmsPassageStatusReportPullTask;
import com.huashi.mms.passage.domain.MmsPassageAccess;
import com.huashi.mms.passage.service.IMmsPassageAccessService;
import com.huashi.mms.record.service.IMmsMoMessageService;
import com.huashi.mms.record.service.IMmsMtDeliverService;

@Service
public class MmsPassagePrervice {

    @Resource
    private RabbitTemplate           rabbitTemplate;
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

        logger.info("通道简码：{}，短信状态报告数据: {}", provider, jsonObject.toJSONString());

        jsonObject.put(ParameterFilterContext.PASSAGE_PROVIDER_CODE_NODE, provider);

        // 发送异步消息
        rabbitTemplate.convertAndSend(RabbitConstant.MQ_MMS_MT_WAIT_RECEIPT, jsonObject);
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
        rabbitTemplate.convertAndSend(RabbitConstant.MQ_MMS_MO_RECEIVE, jsonObject);
    }

    /**
     * TODO 通道下行状态扫描
     */
    public void doPassageStatusPulling() {
        List<MmsPassageAccess> list = mmsPassageAccessService.findWaitPulling(PassageCallType.MT_STATUS_RECEIPT_WITH_SELF_GET);
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("未检索到通道下行状态报告回执");
            return;
        }

        Thread thread = null;
        for (MmsPassageAccess access : list) {
            thread = new Thread(new MmsPassageStatusReportPullTask(access, mmsMtDeliverService, mmsProviderService));
            thread.start();
            logger.info("通道状态回执URL：{}，类型：{} 已监听", access.getUrl(), access.getCallType());
        }
    }

    /**
     * TODO 通道上行回执数据扫描
     */
    public void doPassageMoPulling() {
        List<MmsPassageAccess> list = mmsPassageAccessService.findWaitPulling(PassageCallType.MO_REPORT_WITH_SELF_GET);
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("未检索到通道上行报告回执");
            return;
        }

        Thread thread = null;
        for (MmsPassageAccess access : list) {
            thread = new Thread(new MmsPassageMoReportPullTask(access, mmsMoMessageService, mmsProviderService));
            thread.start();
            logger.info("通道上行回执URL：{}，类型：{} 已监听", access.getUrl(), access.getCallType());
        }
    }

}
