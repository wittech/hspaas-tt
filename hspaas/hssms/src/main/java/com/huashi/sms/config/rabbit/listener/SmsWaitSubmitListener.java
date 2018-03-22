package com.huashi.sms.config.rabbit.listener;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.huashi.common.third.model.MobileCatagory;
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
import com.huashi.sms.config.rabbit.listener.packets.BasePacketsSupport;
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
public class SmsWaitSubmitListener extends BasePacketsSupport implements ChannelAwareMessageListener {

    @Resource
    private RabbitTemplate                    rabbitTemplate;
    @Resource
    private StringRedisTemplate               stringRedisTemplate;

    @Reference(timeout = 1200000)
    private ISmsProviderService               smsProviderService;
    @Reference
    private IPushConfigService                pushConfigService;
    @Reference
    private IMobileLocalService               mobileLocalService;
    @Reference
    private IUserSmsConfigService             userSmsConfigService;

    @Autowired
    private ISmsMtPushService                 smsMtPushService;
    @Autowired
    private ISmsPassageService                smsPassageService;
    @Autowired
    private ISignatureExtNoService            signatureExtNoService;
    @Autowired
    private Jackson2JsonMessageConverter      messageConverter;
    @Autowired
    private ISmsPassageMessageTemplateService smsPassageMessageTemplateService;

    private Logger                            logger = LoggerFactory.getLogger(getClass());

    /**
     * TODO 处理分包产生的数据，并调用上家通道接口
     *
     * @param packets 子任务
     */
    private void transport2Gateway(SmsMtTaskPackets packets) {
        if (StringUtils.isBlank(packets.getMobile())) {
            throw new RuntimeException("手机号码数据包为空，无法解析");
        }

        if (packets.getStatus() == PacketsApproveStatus.WAITING.getCode()
            || packets.getStatus() == PacketsApproveStatus.REJECT.getCode()) {
            logger.info("子任务状态为待处理或驳回，不处理");
            return;
        }

        try {
            // 组装最终发送短信的扩展号码
            String extNumber = getUserExtNumber(packets.getUserId(), packets.getTemplateExtNumber(),
                                                packets.getExtNumber(), packets.getContent());

            // 获取通道信息
            SmsPassage smsPassage = smsPassageService.findById(packets.getFinalPassageId());

            // 查询推送地址信息
            PushConfig pushConfig = pushConfigService.getPushUrl(packets.getUserId(),
                                                                 PlatformType.SEND_MESSAGE_SERVICE.getCode(),
                                                                 packets.getCallback());

            // 重新调整扩展号码
            extNumber = resizeExtNumber(extNumber, smsPassage);

            // 根据网关分包数要求对手机号码进行拆分，分批提交
            List<String> mobiles = regroupMobileByPacketsSize(packets.getMobile(), smsPassage);

            // add by zhengying 20179610 加入签名自动前置后置等逻辑
            packets.setContent(changeMessageContentBySignMode(packets.getContent(), packets.getPassageSignMode()));

            for (String mobile : mobiles) {
                // 调用网关通道处理器，提交短信信息，并接收回执
                long s0 = System.currentTimeMillis();
                List<ProviderSendResponse> responses = smsProviderService.doTransport(getPassageParameter(packets,
                                                                                                          smsPassage),
                                                                                      mobile, packets.getContent(),
                                                                                      packets.getSingleFee(), extNumber);
                long s1 = System.currentTimeMillis();
                logger.info("网关回执------------------：" + (s1 - s0));

                // ProviderSendResponse response = list.iterator().next();
                List<SmsMtMessageSubmit> list = buildSubmitMessage(packets, mobile, responses, extNumber, pushConfig);
                if (CollectionUtils.isEmpty(list)) {
                    logger.error("解析上家回执数据逻辑数据为空");
                    return;
                }

                long s2 = System.currentTimeMillis();
                logger.info("拼接SUBMIT------------------：" + (s2 - s1));

                doSubmitFinished(list);

                long s3 = System.currentTimeMillis();
                logger.info("如队列------------------：" + (s3 - s2));
            }

        } catch (Exception e) {
            logger.error("调用上家通道失败", e);
            doSubmitMessageFailed(packets, packets.getMobile());
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
     * @param extNumber 扩展号码
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
            return extNumber.length() < smsPassage.getExtNumber() ? extNumber : extNumber.substring(0,
                                                                                                    smsPassage.getExtNumber());
        }
    }

    /**
     * 短信签名前缀符号
     */
    private static final String MESSAGE_SIGNATURE_PRIFIX              = "【";

    /**
     * 短信签名后缀符号
     */
    private static final String MESSAGE_SIGNATURE_SUFFIX              = "】";

    /**
     * 扩展号码长度无限
     */
    private static final int    PASSAGE_EXT_NUMBER_LENGTH_ENDLESS     = -1;

    /**
     * 扩展号码不可扩展
     */
    private static final int    PASSAGE_EXT_NUMBER_LENGTH_NOT_ALLOWED = 0;

    /**
     * TODO 根据签名模式调整短信内容（主要针对签名位置）
     *
     * @param content 短信内容
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
                return content.substring(content.lastIndexOf(MESSAGE_SIGNATURE_PRIFIX))
                       + content.substring(0, content.lastIndexOf(MESSAGE_SIGNATURE_PRIFIX));
            }

        } else if (PassageSignMode.SIGNATURE_AUTO_POSTPOSITION.getValue() == signMode) {
            // 自动后置
            if (content.startsWith(MESSAGE_SIGNATURE_PRIFIX)) {
                return content.substring(content.indexOf(MESSAGE_SIGNATURE_SUFFIX) + 1, content.length())
                       + content.substring(0, content.indexOf(MESSAGE_SIGNATURE_SUFFIX) + 1);
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
     * @param packets 分包信息
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
        if (smsPassage.getSmsTemplateParam() == null
            || PassageSmsTemplateParam.NO.getValue() == smsPassage.getSmsTemplateParam()) {
            return parameter;
        }

        logger.info("通道：{} 为携参通道", smsPassage.getId());

        // 根据短信内容查询通道短信模板参数
        SmsPassageMessageTemplate smsPassageMessageTemplate = smsPassageMessageTemplateService.getByMessageContent(smsPassage.getId(),
                                                                                                                   packets.getContent());
        if (smsPassageMessageTemplate == null) {
            logger.warn("通道：{} 短信模板参数信息匹配为空", smsPassage.getId());
            return parameter;
        }

        // 针对通道方指定模板ID及模板内变量名称数据模式设置参数
        parameter.setSmsTemplateId(smsPassageMessageTemplate.getTemplateId());
        parameter.setVariableParamNames(smsPassageMessageTemplate.getParamNames().split(","));

        // 根据表达式和参数数量获取本次具体的变量值
        parameter.setVariableParamValues(SmsPassageMessageTemplateService.pickupValuesByRegex(packets.getContent(),
                                                                                              smsPassageMessageTemplate.getRegexValue(),
                                                                                              smsPassageMessageTemplate.getParamNames().split(",").length));

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

            stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_DB_MESSAGE_SUBMIT_LIST,
                                                       JSON.toJSONString(submits));

            logger.info("待提交信息已提交至REDIS队列完成");
        } catch (Exception e) {
            logger.error("处理待提交信息REDIS失败，失败信息：{}", JSON.toJSONString(submits), e);
        }
    }

    /**
     * TODO 组装提交完成的短息信息入库
     * 
     * @param packets
     * @param mobile
     * @param responses
     * @param extNumber 扩展号码
     * @param pushConfig
     * @return
     */
    private List<SmsMtMessageSubmit> buildSubmitMessage(SmsMtTaskPackets packets, String mobile,
                                                        List<ProviderSendResponse> responses, String extNumber,
                                                        PushConfig pushConfig) {
        List<SmsMtMessageSubmit> submits = new ArrayList<>();

        SmsMtMessageSubmit submit = new SmsMtMessageSubmit();

        long l1 = System.currentTimeMillis();
        BeanUtils.copyProperties(packets, submit, "passageId", "mobile");
        submit.setPassageId(packets.getFinalPassageId());
        long l2 = System.currentTimeMillis();
        logger.info("SmsWaitSubmitListener l1 BeanUtils.copyProperties" + (l2 - l1));

        // 推送信息为固定地址或者每次传递地址才需要推送
        if (pushConfig != null && pushConfig.getStatus() != PushConfigStatus.NO.getCode()) {
            submit.setPushUrl(pushConfig.getUrl());
            submit.setNeedPush(true);
        }

        submit.setCreateTime(new Date());
        submit.setCreateUnixtime(submit.getCreateTime().getTime());
        submit.setDestnationNo(extNumber);

        String[] mobiles = mobile.split(",");

        
        long t1 = System.currentTimeMillis();
        for (String m : mobiles) {
            SmsMtMessageSubmit _submit = new SmsMtMessageSubmit();

            long f1 = System.currentTimeMillis();
            for (ProviderSendResponse response : responses) {
                if (StringUtils.isNotEmpty(response.getMobile()) && !m.equals(response.getMobile())) {
                    continue;
                }

                submit.setStatus(response.isSuccess() ? MessageSubmitStatus.SUCCESS.getCode() : MessageSubmitStatus.FAILED.getCode());
                submit.setRemark(response.getRemark());
                submit.setMsgId(StringUtils.isNotEmpty(response.getSid()) ? response.getSid() : packets.getSid() + "");
            }
            long f2 = System.currentTimeMillis();
            
            logger.info("ffffffff f2" + (f2 - f1));
            
            long b1 = System.currentTimeMillis();
            BeanUtils.copyProperties(submit, _submit);
            _submit.setMobile(m);
            long b2 = System.currentTimeMillis();
            
            
            logger.info("BeanUtils.copyProperties(submit, _submit) b2" + (b2 - b1));

            long s1 = System.currentTimeMillis();
            // 获取省份代码/运营商相关内容 add by 20171126
            ProvinceLocal provinceLocal = mobileLocalService.getByMobile(m);
            _submit.setCmcp(provinceLocal.getCmcp());
            _submit.setProvinceCode(provinceLocal.getProvinceCode());
            long s2 = System.currentTimeMillis();
            logger.info("mobileLocalService.getByMobile(m) s2 " + (s2 - s1));

            // 如果提交数据失败，则需要制造伪造包补推送
            if (_submit.getStatus() == MessageSubmitStatus.FAILED.getCode()) {
                _submit.setPushErrorCode(SmsPushCode.SMS_SUBMIT_PASSAGE_FAILED.getCode());
                rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_SMS, RabbitConstant.MQ_SMS_MT_PACKETS_EXCEPTION,
                                              _submit);
            }

            submits.add(_submit);
        }
        
        long t2 = System.currentTimeMillis();
        
        logger.info("fffffffffff t2 " + (t2 - t1));

        return submits;
    }

    /**
     * TODO 提交短信至上家通道（发送网关错误，组装伪造包S0099）
     *
     * @param model
     * @param mobileReport
     */
    private void doSubmitMessageFailed(SmsMtTaskPackets model, String mobileReport) {
        PushConfig pushConfig = pushConfigService.getPushUrl(model.getUserId(),
                                                             PlatformType.SEND_MESSAGE_SERVICE.getCode(),
                                                             model.getCallback());

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

            rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_SMS, RabbitConstant.MQ_SMS_MT_PACKETS_EXCEPTION,
                                          submitx);
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
     * @param smsPassage
     * @return
     */
    private static List<String> regroupMobileByPacketsSize(String mobile, SmsPassage smsPassage) {
        if (StringUtils.isBlank(mobile)) {
            throw new RuntimeException("提交任务错误：手机号码为空");
        }

        String[] mobiles = mobile.split(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
        if (mobiles.length == 1) {
            return Arrays.asList(mobiles);
        }

        // 如果通道为空或者分包手机号码未配置按照 分包队列的默认数直接提交（默认4000）
        if (smsPassage == null || smsPassage.getMobileSize() == null || smsPassage.getMobileSize() == 0
            || smsPassage.getMobileSize() == DEFAULT_REQUEST_MOBILE_PACKAGE_SIZE) {
            return regroupMobiles(mobiles, mobiles.length);
        }

        return regroupMobiles(mobiles, smsPassage.getMobileSize());
    }

}
