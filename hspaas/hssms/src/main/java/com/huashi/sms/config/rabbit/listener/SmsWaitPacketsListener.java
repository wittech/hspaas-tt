package com.huashi.sms.config.rabbit.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.huashi.bill.pay.constant.PayContext.PaySource;
import com.huashi.bill.pay.constant.PayContext.PayType;
import com.huashi.common.settings.context.SettingsContext;
import com.huashi.common.settings.domain.Province;
import com.huashi.common.settings.domain.PushConfig;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.common.third.model.MobileCatagory;
import com.huashi.common.third.service.IMobileLocalService;
import com.huashi.common.user.context.UserBalanceConstant;
import com.huashi.common.user.context.UserSettingsContext.SmsSignatureSource;
import com.huashi.common.user.domain.UserSmsConfig;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.user.service.IUserPassageService;
import com.huashi.common.user.service.IUserSmsConfigService;
import com.huashi.common.util.PatternUtil;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode.SmsPushCode;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.rabbit.AbstartRabbitListener;
import com.huashi.sms.config.rabbit.constant.RabbitConstant;
import com.huashi.sms.passage.context.PassageContext;
import com.huashi.sms.passage.context.PassageContext.PassageStatus;
import com.huashi.sms.passage.context.PassageContext.RouteType;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.passage.service.ISmsPassageAccessService;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.sms.settings.service.IForbiddenWordsService;
import com.huashi.sms.settings.service.ISmsMobileBlackListService;
import com.huashi.sms.settings.service.ISmsMobileTablesService;
import com.huashi.sms.settings.service.ISmsMobileWhiteListService;
import com.huashi.sms.task.context.TaskContext.MessageSubmitStatus;
import com.huashi.sms.task.context.TaskContext.PacketsActionActor;
import com.huashi.sms.task.context.TaskContext.PacketsActionPosition;
import com.huashi.sms.task.context.TaskContext.PacketsApproveStatus;
import com.huashi.sms.task.context.TaskContext.PacketsProcessStatus;
import com.huashi.sms.task.domain.SmsMtTask;
import com.huashi.sms.task.domain.SmsMtTaskPackets;
import com.huashi.sms.task.exception.QueueProcessException;
import com.huashi.sms.task.model.SmsRoutePassage;
import com.huashi.sms.template.context.TemplateContext;
import com.huashi.sms.template.context.TemplateContext.IgnoreBlacklist;
import com.huashi.sms.template.domain.MessageTemplate;
import com.huashi.sms.template.service.ISmsTemplateService;
import com.rabbitmq.client.Channel;

/**
 * TODO 待消息分包处理
 *
 * @author zhengying
 * @version V1.0.0  
 * @date 2016年9月8日 下午11:35:54
 */
@Component
public class SmsWaitPacketsListener extends AbstartRabbitListener {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private Jackson2JsonMessageConverter messageConverter;

    @Autowired
    private ISmsTemplateService smsTemplateService;
    @Autowired
    private IForbiddenWordsService forbiddenWordsService;
    @Autowired
    private ISmsPassageAccessService smsPassageAccessService;
    @Autowired
    private ISmsMobileTablesService smsMobileTablesService;
    @Autowired
    private ISmsMobileBlackListService mobileBlackListService;
    @Autowired
    private ISmsMobileWhiteListService smsMobileWhiteListService;
    @Autowired
    private ISmsMtSubmitService smtMtSubmitService;

    @Reference
    private IMobileLocalService mobileLocalService;
    @Reference
    private IPushConfigService pushConfigService;
    @Reference
    private IUserBalanceService userBalanceService;
    @Reference
    private IUserPassageService userPassageService;
    @Reference
    private IUserSmsConfigService userSmsConfigService;

    /**
     * 根据当前用户ID和短信内容提取出的短信模板信息
     */
    private final ThreadLocal<MessageTemplate> messageTemplateLocal = new ThreadLocal<>();

    /**
     * 错误序列号计数器
     */
    private final AtomicInteger errorNo = new AtomicInteger();

    /**
     * TODO 正常任务处理
     *
     * @param task
     * @param mobileCatagory
     * @param routePassage
     */
    private void doPassagePacketsFinished(SmsMtTask task, MobileCatagory mobileCatagory, SmsRoutePassage routePassage) {
        try {
            // 初始化分包信息
            task.setPackets(new ArrayList<>());

            subpackage(task, routePassage);

            // 生成子任务，并异步发送数据
            asyncSendTask(task, mobileCatagory);
        } catch (Exception e) {
            logger.warn("分包逻辑失败", e);
        }
    }

    /**
     * TODO 针对异常情况，提交子任务
     *
     * @param task
     */
    private void asyncSendTask(SmsMtTask task) {
        task.setPackets(null);
        asyncSendTask(task, null);
    }

    /**
     * TODO 正常任务执行
     *
     * @param task
     * @param mobileCatagory
     * @return
     */
    private void asyncSendTask(SmsMtTask task, MobileCatagory mobileCatagory) {
        task.setFinalContent(task.getContent());
        // 中间可能存在 去除黑名单等逻辑剔除不符合手机号码，但主任务需要保留原号码数据
        task.setMobile(task.getOriginMobile());

        // 如果错误消息为空，则认为处理状态为 正常
        task.setProcessStatus(StringUtils.isEmpty(task.getErrorMessageReport()) || CollectionUtils.isEmpty(task.getPackets()) ? PacketsProcessStatus.PROCESS_COMPLETE.getCode() : PacketsProcessStatus.PROCESS_EXCEPTION.getCode());

        task.setMessageTemplateId(messageTemplateLocal.get() == null ? null : messageTemplateLocal.get().getId());
        task.setForceActions(task.getForceActionsReport().toString());

        // 如果正在分包或者分包异常，则审核状态为待审核
        task.setApproveStatus(PacketsProcessStatus.DOING.getCode() == task.getProcessStatus() || PacketsProcessStatus.PROCESS_EXCEPTION.getCode() == task.getProcessStatus() ? PacketsApproveStatus.WAITING.getCode() : PacketsApproveStatus.AUTO_COMPLETE.getCode());

        if (mobileCatagory != null) {
            task.setErrorMobiles(mobileCatagory.getFilterNumbers());
            task.setRepeatMobiles(mobileCatagory.getRepeatNumbers());
            // 设置需要返还的条数
            if (mobileCatagory.getFilterSize() != 0 || mobileCatagory.getRepeatSize() != 0) {
                task.setReturnFee((mobileCatagory.getFilterSize() + mobileCatagory.getRepeatSize()) * task.getFee());
            }
        }

        task.setRemark(task.getErrorMessageReport().toString());
        task.setProcessTime(new Date());


        // 发送异步队列 暂时改成本地数据
        stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_DB_MESSAGE_TASK_LIST, JSON.toJSONString(task));

        //  分包状态="分包完成"
        if (PacketsProcessStatus.PROCESS_COMPLETE.getCode() == task.getProcessStatus() && PacketsApproveStatus.WAITING.getCode() != task.getApproveStatus()) {

            // 发送至待提交信息队列
            smtMtSubmitService.sendToSubmitQueue(task.getPackets());

        }

        // 如果存在错号或者重复号码需要将 之前的计费返还到客户余额
        returnFeeToUser(task, mobileCatagory);
    }

    /**
     * TODO 当任务中包含错号/重号 返还相应余额给用户
     *
     * @param task
     * @param mobileCatagory
     */
    private void returnFeeToUser(SmsMtTask task, MobileCatagory mobileCatagory) {
        if (task.getReturnFee() != null && task.getReturnFee() != 0 && mobileCatagory != null) {
            logger.info("用户ID：{} 发送短信 存在错号：{}个，重复号码：{}个，单条计费：{}条，共扣费：{}条，共需返还{}条", task.getUserId(), mobileCatagory.getFilterSize(), mobileCatagory.getRepeatSize(), task.getFee(), (task.getMobiles().length - mobileCatagory.getFilterSize() - mobileCatagory.getRepeatSize()) * task.getFee(), task.getReturnFee());
            try {
                userBalanceService.updateBalance(task.getUserId(), task.getReturnFee(), PlatformType.SEND_MESSAGE_SERVICE.getCode(), PaySource.USER_ACCOUNT_EXCHANGE, PayType.SYSTEM, 0d, 0d, "错号或者重号返还", false);
            } catch (Exception e) {
                logger.error("返还用户ID：{}，总短信条数：{} 失败", task.getUserId(), task.getMobiles().length * task.getFee(), e);
            }
        }
    }
    
    /**
     * 
       * TODO 根据短信模板配置判断是否忽略手机黑名单
       * 
       * @return
     */
    private boolean isIgnoreBlacklist() {
        return messageTemplateLocal.get() != null
                && IgnoreBlacklist.YES.getValue() == messageTemplateLocal.get().getIgnoreBlacklist();
    }

    /**
     * TODO 手机号码处理逻辑，黑名单判断/无效手机号码过滤/运营商分流
     *
     * @param task
     * @return
     */
    private MobileCatagory findOutMatchedMobiles(SmsMtTask task) {
        // 转换手机号码数组
        List<String> mobiles = new ArrayList<>(Arrays.asList(task.getMobile().split(MobileCatagory.MOBILE_SPLIT_CHARCATOR)));
        
        // 移除上次 黑名单数据（主要针对重新分包黑名单不要重复产生记录）add by 2017-04-08
        if (StringUtils.isNotEmpty(task.getBlackMobiles())) {
            mobiles.removeAll(Arrays.asList(task.getBlackMobiles().split(MobileCatagory.MOBILE_SPLIT_CHARCATOR)));
        }
        
        // 黑名单手机号码
        List<String> blackMobiles = mobileBlackListService.filterBlacklistMobile(mobiles, isIgnoreBlacklist());
        if (CollectionUtils.isNotEmpty(blackMobiles)) {
            // 移除需要执行的手机号码
            task.setMobile(StringUtils.join(mobiles, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
            doExceptionOverWithReport(task, blackMobiles, SmsPushCode.SMS_MOBILE_BLACKLIST.getCode());
            task.setBlackMobiles(StringUtils.join(blackMobiles, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
            logger.warn("手机黑名单: {}", StringUtils.join(blackMobiles, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
        }

        // 经过黑名单处理后，如果可用手机号码为空则直接插入主任务
        if (CollectionUtils.isEmpty(mobiles)) {
            // 黑名单直接插入SUBMIT，自己制作伪造包BLACK状态推送给用户（推送队列）
            task.getErrorMessageReport().append(formatMessage("可用手机号码为空（为空或不符合手机号码）"));
            logger.warn("可用手机号码为空，逻辑结束");
            return null;
        }

        // 号码分流
        MobileCatagory mobileNumberResponse = mobileLocalService.doCatagory(mobiles);
        if (mobileNumberResponse == null) {
            task.getErrorMessageReport().append(formatMessage("手机号码解析错误（为空或不符合手机号码"));
            return null;
        }

        if (!mobileNumberResponse.isSuccess()) {
            task.getErrorMessageReport().append(formatMessage("手机号码分流失败"));
            logger.warn(mobileNumberResponse.getMsg());
            return null;
        }

        return mobileNumberResponse;
    }

    @Override
    @RabbitListener(queues = RabbitConstant.MQ_SMS_MT_WAIT_PROCESS)
    public void onMessage(Message message, Channel channel) throws Exception {
        
        checkIsStartingConsumeMessage();
        
        try {
            if(message == null) {
                return;
            }
            
            SmsMtTask model = (SmsMtTask) messageConverter.fromMessage(message);
            model.setOriginMobile(model.getMobile());

            // 用户短信配置中心数据
            UserSmsConfig smsConfig = getSmsConfig(model);

            // 获取短信模板信息
            loadSmsTemplateByContent(model, smsConfig);

            // 校验同模板下手机号码是否超速，超量
            if (!isSameMobileOutOfRange(model, smsConfig)) {
                asyncSendTask(model);
                return;
            }

            // 如果短信内容包含敏感词，打标记
            markContentHasSensitiveWords(model);

            // 短信手机号码处理逻辑
            MobileCatagory mobileCatagory = findOutMatchedMobiles(model);
            if (mobileCatagory == null) {
                asyncSendTask(model);
                return;
            }

            // 获取用户路由（分省）通道信息
            SmsRoutePassage passage = getUserRoutePassage(model, mobileCatagory);

            // 通道分包逻辑
            doPassagePacketsFinished(model, mobileCatagory, passage);

        } catch (Exception e) {
            logger.error("MQ消费任务分包失败： {}", messageConverter.fromMessage(message), e);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            // 清除ThreadLocal对象，加速GC，减小内存压力
            messageTemplateLocal.remove();
            // 置分包异常提示计数器清零
            errorNo.set(0);
        }
    }

    // @Override
    // public void confirm(CorrelationData correlationData, boolean ack, String
    // cause) {
    // if(correlationData == null)
    // return;
    //
    // if (ack) {
    // logger.error("=================消息队列处理成功：{}", correlationData.getId());
    // } else {
    // logger.error("=================消息队列处理失败：{}，信息：{}",
    // correlationData.getId(), cause);
    // }
    // }

    // @PostConstruct
    // public void setConfirmCallback() {
    // // rabbitTemplate如果为单例的话，那回调就是最后设置的内容
    // rabbitTemplate.setConfirmCallback(this);
    // }

    /**
     * TODO 验证数据有效性并返回用户短信配置信息
     *
     * @param task
     */
    private UserSmsConfig getSmsConfig(SmsMtTask task) {
        if (task == null) {
            throw new QueueProcessException("待处理任务数据为空");
        }

        if (StringUtils.isEmpty(task.getMobile())) {
            throw new QueueProcessException("手机号码为空");
        }

        UserSmsConfig userSmsConfig = userSmsConfigService.getByUserId(task.getUserId());
        if (userSmsConfig == null) {
            userSmsConfigService.save(task.getUserId(), UserBalanceConstant.WORDS_SIZE_PER_NUM, task.getExtNumber());
            task.getErrorMessageReport().append(formatMessage("户短信配置为空，需要更新"));
        }

        return userSmsConfig;
    }

    /**
     * TODO 保存子任务
     *
     * @param task          主任务
     * @param mobile        手机号码
     * @param passageAccess 可用通道信息
     */
    private void joinTaskPackets(SmsMtTask task, String mobile, SmsPassageAccess passageAccess) {
        SmsMtTaskPackets smsMtTaskPackets = new SmsMtTaskPackets();
        smsMtTaskPackets.setSid(task.getSid());
        smsMtTaskPackets.setMobile(mobile);

        // 本次通道对应的运营商和省份代码
        if (passageAccess != null) {
            smsMtTaskPackets.setCmcp(passageAccess.getCmcp());
            smsMtTaskPackets.setProvinceCode(passageAccess.getProvinceCode());
        } else {
            smsMtTaskPackets.setCmcp(CMCP.local(mobile).getCode());
        }

        smsMtTaskPackets.setContent(task.getContent());
        smsMtTaskPackets.setMobileSize(mobile.split(MobileCatagory.MOBILE_SPLIT_CHARCATOR).length);

        // edit by zhengying 20170621 针对短信模板加入扩展号码逻辑
        if (messageTemplateLocal.get() != null) {
            smsMtTaskPackets.setMessageTemplateId(messageTemplateLocal.get().getId());
            smsMtTaskPackets.setTemplateExtNumber(messageTemplateLocal.get().getExtNumber());
        }

        if (passageAccess != null) {
            smsMtTaskPackets.setPassageId(passageAccess.getPassageId());
            smsMtTaskPackets.setPassageCode(passageAccess.getPassageCode());
            smsMtTaskPackets.setFinalPassageId(passageAccess.getPassageId());
            smsMtTaskPackets.setPassageProtocol(passageAccess.getProtocol());
            smsMtTaskPackets.setPassageUrl(passageAccess.getUrl());
            smsMtTaskPackets.setPassageParameter(passageAccess.getParams());
            smsMtTaskPackets.setResultFormat(passageAccess.getResultFormat());
            smsMtTaskPackets.setPosition(passageAccess.getPosition());
            smsMtTaskPackets.setSuccessCode(passageAccess.getSuccessCode());
            smsMtTaskPackets.setPassageSpeed(passageAccess.getPacketsSize());

            // add by zhengying 20170610 加入签名模式
            smsMtTaskPackets.setPassageSignMode(passageAccess.getSignMode());
        }

        smsMtTaskPackets.setRemark(task.getErrorMessageReport().toString());
        smsMtTaskPackets.setForceActions(task.getForceActionsReport().toString());
        smsMtTaskPackets.setRetryTimes(0);
        smsMtTaskPackets.setCreateTime(new Date());

        // 如果账号是华时系统通知账号则直接通过
        // boolean isAvaiable = isHsAdmin(task.getAppKey());

        // 短信模板ID为空，短信包含敏感词及其他错误信息，短信通道为空 均至状态为 待人工处理
        if (passageAccess == null || StringUtils.isNotEmpty(smsMtTaskPackets.getRemark()) || messageTemplateLocal.get() == null || messageTemplateLocal.get().getId() == null) {
            smsMtTaskPackets.setStatus(PacketsApproveStatus.WAITING.getCode());
        } else {
            smsMtTaskPackets.setStatus(PacketsApproveStatus.AUTO_COMPLETE.getCode());
        }

        // 用户自定义内容，一般为他方子平台的开发者ID（渠道），用于标识
        smsMtTaskPackets.setAttach(task.getAttach());
        // 设置用户自设置的扩展号码
        smsMtTaskPackets.setExtNumber(task.getExtNumber());
        smsMtTaskPackets.setCallback(task.getCallback());
        smsMtTaskPackets.setUserId(task.getUserId());
        smsMtTaskPackets.setFee(task.getFee() == null ? UserBalanceConstant.CONTENT_WORDS_EXCEPTION_COUNT_FEE : task.getFee());
        smsMtTaskPackets.setSingleFee(smsMtTaskPackets.getFee());

        // 追加子任务
        task.getPackets().add(smsMtTaskPackets);
    }

    /**
     * TODO 追加错误信息
     */
    private String formatMessage(String message) {
        return String.format("%d)%s;", errorNo.incrementAndGet(), message);
    }

    /**
     * TODO 拼接可操作动作代码
     *
     * @param index
     */
    private void refillForceActions(int index, StringBuilder forceActions) {
        // 异常分包情况下允许的操作，如000,010，第一位:未报备模板，第二位：敏感词，第三位：通道不可用
        char[] actions = forceActions.toString().toCharArray();

        actions[index] = PacketsActionActor.BROKEN.getActor();

        forceActions.setLength(0);
        forceActions.append(String.valueOf(actions));
    }

    /**
     * TODO 校验短信签名相关内容
     *
     * @param task
     * @param smsConfig
     */
    private void checkContentSignature(SmsMtTask task, UserSmsConfig smsConfig) {
        // 如果用户不携带签名模式（一客一签模式），模板匹配需要考虑时候将原短信内容基础上加入签名进行匹配
        if (smsConfig.getSignatureSource() != null && smsConfig.getSignatureSource() == SmsSignatureSource.HSPAAS_AUTO_APPEND.getValue()) {

            if (StringUtils.isEmpty(smsConfig.getSignatureContent())) {
                task.getErrorMessageReport().append(formatMessage("短信内容强制签名但用户签名内容未设置"));
            } else {
                task.setContent(String.format("【%s】%s", smsConfig.getSignatureContent(), task.getContent()));
            }

        } else {

            // 如果签名为客户自维护，则需要判断签名相关信息
            if (!PatternUtil.isContainsSignature(task.getContent())) {
                task.getErrorMessageReport().append(formatMessage("用户短信内容不包含签名"));
            }

            // 判断短信内容是否包含多个签名 edit by 20170610 暂时屏蔽
//			if (PatternUtil.isMultiSignatures(model.getContent())) {
//				model.getErrorMessageReport().append(formatMessage("用户短信内容包含多个签名"));
//			}

        }
    }

    /**
     * TODO 标记短信是否有敏感词
     *
     * @param task
     */
    private void markContentHasSensitiveWords(SmsMtTask task) {
        // 判断敏感词开关是否开启 edit by 20180509
//        if (forbiddenWordsService.isForbiddenWordsAllowPassed()) {
//            return;
//        }

        // 短信模板报备白名单词汇（如果本次内容包含此词汇不算作敏感词）
        String whiteWordsRecord = messageTemplateLocal.get() == null ? null : messageTemplateLocal.get().getWhiteWord();

        // 判断短信内容是否包含敏感词
        Set<String> filterWords;
        if (StringUtils.isEmpty(whiteWordsRecord)) {
            filterWords = forbiddenWordsService.filterForbiddenWords(task.getContent());

        } else {
            // 报备的免审敏感词（报备后对敏感词有效）
            Set<String> whiteWordsSet = new HashSet<>(Arrays.asList(whiteWordsRecord.split("\\|")));
            filterWords = forbiddenWordsService.filterForbiddenWords(task.getContent(), whiteWordsSet);
        }

        if (CollectionUtils.isNotEmpty(filterWords)) {
            task.setForbiddenWords(StringUtils.join(filterWords, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
            task.getErrorMessageReport().append(formatMessage("用户短信内容存在敏感词，敏感词为：" + filterWords.toString()));
            refillForceActions(PacketsActionPosition.FOBIDDEN_WORDS.getPosition(), task.getForceActionsReport());
        }

    }

    /**
     * TODO 用户通道处理逻辑
     *
     * @param task
     * @param mobileCatagory
     * @return
     */
    private SmsRoutePassage getUserRoutePassage(SmsMtTask task, MobileCatagory mobileCatagory) {
        Integer passageGroupId = userPassageService.getByUserIdAndType(task.getUserId(), PlatformType.SEND_MESSAGE_SERVICE.getCode());
        if (passageGroupId == null) {
            task.getErrorMessageReport().append("用户通道组未找到");
            refillForceActions(PacketsActionPosition.PASSAGE_NOT_AVAIABLE.getPosition(), task.getForceActionsReport());
            return null;
        }

        return doRoutePassageByCmcp(task, mobileCatagory, task.getUserId());
    }

    /**
     * TODO 获取短信路由类型
     * 如果路由类型未确定，则按默认路由进行
     *
     * @return
     */
    private Integer getMessageRouteType() {
        if (messageTemplateLocal.get() == null || messageTemplateLocal.get().getRouteType() == null) {
            return RouteType.DEFAULT.getValue();
        }

        return messageTemplateLocal.get().getRouteType();
    }

    /**
     * TODO 获取运营商手机号码信息（手机号码分省分流）
     *
     * @param cmcp
     * @param mobileCatagory
     * @return
     */
    private static Map<Integer, String> getCmcpMobileNumbers(CMCP cmcp, MobileCatagory mobileCatagory) {
        if (CMCP.CHINA_MOBILE == cmcp) {
            return mobileCatagory.getCmNumbers();
        } else if (CMCP.CHINA_TELECOM == cmcp) {
            return mobileCatagory.getCtNumbers();
        } else if (CMCP.CHINA_UNICOM == cmcp) {
            return mobileCatagory.getCuNumbers();
        }

        return null;
    }

    /**
     * 运营商枚举
     */
    private static final CMCP[] CMCPS = {CMCP.CHINA_MOBILE, CMCP.CHINA_TELECOM, CMCP.CHINA_UNICOM};

    /**
     * 根据运营商和路由通道寻找具体的通道信息
     *
     * @param model
     * @param mobileCatagory 分省后手机号码组
     * @param userId         用户ID
     * @return
     */
    private SmsRoutePassage doRoutePassageByCmcp(SmsMtTask model, MobileCatagory mobileCatagory, int userId) {
        SmsPassageAccess passageAccess;
        boolean isAvaiable;

        SmsRoutePassage routePassage = new SmsRoutePassage();
        routePassage.setUserId(userId);

        Integer routeType = getMessageRouteType();
        
        Map<String, SmsPassageAccess> userPassages = smsPassageAccessService.getByUserId(userId);
        // 是否有可用通道
        boolean hasAvaiableAccess = MapUtils.isNotEmpty(userPassages);
        if (!hasAvaiableAccess) {
            model.getErrorMessageReport().append(formatMessage("通道不可用"));
            refillForceActions(PacketsActionPosition.PASSAGE_NOT_AVAIABLE.getPosition(), model.getForceActionsReport());
        }
        
        Map<Integer, String> provinceCmcpMobileNumbers;
        for (CMCP cmcp : CMCPS) {
            provinceCmcpMobileNumbers = getCmcpMobileNumbers(cmcp, mobileCatagory);
            if (MapUtils.isEmpty(provinceCmcpMobileNumbers)) {
                continue;
            }

            Set<Integer> provinceCodes = provinceCmcpMobileNumbers.keySet();
            for (Integer provinceCode : provinceCodes) {
                
                // 如果没有可用通道，则直接将相关省份手机号码进行分配
                if(!hasAvaiableAccess) {
                    routePassage.addPassageMobilesMapping(PassageContext.EXCEPTION_PASSAGE_ID, provinceCmcpMobileNumbers.get(provinceCode));
                    continue;
                }
                
                passageAccess = userPassages.get(smsPassageAccessService.getAssistKey(routeType, cmcp.getCode(), provinceCode));

                isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
                if (!isAvaiable) {
                    // 如果通道不可用，判断当前运营商是否包含 全国通道
                    passageAccess = userPassages.get(smsPassageAccessService.getAssistKey(routeType, cmcp.getCode(), Province.PROVINCE_CODE_ALLOVER_COUNTRY));
                    isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
                }

                if (isAvaiable) {
                    routePassage.addPassageMobilesMapping(passageAccess.getPassageId(), provinceCmcpMobileNumbers.get(provinceCode));
                    routePassage.getPassaegAccesses().put(passageAccess.getPassageId(), passageAccess);
                } else {
                    // edit by 20180414 如果未找到相关CMCP的通道及全国通道，则分包异常
                    model.getErrorMessageReport().append(formatMessage("通道不可用"));
                    refillForceActions(PacketsActionPosition.PASSAGE_NOT_AVAIABLE.getPosition(), model.getForceActionsReport());
                    routePassage.addPassageMobilesMapping(PassageContext.EXCEPTION_PASSAGE_ID, provinceCmcpMobileNumbers.get(provinceCode));
                }
            }
        }
        return routePassage;
    }

    /**
     * TODO 验证通道是否可用
     *
     * @param access
     * @return
     */
    private boolean isSmsPassageAccessAvaiable(SmsPassageAccess access) {
        return access != null && access.getStatus() != null && access.getStatus() == PassageStatus.ACTIVE.getValue();
    }

    /**
     * TODO 获取短信模板数据
     *
     * @param task
     * @param smsConfig
     */
    private void loadSmsTemplateByContent(SmsMtTask task, UserSmsConfig smsConfig) {
        // 短信签名判断
        checkContentSignature(task, smsConfig);

        MessageTemplate template;
        // 短信是否免审
        if (!smsConfig.getMessagePass()) {
            template = new MessageTemplate();
            template.setId(TemplateContext.SUPER_TEMPLATE_ID);
            template.setRouteType(RouteType.DEFAULT.getValue());
            template.setSubmitInterval(smsConfig.getSubmitInterval());
            template.setLimitTimes(smsConfig.getLimitTimes());

        } else {
            // 根据短信内容匹配模板，短信模板需要报备而查出的短信模板为空则提至人工处理信息中
            template = smsTemplateService.getByContent(task.getUserId(), task.getContent());
            if (template == null) {
                task.getErrorMessageReport().append(formatMessage("用户短信模板未报备"));
                refillForceActions(PacketsActionPosition.SMS_TEMPLATE_MISSED.getPosition(), task.getForceActionsReport());
            }
        }

        // 如果模板不为空则设置线程内模板变量
        if (template != null) {
            messageTemplateLocal.set(template);
        }
    }

    /**
     * 判断用户手机号码是超限/超速
     *
     * @param task
     * @param smsConfig
     * @return
     */
    private boolean isSameMobileOutOfRange(SmsMtTask task, UserSmsConfig smsConfig) {
        fillTemplateAttributes(smsConfig);

        // 根据userId获取白名单手机号码数据
        Set<String> whiteMobiles = smsMobileWhiteListService.getByUserId(task.getUserId());

        // 转换手机号码数组
        List<String> mobiles = new ArrayList<>(Arrays.asList(task.getMobile().split(MobileCatagory.MOBILE_SPLIT_CHARCATOR)));

        // 过滤超速集合
        List<String> benyondSpeedList = new ArrayList<>();

        // 过滤超限集合
        List<String> benyondTimesList = new ArrayList<>();
        for (String mobile : mobiles) {
            // 判断手机号码是否是用户的白名单手机号码，是则不拦截 add by 2017-06-26
            // edit by zhengying 20171126 加入批量查询白名单手机号码功能
            if (CollectionUtils.isNotEmpty(whiteMobiles) && whiteMobiles.contains(mobile)) {
                continue;
            }

            // 判断短信发送是否超速
            int beyondExpected = smsMobileTablesService.checkMobileIsBeyondExpected(task.getUserId(), messageTemplateLocal.get().getId(), mobile, messageTemplateLocal.get().getSubmitInterval(), messageTemplateLocal.get().getLimitTimes());

            if (ISmsMobileTablesService.MOBILE_BEYOND_SPEED == beyondExpected) {
                benyondSpeedList.add(mobile);
                continue;
            }

            // 短信是否超量
            if (ISmsMobileTablesService.MOBILE_BEYOND_TIMES == beyondExpected) {
                benyondTimesList.add(mobile);
            }
        }

        if (CollectionUtils.isNotEmpty(benyondSpeedList)) {
            // 移除需要执行的手机号码
            mobiles.removeAll(benyondSpeedList);
            task.setMobile(StringUtils.join(mobiles, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
            doExceptionOverWithReport(task, benyondSpeedList, SmsPushCode.SMS_SAME_MOBILE_NUM_SEND_BY_HIGN_FREQUENCY.getCode());
            logger.warn("手机号码超速 {}", StringUtils.join(benyondSpeedList, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
        }

        if (CollectionUtils.isNotEmpty(benyondTimesList)) {
            // 移除需要执行的手机号码
            mobiles.removeAll(benyondTimesList);
            task.setMobile(StringUtils.join(mobiles, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
            doExceptionOverWithReport(task, benyondTimesList, SmsPushCode.SMS_SAME_MOBILE_NUM_BEYOND_LIMIT_IN_ONE_DAY.getCode());
            logger.warn("手机号码超量 {}", StringUtils.join(benyondSpeedList, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
        }

        return CollectionUtils.isNotEmpty(mobiles);
    }

    /**
     * TODO 设置用户短息内容归属的模板信息
     *
     * @param smsConfig
     */
    private void fillTemplateAttributes(UserSmsConfig smsConfig) {
        MessageTemplate template = messageTemplateLocal.get();
        if (template == null) {
            template = new MessageTemplate();
            template.setId(TemplateContext.SUPER_TEMPLATE_ID);
            template.setLimitTimes(smsConfig.getLimitTimes());
            template.setSubmitInterval(smsConfig.getSubmitInterval());
        } else {
            if (template.getLimitTimes() == null) {
                template.setLimitTimes(TemplateContext.DEFAULT_LIMIT_TIMES);
            }

            if (template.getSubmitInterval() == null) {
                template.setSubmitInterval(TemplateContext.DEFAULT_SUBMIT_INTERVAL);
            }

        }
        messageTemplateLocal.set(template);
    }

    /**
     * TODO 执行异常结束逻辑(制造状态伪造包，需要判断是否需要状态报告)
     *
     * @param task
     * @param mobiles
     * @param remark  备注
     */
    private void doExceptionOverWithReport(SmsMtTask task, List<String> mobiles, String remark) {
        SmsMtMessageSubmit submit = new SmsMtMessageSubmit();
        submit.setUserId(task.getUserId());
        submit.setSid(task.getSid());

        submit.setContent(task.getContent());
        submit.setFee(task.getFee());
        submit.setAttach(task.getAttach());
        submit.setPassageId(PassageContext.EXCEPTION_PASSAGE_ID);
        submit.setCreateTime(new Date());
        submit.setCreateUnixtime(submit.getCreateTime().getTime());
        submit.setStatus(MessageSubmitStatus.SUCCESS.getCode());
        submit.setRemark(remark);
        submit.setMsgId(task.getSid().toString());
        submit.setCallback(task.getCallback());

        // 省份代码默认 为 0（全国）
        submit.setProvinceCode(Province.PROVINCE_CODE_ALLOVER_COUNTRY);

        // add by zhengying 2017-03-28 针对用户WEB平台发送的数据，则不进行推送，直接在平台看推送记录
        if (task.getAppType() != null && AppType.DEVELOPER.getCode() == AppType.WEB.getCode()) {
            submit.setNeedPush(false);

        } else {
            PushConfig pushConfig = pushConfigService.getPushUrl(task.getUserId(), PlatformType.SEND_MESSAGE_SERVICE.getCode(), task.getCallback());

            // 推送信息为固定地址或者每次传递地址才需要推送
            if (pushConfig != null && SettingsContext.PushConfigStatus.NO.getCode() != pushConfig.getStatus()) {
                submit.setPushUrl(pushConfig.getUrl());
                submit.setNeedPush(true);
            }
        }

        // 如果黑名单手机号码为多个，则多次发送至队列
        for (String mobile : mobiles) {
            submit.setMobile(mobile);
            submit.setCmcp(CMCP.local(mobile).getCode());

            rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_SMS, RabbitConstant.MQ_SMS_MT_PACKETS_EXCEPTION, submit);
        }
    }

    /**
     * TODO 通道分包逻辑
     * 根据号码分流分省后得出的通道对应手机号码集合对应信息，进行重组子任务
     *
     * @param task
     * @param routePassage 用户运营商路由下对应的通道信息
     */
    private void subpackage(SmsMtTask task, SmsRoutePassage routePassage) {
        String mobile;
        SmsPassageAccess passage;

        for (Integer passageId : routePassage.getPassageMobiles().keySet()) {
            passage = routePassage.getPassaegAccesses().get(passageId);
            mobile = routePassage.getPassageMobiles().get(passageId);

            // 判断手机号码是否为空
            String[] mobiles = mobile.split(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
            if (mobiles.length == 0) {
                logger.error("手机号码为空 {}", mobile);
                continue;
            }

            // 通道信息为空，则子任务插入空数据
            if (passage == null) {
                joinTaskPackets(task, mobile, null);
                logger.info("通道信息为空, sid: {}, mobile:{}", task.getSid(), mobile);
                continue;
            }

            // 手机号码只有一个则直接分成一个包提交
            if (mobiles.length == 1) {
                joinTaskPackets(task, mobile, passage);
                continue;
            }

            // 如果手机号码多于分包数量，需要对手机号码分包，重组子任务
            List<String> groupMobiles = regroupMobiles(mobiles, DEFAULT_REQUEST_MOBILE_PACKAGE_SIZE);
            if (CollectionUtils.isEmpty(groupMobiles)) {
                continue;
            }

            for (String gm : groupMobiles) {
                joinTaskPackets(task, gm, passage);
            }

        }
    }

}
