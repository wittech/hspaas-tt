package com.huashi.sms.config.rabbit.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.huashi.common.settings.context.SettingsContext.PushConfigStatus;
import com.huashi.common.settings.domain.ProvinceLocal;
import com.huashi.common.settings.domain.PushConfig;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.common.third.service.IMobileLocalService;
import com.huashi.common.user.context.UserBalanceConstant;
import com.huashi.common.user.domain.UserSmsConfig;
import com.huashi.common.user.service.IUserSmsConfigService;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode.SmsPushCode;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.service.ISmsProviderService;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.rabbit.constant.RabbitConstant;
import com.huashi.sms.passage.context.PassageContext.PassageSignMode;
import com.huashi.sms.passage.context.PassageContext.PassageSmsTemplateParam;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageMessageTemplate;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.passage.service.ISmsPassageMessageTemplateService;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.huashi.sms.passage.service.SmsPassageMessageTemplateService;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.service.ISmsMtPushService;
import com.huashi.sms.signature.service.ISignatureExtNoService;
import com.huashi.sms.task.context.TaskContext.MessageSubmitStatus;
import com.huashi.sms.task.context.TaskContext.PacketsApproveStatus;
import com.huashi.sms.task.domain.SmsMtTask;
import com.huashi.sms.task.domain.SmsMtTaskPackets;
import com.rabbitmq.client.Channel;

/**
 * TODO 短信待提交队列监听
 *
 * @author zhengying
 * @version V1.0
 * @date 2016年10月11日 下午1:20:14
 */
@Component
public class SmsWaitSubmitListener implements ChannelAwareMessageListener {

    @Reference(timeout = 1200000)
    private ISmsProviderService smsProviderService;
    @Reference
    private IPushConfigService pushConfigService;
    @Autowired
    private ISmsMtPushService smsMtPushService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Jackson2JsonMessageConverter messageConverter;
    @Reference
    private IUserSmsConfigService userSmsConfigService;
    @Autowired
    private ISmsPassageService smsPassageService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ISignatureExtNoService signatureExtNoService;
    @Autowired
    private ISmsPassageMessageTemplateService smsPassageMessageTemplateService;
    @Reference
    private IMobileLocalService mobileLocalService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * TODO 处理分包产生的数据，并调用上家通道接口
     *
     * @param model
     */
    private void doTransport(SmsMtTaskPackets model) {
        if (StringUtils.isEmpty(model.getMobile())) {
            throw new RuntimeException("手机号码数据包为空，无法解析");
        }

        if (model.getStatus() == PacketsApproveStatus.WAITING.getCode() || model.getStatus() == PacketsApproveStatus.REJECT.getCode()) {
            logger.info("子任务状态为待处理或驳回，不处理");
            return;
        }

        try {
            // 组装最终发送短信的扩展号码
            String extNumber = getUserExtNumber(model.getUserId(), model.getTemplateExtNumber(), model.getExtNumber(), model.getContent());

            // 获取通道信息
            SmsPassage smsPassage = smsPassageService.findById(model.getFinalPassageId());

            // 重新调整扩展号码
            extNumber = resizeExtNumber(extNumber, smsPassage);

            // add by zhengying 20179610 加入签名自动前置后置等逻辑
            model.setContent(changeMessageContentBySignMode(model.getContent(), model.getPassageSignMode()));

            List<ProviderSendResponse> responses = smsProviderService.doTransport(getPassageParameter(model, smsPassage), model.getMobile(), model.getContent(), model.getSingleFee(), extNumber);

            // 解析调用上家接口结果
            if (CollectionUtils.isEmpty(responses)) {
                throw new RuntimeException("调用上家接口回执数据为空");
            }

            // ProviderSendResponse response = list.iterator().next();
            List<SmsMtMessageSubmit> list = buildSubmitMessage(model, responses, extNumber);
            if (CollectionUtils.isEmpty(list)) {
                logger.error("解析上家回执数据逻辑数据为空");
                return;
            }

            doSubmitFinished(list);

        } catch (Exception e) {
            logger.error("调用上家通道失败", e);
            doSubmitMessageFailed(model, model.getMobile());
        }
    }

    /**
     * TODO 获取用户的拓展号码
     *
     * @param userId
     * @param templateExtNumber 短信模板扩展号码
     * @param extNumber         用户自定义扩展号码
     * @return
     */
    private String getUserExtNumber(Integer userId, String templateExtNumber, String extNumber, String content) {

        // 签名扩展号码（1对1），优先级最高，add by 20170709
        String signExtNumber = signatureExtNoService.getExtNumber(userId, content);
        if (signExtNumber == null) {
            signExtNumber = "";
        }

        // 如果短信模板扩展名不为空，则按照此扩展号码为主（忽略用户短信配置的扩展号码）
        if (StringUtils.isNotEmpty(templateExtNumber)) {
            return signExtNumber + templateExtNumber + (StringUtils.isEmpty(extNumber) ? "" : extNumber);
        }

        // 如果签名扩展号码不为空，并且模板扩展号码为空，则以扩展号码为主（忽略用户短信配置的扩展号码）
        if (StringUtils.isNotEmpty(signExtNumber)) {
            return signExtNumber + (StringUtils.isEmpty(extNumber) ? "" : extNumber);
        }

        if (userId == null) {
            return extNumber;
        }

        UserSmsConfig userSmsConfig = userSmsConfigService.getByUserId(userId);
        if (userSmsConfig == null) {
            return extNumber;
        }

        if (StringUtils.isEmpty(userSmsConfig.getExtNumber())) {
            return extNumber;
        }

        return userSmsConfig.getExtNumber() + (StringUtils.isEmpty(extNumber) ? "" : extNumber);
    }

    /**
     * TODO 截取超出通道扩展号最大长度的位数
     *
     * @param extNumber  扩展号码
     * @param smsPassage 通道信息
     */
    private String resizeExtNumber(String extNumber, SmsPassage smsPassage) {
        if (StringUtils.isEmpty(extNumber)) {
            return extNumber;
        }

        // 如果扩展号码
        if (smsPassage == null || PASSAGE_EXT_NUMBER_LENGTH_ENDLESS == smsPassage.getExtNumber()) {
            return extNumber;
        } else if (PASSAGE_EXT_NUMBER_LENGTH_NOT_ALLOWED == smsPassage.getExtNumber()) {
            return "";
        } else {
            // add by zhengying 2017-2-50
            // 如果当前扩展号码总长度小于扩展号长度上限则在直接返回，否则按照扩展号上限截取
            return extNumber.length() < smsPassage.getExtNumber() ? extNumber : extNumber.substring(0, smsPassage.getExtNumber());
        }
    }

    /**
     * 短信签名前缀符号
     */
    private static final String MESSAGE_SIGNATURE_PRIFIX = "【";

    /**
     * 短信签名后缀符号
     */
    private static final String MESSAGE_SIGNATURE_SUFFIX = "】";

    /**
     * 扩展号码长度无限
     */
    private static final int PASSAGE_EXT_NUMBER_LENGTH_ENDLESS = -1;

    /**
     * 扩展号码不可扩展
     */
    private static final int PASSAGE_EXT_NUMBER_LENGTH_NOT_ALLOWED = 0;

    /**
     * TODO 根据签名模式调整短信内容（主要针对签名位置）
     *
     * @param content  短信内容
     * @param signMode 签名模型
     * @return
     */
    private static String changeMessageContentBySignMode(String content, Integer signMode) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }

        if (signMode == null || PassageSignMode.IGNORED.getValue() == signMode) {
            return content;
        }

        if (PassageSignMode.SIGNATURE_AUTO_PREPOSITION.getValue() == signMode) {
            // 自动前置
            if (content.endsWith(MESSAGE_SIGNATURE_SUFFIX)) {
                return content.substring(content.lastIndexOf(MESSAGE_SIGNATURE_PRIFIX)) + content.substring(0, content.lastIndexOf(MESSAGE_SIGNATURE_PRIFIX));
            }

        } else if (PassageSignMode.SIGNATURE_AUTO_POSTPOSITION.getValue() == signMode) {
            // 自动后置
            if (content.startsWith(MESSAGE_SIGNATURE_PRIFIX)) {
                return content.substring(content.indexOf(MESSAGE_SIGNATURE_SUFFIX) + 1, content.length()) + content.substring(0, content.indexOf(MESSAGE_SIGNATURE_SUFFIX) + 1);
            }

        } else if (PassageSignMode.REMOVE_SIGNATURE.getValue() == signMode) {
            // 自动去签名
            if (content.startsWith(MESSAGE_SIGNATURE_PRIFIX)) {
                content = content.substring(content.indexOf(MESSAGE_SIGNATURE_SUFFIX) + 1, content.length());
            }

            if (content.endsWith(MESSAGE_SIGNATURE_SUFFIX)) {
                content = content.substring(0, content.lastIndexOf(MESSAGE_SIGNATURE_PRIFIX));
            }

        }

        return content;
    }

    /**
     * TODO 转换获取通道参数信息
     *
     * @param packets    分包信息
     * @param smsPassage 通道信息
     * @return
     */
    private SmsPassageParameter getPassageParameter(SmsMtTaskPackets packets, SmsPassage smsPassage) {
        SmsPassageParameter parameter = new SmsPassageParameter();
        parameter.setProtocol(packets.getPassageProtocol());
        parameter.setParams(packets.getPassageParameter());
        parameter.setUrl(packets.getPassageUrl());
        parameter.setSuccessCode(packets.getSuccessCode());
        parameter.setResultFormat(packets.getResultFormat());
        parameter.setPosition(packets.getPosition());
        parameter.setPassageId(packets.getFinalPassageId());
        parameter.setPacketsSize(packets.getPassageSpeed());

        if (smsPassage == null) {
            return parameter;
        }

        // add by 20170831 加入最大连接数和连接超时时间限制（目前主要用于HTTP请求）
        parameter.setConnectionSize(smsPassage.getConnectionSize());
        parameter.setReadTimeout(smsPassage.getReadTimeout());

        if (smsPassage.getWordNumber() != null && smsPassage.getWordNumber() != UserBalanceConstant.WORDS_SIZE_PER_NUM) {
            parameter.setFeeByWords(smsPassage.getWordNumber());
        }

        // add by 20170918 判断通道是否为强制参数模式
        if (smsPassage.getSmsTemplateParam() == null || PassageSmsTemplateParam.NO.getValue() == smsPassage.getSmsTemplateParam()) {
            return parameter;
        }

        logger.info("通道：{} 为携参通道", smsPassage.getId());

        // 根据短信内容查询通道短信模板参数
        SmsPassageMessageTemplate smsPassageMessageTemplate = smsPassageMessageTemplateService.getByMessageContent(smsPassage.getId(), packets.getContent());
        if (smsPassageMessageTemplate == null) {
            logger.warn("通道：{} 短信模板参数信息匹配为空", smsPassage.getId());
            return parameter;
        }

        // 针对通道方指定模板ID及模板内变量名称数据模式设置参数
        parameter.setSmsTemplateId(smsPassageMessageTemplate.getTemplateId());
        parameter.setVariableParamNames(smsPassageMessageTemplate.getParamNames().split(","));

        // 根据表达式和参数数量获取本次具体的变量值
        parameter.setVariableParamValues(SmsPassageMessageTemplateService.pickupValuesByRegex(packets.getContent(), smsPassageMessageTemplate.getRegexValue(), smsPassageMessageTemplate.getParamNames().split(",").length));

        return parameter;
    }

    /**
     * TODO 处理提交完成逻辑
     *
     * @param submits
     */
    private void doSubmitFinished(List<SmsMtMessageSubmit> submits) {
        try {
            for (SmsMtMessageSubmit submit : submits) {
                // 如果需要推送，则设置REDIS 推送开关数据
                if (submit.getNeedPush() != null && submit.getNeedPush() && StringUtils.isNotEmpty(submit.getPushUrl())) {
                    smsMtPushService.setReadyMtPushConfig(submit);
                }
            }

            stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_DB_MESSAGE_SUBMIT_LIST, JSON.toJSONString(submits));

            logger.info("待提交信息已提交至REDIS队列完成");
        } catch (Exception e) {
            logger.error("处理待提交信息REDIS失败，失败信息：{}", JSON.toJSONString(submits), e);
        }
    }

    /**
     * TODO 组装提交完成的短息信息入库
     *
     * @param model
     * @param responses
     * @param extNumber 扩展号码
     */
    private List<SmsMtMessageSubmit> buildSubmitMessage(SmsMtTaskPackets model, List<ProviderSendResponse> responses, String extNumber) {
        List<SmsMtMessageSubmit> submits = new ArrayList<>();

        PushConfig pushConfig = pushConfigService.getPushUrl(model.getUserId(), PlatformType.SEND_MESSAGE_SERVICE.getCode(), model.getCallback());

        SmsMtMessageSubmit submit = new SmsMtMessageSubmit();

        BeanUtils.copyProperties(model, submit, "passageId", "mobile");
        submit.setPassageId(model.getFinalPassageId());

        // 推送信息为固定地址或者每次传递地址才需要推送
        if (pushConfig != null && pushConfig.getStatus() != PushConfigStatus.NO.getCode()) {
            submit.setPushUrl(pushConfig.getUrl());
            submit.setNeedPush(true);
        }

        submit.setCreateTime(new Date());
        submit.setCreateUnixtime(submit.getCreateTime().getTime());
        submit.setDestnationNo(extNumber);

        String[] mobiles = model.getMobile().split(",");

        for (String mobile : mobiles) {
            SmsMtMessageSubmit _submit = new SmsMtMessageSubmit();

            for (ProviderSendResponse response : responses) {
                if (StringUtils.isNotEmpty(response.getMobile()) && !mobile.equals(response.getMobile())) {
                    continue;
                }

                submit.setStatus(response.isSuccess() ? MessageSubmitStatus.SUCCESS.getCode()
                        : MessageSubmitStatus.FAILED.getCode());
                submit.setRemark(response.getRemark());
                submit.setMsgId(StringUtils.isNotEmpty(response.getSid()) ? response.getSid() : model.getSid() + "");
            }

            BeanUtils.copyProperties(submit, _submit);
            _submit.setMobile(mobile);

            // 获取省份代码/运营商相关内容 add by 20171126
            ProvinceLocal provinceLocal = mobileLocalService.getByMobile(mobile);
            _submit.setCmcp(provinceLocal.getCmcp());
            _submit.setProvinceCode(provinceLocal.getProvinceCode());

            // 如果提交数据失败，则需要制造伪造包补推送
            if (_submit.getStatus() == MessageSubmitStatus.FAILED.getCode()) {
                _submit.setPushErrorCode(SmsPushCode.SMS_SUBMIT_PASSAGE_FAILED.getCode());
                rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_SMS, RabbitConstant.MQ_SMS_MT_PACKETS_EXCEPTION, _submit);
            }

            submits.add(_submit);
        }

        return submits;
    }

    /**
     * TODO 提交短信至上家通道（发送错误）
     *
     * @param model
     * @param mobileReport
     */
    private void doSubmitMessageFailed(SmsMtTaskPackets model, String mobileReport) {
        PushConfig pushConfig = pushConfigService.getPushUrl(model.getUserId(), PlatformType.SEND_MESSAGE_SERVICE.getCode(), model.getCallback());

        SmsMtMessageSubmit submit = new SmsMtMessageSubmit();

        BeanUtils.copyProperties(model, submit, "passageId", "mobile");
        submit.setPassageId(model.getFinalPassageId());

        // 推送信息为固定地址或者每次传递地址才需要推送
        if (pushConfig != null && PushConfigStatus.NO.getCode() != pushConfig.getStatus()) {
            submit.setPushUrl(pushConfig.getUrl());
            submit.setNeedPush(true);
        }

        submit.setCreateTime(new Date());
        submit.setCreateUnixtime(submit.getCreateTime().getTime());
        submit.setStatus(MessageSubmitStatus.FAILED.getCode());
        submit.setRemark(SmsPushCode.SMS_SUBMIT_PASSAGE_FAILED.getCode());
        submit.setMsgId(submit.getSid().toString());

        String[] mobiles = mobileReport.split(",");
        for (String mobile : mobiles) {
            SmsMtMessageSubmit submitx = new SmsMtMessageSubmit();
            BeanUtils.copyProperties(submit, submitx);
            submitx.setCmcp(CMCP.local(mobile).getCode());
            submitx.setMobile(mobile);

            rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_SMS, RabbitConstant.MQ_SMS_MT_PACKETS_EXCEPTION, submitx);
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
                doTransport((SmsMtTaskPackets) object);
            } else {
                doTaskMain((SmsMtTask) object);
            }

        } catch (Exception e) {
            logger.error("MQ消费提交网关数据失败： {}", messageConverter.fromMessage(message), e);
            // channel.basicNack(message.getMessageProperties().getDeliveryTag(),
            // false, false);
        } finally {
            // 确认消息成功消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    private void doTaskMain(SmsMtTask task) {
        if (task == null) {
            logger.error("待提交队列解析失败，主任务为空");
            return;
        }

        List<SmsMtTaskPackets> list = task.getPackets();
        for (SmsMtTaskPackets packet : list) {
            if (packet.getStatus() == PacketsApproveStatus.WAITING.getCode() || packet.getStatus() == PacketsApproveStatus.REJECT.getCode()) {
                logger.info("数据包待处理，无需本次分包处理");
                continue;
            }

            if (StringUtils.isEmpty(packet.getMobile())) {
                logger.warn("待提交队列处理异常：手机号码为空， 跳出 {}", JSON.toJSONString(packet));
                continue;
            }

            doTransport(packet);
        }
    }

}
