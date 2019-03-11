package com.huashi.mms.config.rabbit.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.huashi.common.settings.context.SettingsContext.PushConfigStatus;
import com.huashi.common.settings.domain.ProvinceLocal;
import com.huashi.common.settings.domain.PushConfig;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.common.third.model.MobileCatagory;
import com.huashi.common.third.service.IMobileLocalService;
import com.huashi.common.user.context.UserBalanceConstant;
import com.huashi.common.user.domain.UserMmsConfig;
import com.huashi.common.user.service.IUserMmsConfigService;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode;
import com.huashi.constants.OpenApiCode.SmsPushCode;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.service.IMmsProviderService;
import com.huashi.mms.config.rabbit.AbstartRabbitListener;
import com.huashi.mms.config.rabbit.constant.RabbitConstant;
import com.huashi.mms.passage.domain.MmsPassage;
import com.huashi.mms.passage.domain.MmsPassageParameter;
import com.huashi.mms.passage.service.IMmsPassageService;
import com.huashi.mms.record.domain.MmsMtMessageSubmit;
import com.huashi.mms.record.service.IMmsMtSubmitService;
import com.huashi.mms.task.domain.MmsMtTaskPackets;
import com.huashi.mms.template.domain.MmsPassageMessageTemplate;
import com.huashi.sms.task.context.TaskContext.PacketsApproveStatus;
import com.rabbitmq.client.Channel;

/**
 * TODO 彩信待提交队列监听
 *
 * @author zhengying
 * @version V1.0
 * @date 2016年10月11日 下午1:20:14
 */
@Component
public class MmsWaitSubmitListener extends AbstartRabbitListener {

    @Resource
    private RabbitTemplate               rabbitTemplate;
    @Resource
    private StringRedisTemplate          stringRedisTemplate;

    @Reference(timeout = 1200000)
    private IMmsProviderService          mmsProviderService;
    @Reference
    private IPushConfigService           pushConfigService;
    @Reference
    private IMobileLocalService          mobileLocalService;
    @Reference
    private IUserMmsConfigService        userMmsConfigService;

    @Autowired
    private IMmsMtSubmitService          mmsSubmitService;
    @Autowired
    private IMmsPassageService           mmsPassageService;
    @Autowired
    private Jackson2JsonMessageConverter messageConverter;
    @Resource
    private ThreadPoolTaskExecutor       threadPoolTaskExecutor;

    /**
     * TODO 处理分包产生的数据，并调用上家通道接口
     *
     * @param packets 子任务
     */
    private void transport2Gateway(MmsMtTaskPackets packets) {
        if (StringUtils.isBlank(packets.getMobile())) {
            throw new RuntimeException("手机号码数据包为空，无法解析");
        }

        if (packets.getStatus() == PacketsApproveStatus.WAITING.getCode()
            || packets.getStatus() == PacketsApproveStatus.REJECT.getCode()) {
            logger.info("子任务状态为待处理或驳回，不处理");
            return;
        }

        // 查询推送地址信息
        PushConfig pushConfig = pushConfigService.getPushUrl(packets.getUserId(),
                                                             PlatformType.MULTIMEDIA_MESSAGE_SERVICE.getCode(),
                                                             packets.getCallback());

        try {
            // 组装最终发送短信的扩展号码
            String extNumber = getUserExtNumber(packets.getUserId(), packets.getTemplateExtNumber(),
                                                packets.getExtNumber(), packets.getContent());

            // 获取通道信息
            MmsPassage mmsPassage = mmsPassageService.findById(packets.getFinalPassageId());

            // 重新调整扩展号码
            extNumber = resizeExtNumber(extNumber, mmsPassage);

            // 根据网关分包数要求对手机号码进行拆分，分批提交
            List<String> groupMobiles = regroupMobileByPacketsSize(packets.getMobile(), mmsPassage);

            for (String groupMobile : groupMobiles) {
                if (StringUtils.isEmpty(groupMobile)) {
                    continue;
                }

                // 调用网关通道处理器，提交短信信息，并接收回执
                List<ProviderSendResponse> responses = mmsProviderService.sendMms(getPassageParameter(packets,
                                                                                                          smsPassage),
                                                                                      groupMobile,
                                                                                      packets.getContent(),
                                                                                      packets.getSingleFee(), extNumber);

                // ProviderSendResponse response = list.iterator().next();
                List<MmsMtMessageSubmit> list = makeSubmitReport(packets, groupMobile, responses, extNumber, pushConfig);
                if (CollectionUtils.isEmpty(list)) {
                    logger.error("解析上家回执数据逻辑数据为空，伪造包逻辑处理");
                    continue;
                }

                persistSubmitMessage(list);
            }

        } catch (Exception e) {
            logger.error("调用上家通道失败", e);
            sendMqueueIfFailed(packets, packets.getMobile(), pushConfig);
        }
    }

    /**
     * TODO 获取用户的拓展号码
     *
     * @param userId
     * @param templateExtNumber 短信模板扩展号码
     * @param extNumber 用户自定义扩展号码
     * @return
     */
    private String getUserExtNumber(Integer userId, String templateExtNumber, String extNumber, String content) {
        // 如果短信模板扩展名不为空，则按照此扩展号码为主（忽略用户短信配置的扩展号码）
        if (StringUtils.isNotEmpty(templateExtNumber)) {
            return templateExtNumber + (StringUtils.isEmpty(extNumber) ? "" : extNumber);
        }

        if (userId == null) {
            return extNumber;
        }

        UserMmsConfig userMmsConfig = userMmsConfigService.getByUserId(userId);
        if (userMmsConfig == null) {
            return extNumber;
        }

        if (StringUtils.isEmpty(userMmsConfig.getExtNumber())) {
            return extNumber;
        }

        return userMmsConfig.getExtNumber() + (StringUtils.isEmpty(extNumber) ? "" : extNumber);
    }

    /**
     * TODO 截取超出通道扩展号最大长度的位数
     *
     * @param extNumber 扩展号码
     * @param mmsPassage 通道信息
     */
    private String resizeExtNumber(String extNumber, MmsPassage mmsPassage) {
        if (StringUtils.isEmpty(extNumber)) {
            return extNumber;
        }

        // 如果扩展号码
        if (mmsPassage == null || PASSAGE_EXT_NUMBER_LENGTH_ENDLESS == mmsPassage.getExtNumber()) {
            return extNumber;
        } else if (PASSAGE_EXT_NUMBER_LENGTH_NOT_ALLOWED == mmsPassage.getExtNumber()) {
            return "";
        } else {
            // add by zhengying 2017-2-50
            // 如果当前扩展号码总长度小于扩展号长度上限则在直接返回，否则按照扩展号上限截取
            return extNumber.length() < mmsPassage.getExtNumber() ? extNumber : extNumber.substring(0,
                                                                                                    mmsPassage.getExtNumber());
        }
    }

    /**
     * TODO 转换获取通道参数信息
     *
     * @param packets 分包信息
     * @param smsPassage 通道信息
     * @return
     */
    private MmsPassageParameter getPassageParameter(MmsMtTaskPackets packets, MmsPassage mmsPassage) {
        MmsPassageParameter parameter = new MmsPassageParameter();
        parameter.setProtocol(packets.getPassageProtocol());
        parameter.setParams(packets.getPassageParameter());
        parameter.setUrl(packets.getPassageUrl());
        parameter.setSuccessCode(packets.getSuccessCode());
        parameter.setResultFormat(packets.getResultFormat());
        parameter.setPosition(packets.getPosition());
        parameter.setPassageId(packets.getFinalPassageId());
        parameter.setPacketsSize(packets.getPassageSpeed());

        if (mmsPassage == null) {
            return parameter;
        }

        // add by 20170831 加入最大连接数和连接超时时间限制（目前主要用于HTTP请求）
        parameter.setConnectionSize(mmsPassage.getConnectionSize());
        parameter.setReadTimeout(mmsPassage.getReadTimeout());

        // 根据短信内容查询通道短信模板参数
        MmsPassageMessageTemplate mmsPassageMessageTemplate = mmsPassageMessageTemplateService.getByMessageContent(smsPassage.getId(),
                                                                                                                   packets.getContent());
        if (smsPassageMessageTemplate == null) {
            logger.warn("通道：{} 短信模板参数信息匹配为空", smsPassage.getId());
            return parameter;
        }

        // 针对通道方指定模板ID及模板内变量名称数据模式设置参数
        parameter.setSmsTemplateId(smsPassageMessageTemplate.getTemplateId());
        parameter.setVariableParamNames(smsPassageMessageTemplate.getParamNames().split(","));

        // 根据表达式和参数数量获取本次具体的变量值
        parameter.setVariableParamValues(MmsPassageMessageTemplateService.pickupValuesByRegex(packets.getContent(),
                                                                                              smsPassageMessageTemplate.getRegexValue(),
                                                                                              smsPassageMessageTemplate.getParamNames().split(",").length));

        return parameter;
    }

    /**
     * TODO 处理提交完成逻辑
     *
     * @param submits
     */
    private void persistSubmitMessage(List<MmsMtMessageSubmit> submits) {
        try {
            mmsSubmitService.batchInsertSubmit(submits);

            // 判断并设置推送信息
            mmsSubmitService.setPushConfigurationIfNecessary(submits);

        } catch (Exception e) {
            logger.error("处理待提交信息REDIS失败，失败信息：{}", JSON.toJSONString(submits), e);
        }
    }

    /**
     * TODO 组装提交完成的短息信息入库
     * 
     * @param packets
     * @param mobileStr 以逗号分隔的手机号码字符串（可能多个手机号码，也可能单个）
     * @param responses
     * @param extNumber 扩展号码
     * @param pushConfig
     * @return
     */
    private List<MmsMtMessageSubmit> makeSubmitReport(MmsMtTaskPackets packets, String mobileStr,
                                                      List<ProviderSendResponse> responses, String extNumber,
                                                      PushConfig pushConfig) {
        String[] mobiles = mobileStr.split(",");
        if (mobiles.length == 0) {
            return null;
        }

        MmsMtMessageSubmit submitTemplate = makeMessageSubmitTemplate(packets, pushConfig, extNumber);

        List<SmsMtMessageSubmit> submits = new ArrayList<>();

        // 批量获取手机号码省份归属地
        Map<String, ProvinceLocal> mobileProvinceLocals = mobileLocalService.getByMobiles(mobiles);

        for (String mobile : mobiles) {

            SmsMtMessageSubmit submit = new SmsMtMessageSubmit();

            BeanUtils.copyProperties(submitTemplate, submit);
            submit.setMobile(mobile);
            submit.setCmcp(mobileProvinceLocals.get(mobile).getCmcp());
            submit.setProvinceCode(mobileProvinceLocals.get(mobile).getProvinceCode());

            fillSubmitFromResponse(submit, responses, packets.getSid());

            // 如果提交数据失败，则需要制造伪造包补推送
            if (MessageSubmitStatus.FAILED.getCode() == submit.getStatus()) {
                rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_MMS, RabbitConstant.MQ_MMS_MT_PACKETS_EXCEPTION,
                                              submit);
                continue;
            }

            submits.add(submit);
        }

        return submits;
    }

    /**
     * TODO 生成短消息提交模板记录
     * 
     * @param packets
     * @param pushConfig
     * @param extNumber
     * @return
     */
    private MmsMtMessageSubmit makeMessageSubmitTemplate(MmsMtTaskPackets packets, PushConfig pushConfig,
                                                         String extNumber) {
        MmsMtMessageSubmit submitTemplate = new MmsMtMessageSubmit();

        // 排除子任务中的通道ID（要以最终通道为准 : finalPassageId）
        // mobile子任务中是以逗号隔开的多个手机号码，submit需要分开赋值
        BeanUtils.copyProperties(packets, submitTemplate, "passageId", "mobile");
        submitTemplate.setPassageId(packets.getFinalPassageId());

        // 推送信息为固定地址或者每次传递地址才需要推送
        if (pushConfig != null && pushConfig.getStatus() != PushConfigStatus.NO.getCode()) {
            submitTemplate.setPushUrl(pushConfig.getUrl());
            submitTemplate.setNeedPush(true);
        }

        submitTemplate.setCreateTime(new Date());
        submitTemplate.setCreateUnixtime(submitTemplate.getCreateTime().getTime());
        submitTemplate.setDestnationNo(extNumber);

        return submitTemplate;
    }

    /**
     * TODO 根据回执信息填充submit
     * 
     * @param submit
     * @param responses
     * @param sid
     */
    private void fillSubmitFromResponse(SmsMtMessageSubmit submit, List<ProviderSendResponse> responses, Long sid) {
        // 回执数据可能为空（直连协议常见）
        if (CollectionUtils.isEmpty(responses)) {
            submit.setStatus(MessageSubmitStatus.FAILED.getCode());
            submit.setPushErrorCode(SmsPushCode.SMS_SUBMIT_PASSAGE_FAILED.getCode());
            submit.setRemark(SmsPushCode.SMS_SUBMIT_PASSAGE_FAILED.getCode());
            submit.setMsgId(sid + "");
            return;
        }

        int effect = 0;
        for (ProviderSendResponse response : responses) {
            // 回执手机号码如果为空，则表明网关回执不携带手机号码直接赋值状态相关
            if (StringUtils.isNotEmpty(response.getMobile()) && !submit.getMobile().equals(response.getMobile())) {
                continue;
            }

            submit.setStatus(response.isSuccess() ? MessageSubmitStatus.SUCCESS.getCode() : MessageSubmitStatus.FAILED.getCode());
            // 如果通道发送失败，则设置伪造包状态码S0013 add by 20180908
            if (!response.isSuccess()) {
                submit.setPushErrorCode(OpenApiCode.SmsPushCode.SMS_PASSAGE_AUTH_NOT_MATCHED.getCode());
            }

            submit.setRemark(response.getRemark());
            submit.setMsgId(StringUtils.isNotEmpty(response.getSid()) ? response.getSid() : sid + "");

            effect++;
        }

        // 如果最终一条都未匹配上，则任务调用错误，理论上不会发生，除非上家通道提交回执错乱
        if (effect == 0) {
            submit.setStatus(MessageSubmitStatus.FAILED.getCode());
            submit.setPushErrorCode(SmsPushCode.SMS_SUBMIT_PASSAGE_FAILED.getCode());
            submit.setRemark(SmsPushCode.SMS_SUBMIT_PASSAGE_FAILED.getCode());
            submit.setMsgId(sid + "");
        }
    }

    /**
     * TODO 提交短信至上家通道（发送网关错误，组装伪造包S0099）
     *
     * @param model
     * @param mobileReport
     * @param pushConfig
     */
    private void sendMqueueIfFailed(SmsMtTaskPackets packets, String mobileReport, PushConfig pushConfig) {

        SmsMtMessageSubmit submitTemplate = makeMessageSubmitTemplate(packets, pushConfig, null);

        submitTemplate.setStatus(MessageSubmitStatus.FAILED.getCode());
        submitTemplate.setRemark(SmsPushCode.SMS_SUBMIT_PASSAGE_FAILED.getCode());
        submitTemplate.setMsgId(packets.getSid() + "");

        String[] mobiles = mobileReport.split(",");
        for (String mobile : mobiles) {
            SmsMtMessageSubmit submit = new SmsMtMessageSubmit();
            BeanUtils.copyProperties(submitTemplate, submit);
            submit.setCmcp(CMCP.local(mobile).getCode());
            submit.setMobile(mobile);

            rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_MMS, RabbitConstant.MQ_MMS_MT_PACKETS_EXCEPTION,
                                          submit);
        }
    }

    /**
     * TODO 待提交短信处理
     *
     * @param message
     * @param channel
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            Object object = messageConverter.fromMessage(message);

            // 针对 人工审核处理，重新入队列，入队列数据为子包
            if (object instanceof SmsMtTaskPackets) {
                transport2Gateway((SmsMtTaskPackets) object);
            } else {
                transport2Gateway((SmsMtTask) object);
            }

        } catch (Exception e) {
            logger.error("MQ消费提交网关数据失败： {}", messageConverter.fromMessage(message), e);
            // channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        } finally {
            // 确认消息成功消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    /**
     * TODO 主任务（多个子任务）发送网关
     *
     * @param task
     */
    private void transport2Gateway(SmsMtTask task) {
        if (task == null) {
            logger.error("待提交队列解析失败，主任务为空");
            return;
        }

        List<SmsMtTaskPackets> list = task.getPackets();
        for (SmsMtTaskPackets packet : list) {
            if (packet.getStatus() == PacketsApproveStatus.WAITING.getCode()
                || packet.getStatus() == PacketsApproveStatus.REJECT.getCode()) {
                logger.info("数据包待处理，无需本次分包处理");
                continue;
            }

            if (StringUtils.isEmpty(packet.getMobile())) {
                logger.warn("待提交队列处理异常：手机号码为空， 跳出 {}", JSON.toJSONString(packet));
                continue;
            }

            transport2Gateway(packet);
        }
    }

    /**
     * TODO 根据通道分包数重组手机号码
     * 
     * @param mobile
     * @param mmsPassage
     * @return
     */
    private static List<String> regroupMobileByPacketsSize(String mobile, MmsPassage mmsPassage) {
        if (StringUtils.isBlank(mobile)) {
            throw new RuntimeException("提交任务错误：手机号码为空");
        }

        String[] mobiles = mobile.split(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
        if (mobiles.length == 1) {
            return Arrays.asList(mobiles);
        }

        // 如果通道为空或者分包手机号码未配置按照 分包队列的默认数直接提交（默认4000）
        if (mmsPassage == null || mmsPassage.getMobileSize() == null || mmsPassage.getMobileSize() == 0
            || mmsPassage.getMobileSize() == DEFAULT_REQUEST_MOBILE_PACKAGE_SIZE) {
            return regroupMobiles(mobiles, mobiles.length);
        }

        return regroupMobiles(mobiles, mmsPassage.getMobileSize());
    }

}