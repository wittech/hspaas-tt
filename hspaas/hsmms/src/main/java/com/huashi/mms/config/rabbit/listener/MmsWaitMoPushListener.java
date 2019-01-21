package com.huashi.mms.config.rabbit.listener;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.huashi.mms.config.rabbit.AbstartRabbitListener;
import com.huashi.mms.config.rabbit.constant.RabbitConstant;
import com.huashi.mms.record.dao.MmsMoMessagePushMapper;
import com.huashi.mms.record.domain.MmsMoMessagePush;
import com.huashi.mms.record.domain.MmsMoMessageReceive;
import com.huashi.sms.passage.context.PassageContext.PushStatus;
import com.huashi.util.HttpClientUtil;
import com.huashi.util.HttpClientUtil.RetryResponse;
import com.rabbitmq.client.Channel;

/**
 * TODO 上行短信推送监听
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月1日 下午2:32:35
 */
@Component
public class MmsWaitMoPushListener extends AbstartRabbitListener {

    @Resource
    private StringRedisTemplate          stringRedisTemplate;
    @Autowired
    private Jackson2JsonMessageConverter messageConverter;
    @Autowired
    private MmsMoMessagePushMapper       mmsMoMessagePushMapper;

    /**
     * TODO 上行推送数据
     * 
     * @param report
     */
    private void doTranslateHttpReport(MmsMoMessageReceive report) {
        Long startTime = System.currentTimeMillis();
        RetryResponse retryResponse = null;
        String pushContent = null;
        try {
            pushContent = JSON.toJSONString(report, new SimplePropertyPreFilter("sid", "mobile", "content",
                                                                                "destnationNo", "receiveTime"),
                                            SerializerFeature.WriteMapNullValue,
                                            SerializerFeature.WriteNullStringAsEmpty);

            retryResponse = HttpClientUtil.postBody(report.getPushUrl(), pushContent, 1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            doPushPersistence(report, pushContent, retryResponse, System.currentTimeMillis() - startTime);
        }
    }

    @Override
    @RabbitListener(queues = RabbitConstant.MQ_MMS_MO_WAIT_PUSH)
    public void onMessage(Message message, Channel channel) throws Exception {

        Object object = messageConverter.fromMessage(message);
        try {
            if (object == null) {
                logger.info("上行推送报告数据为空，推送解析失败");
                return;
            }

            MmsMoMessageReceive report = (MmsMoMessageReceive) object;

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
     * 推送持久化
     * 
     * @param report
     * @param content
     * @param retryResponse
     * @param timeCost
     */
    private void doPushPersistence(MmsMoMessageReceive report, String content, RetryResponse retryResponse,
                                   long timeCost) {
        MmsMoMessagePush push = new MmsMoMessagePush();
        push.setMsgId(report.getMsgId());
        push.setMobile(report.getMobile());
        if (retryResponse == null) {
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

        // 插入DB
        mmsMoMessagePushMapper.insert(push);
    }
}
