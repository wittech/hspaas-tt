package com.huashi.mms.config.rabbit.listener;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.huashi.bill.pay.constant.PayContext.PaySource;
import com.huashi.bill.pay.constant.PayContext.PayType;
import com.huashi.common.settings.context.SettingsContext;
import com.huashi.common.settings.domain.Province;
import com.huashi.common.settings.domain.PushConfig;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.common.third.model.MobileCatagory;
import com.huashi.common.third.service.IMobileLocalService;
import com.huashi.common.user.domain.UserMmsConfig;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.user.service.IUserMmsConfigService;
import com.huashi.common.user.service.IUserPassageService;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.constants.CommonContext.CallbackUrlType;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode.SmsPushCode;
import com.huashi.mms.config.rabbit.AbstartRabbitListener;
import com.huashi.mms.config.rabbit.constant.RabbitConstant;
import com.huashi.mms.passage.domain.MmsPassageAccess;
import com.huashi.mms.passage.service.IMmsPassageAccessService;
import com.huashi.mms.record.domain.MmsMtMessageSubmit;
import com.huashi.mms.record.service.IMmsMtSubmitService;
import com.huashi.mms.task.constant.MmsPacketsActionPosition;
import com.huashi.mms.task.domain.MmsMtTask;
import com.huashi.mms.task.domain.MmsMtTaskPackets;
import com.huashi.mms.task.model.MmsRoutePassage;
import com.huashi.mms.task.service.IMmsMtTaskService;
import com.huashi.mms.template.constant.MmsTemplateContext.ApproveStatus;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.service.IMmsTemplateService;
import com.huashi.sms.passage.context.PassageContext;
import com.huashi.sms.passage.context.PassageContext.PassageStatus;
import com.huashi.sms.passage.context.PassageContext.RouteType;
import com.huashi.sms.settings.service.ISmsMobileBlackListService;
import com.huashi.sms.settings.service.ISmsMobileTablesService;
import com.huashi.sms.settings.service.ISmsMobileWhiteListService;
import com.huashi.sms.task.context.TaskContext.MessageSubmitStatus;
import com.huashi.sms.task.context.TaskContext.PacketsActionActor;
import com.huashi.sms.task.context.TaskContext.PacketsApproveStatus;
import com.huashi.sms.task.context.TaskContext.PacketsProcessStatus;
import com.huashi.sms.template.context.SmsTemplateContext;
import com.rabbitmq.client.Channel;

/**
 *  待消息分包处理
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月8日 下午11:35:54
 */
@Component
public class MmsWaitPacketsListener extends AbstartRabbitListener {

    @Resource
    private RabbitTemplate                        rabbitTemplate;
    @Resource
    private Jackson2JsonMessageConverter          messageConverter;

    @Reference
    private IMmsMtTaskService                     mmsMtTaskService;
    @Reference
    private IMmsMtSubmitService                   mmtMtSubmitService;
    @Reference
    private IMmsTemplateService                   mmsTemplateService;
    @Reference
    private IMmsPassageAccessService              mmsPassageAccessService;

    @Reference
    private ISmsMobileTablesService               smsMobileTablesService;
    @Reference
    private ISmsMobileBlackListService            mobileBlackListService;
    @Reference
    private ISmsMobileWhiteListService            smsMobileWhiteListService;
    @Reference
    private IMobileLocalService                   mobileLocalService;
    @Reference
    private IPushConfigService                    pushConfigService;
    @Reference
    private IUserBalanceService                   userBalanceService;
    @Reference
    private IUserPassageService                   userPassageService;
    @Reference
    private IUserMmsConfigService                 userMmsConfigService;

    /**
     * 根据当前用户ID和短信内容提取出的短信模板信息
     */
    private final ThreadLocal<MmsMessageTemplate> messageTemplateLocal = new ThreadLocal<>();

    /**
     * 当前用户传递的消息报文数据
     */
    private final ThreadLocal<MmsMtTask>          mmsMtTaskLocal       = new ThreadLocal<>();

    /**
     * 错误序列号计数器
     */
    private final AtomicInteger                   errorNo              = new AtomicInteger();

    /**
     *  正常任务处理
     *
     * @param mobileCatagory 手机号码包
     * @param routePassage 路由通道
     */
    private void doPassagePacketsFinished(MobileCatagory mobileCatagory, MmsRoutePassage routePassage) {
        try {
            // 初始化分包信息
            mmsMtTaskLocal.get().setPackets(new ArrayList<>());

            subpackage(routePassage);

            // 生成子任务，并异步发送数据
            asyncSendTask(mobileCatagory);
        } catch (Exception e) {
            logger.warn("分包逻辑失败", e);
        }
    }

    /**
     *  针对异常情况，提交子任务
     */
    private void asyncSendTask() {
        mmsMtTaskLocal.get().setPackets(null);
        asyncSendTask(null);
    }

    /**
     *  正常任务执行
     *
     * @param mobileCatagory 手机号码包
     */
    private void asyncSendTask(MobileCatagory mobileCatagory) {
        MmsMtTask task = mmsMtTaskLocal.get();
        task.setFinalBody(task.getBody());
        // 中间可能存在 去除黑名单等逻辑剔除不符合手机号码，但主任务需要保留原号码数据
        task.setMobile(task.getOriginMobile());

        // 如果错误消息为空，则认为处理状态为 正常
        task.setProcessStatus(StringUtils.isEmpty(task.getErrorMessageReport())
                              || CollectionUtils.isEmpty(task.getPackets()) ? PacketsProcessStatus.PROCESS_COMPLETE.getCode() : PacketsProcessStatus.PROCESS_EXCEPTION.getCode());

        task.setForceActions(task.getForceActionsReport().toString());

        // 如果正在分包或者分包异常，则审核状态为待审核
        task.setApproveStatus(PacketsProcessStatus.DOING.getCode() == task.getProcessStatus()
                              || PacketsProcessStatus.PROCESS_EXCEPTION.getCode() == task.getProcessStatus() ? PacketsApproveStatus.WAITING.getCode() : PacketsApproveStatus.AUTO_COMPLETE.getCode());

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

        mmsMtTaskService.save(task);

        // 分包状态="分包完成"
        if (PacketsProcessStatus.PROCESS_COMPLETE.getCode() == task.getProcessStatus()
            && PacketsApproveStatus.WAITING.getCode() != task.getApproveStatus()) {

            // 发送至待提交信息队列
            mmtMtSubmitService.sendToSubmitQueue(task.getPackets());

        }

        // 如果存在错号或者重复号码需要将 之前的计费返还到客户余额
        returnFeeToUser(task, mobileCatagory);
    }

    /**
     *  当任务中包含错号/重号 返还相应余额给用户
     *
     * @param task 主任务
     * @param mobileCatagory 手机号码包
     */
    private void returnFeeToUser(MmsMtTask task, MobileCatagory mobileCatagory) {
        if (task.getReturnFee() != null && task.getReturnFee() != 0 && mobileCatagory != null) {
            logger.info("用户ID：{} 发送彩信 存在错号：{}个，重复号码：{}个，单条计费：{}条，共扣费：{}条，共需返还{}条", task.getUserId(),
                        mobileCatagory.getFilterSize(), mobileCatagory.getRepeatSize(), task.getFee(),
                        (task.getMobiles().length - mobileCatagory.getFilterSize() - mobileCatagory.getRepeatSize())
                                                                                                       * task.getFee(),
                        task.getReturnFee());
            try {
                userBalanceService.updateBalance(task.getUserId(), task.getReturnFee(),
                                                 PlatformType.MULTIMEDIA_MESSAGE_SERVICE.getCode(),
                                                 PaySource.USER_ACCOUNT_EXCHANGE, PayType.SYSTEM, 0d, 0d, "错号或者重号返还",
                                                 false);
            } catch (Exception e) {
                logger.error("返还用户ID：{}，总短信条数：{} 失败", task.getUserId(), task.getMobiles().length * task.getFee(), e);
            }
        }
    }

    /**
     *  手机号码处理逻辑，黑名单判断/无效手机号码过滤/运营商分流
     *
     * @return 手机号码包
     */
    private MobileCatagory findOutMatchedMobiles() {
        // 转换手机号码数组
        List<String> mobiles = Lists.newArrayList(mmsMtTaskLocal.get().getMobile().split(MobileCatagory.MOBILE_SPLIT_CHARCATOR));

        // 移除上次 黑名单数据（主要针对重新分包黑名单不要重复产生记录）add by 2017-04-08
        if (StringUtils.isNotEmpty(mmsMtTaskLocal.get().getBlackMobiles())) {
            mobiles.removeAll(Arrays.asList(mmsMtTaskLocal.get().getBlackMobiles().split(MobileCatagory.MOBILE_SPLIT_CHARCATOR)));
        }

        // 黑名单手机号码
        List<String> blackMobiles = mobileBlackListService.filterBlacklistMobile(mobiles,false);
        if (CollectionUtils.isNotEmpty(blackMobiles)) {
            // 移除需要执行的手机号码
            mmsMtTaskLocal.get().setMobile(StringUtils.join(mobiles, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
            doExceptionOverWithReport(blackMobiles, SmsPushCode.SMS_MOBILE_BLACKLIST.getCode());
            mmsMtTaskLocal.get().setBlackMobiles(StringUtils.join(blackMobiles, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
            logger.warn("手机黑名单: {}", StringUtils.join(blackMobiles, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
        }

        // 经过黑名单处理后，如果可用手机号码为空则直接插入主任务
        if (CollectionUtils.isEmpty(mobiles)) {
            // 黑名单直接插入SUBMIT，自己制作伪造包BLACK状态推送给用户（推送队列）
            mmsMtTaskLocal.get().getErrorMessageReport().append(formatMessage("可用手机号码为空（为空或不符合手机号码）"));
            logger.warn("可用手机号码为空，逻辑结束");
            return null;
        }

        // 号码分流
        MobileCatagory mobileNumberResponse = mobileLocalService.doCatagory(mobiles);
        if (mobileNumberResponse == null) {
            mmsMtTaskLocal.get().getErrorMessageReport().append(formatMessage("手机号码解析错误（为空或不符合手机号码"));
            return null;
        }

        if (!mobileNumberResponse.isSuccess()) {
            mmsMtTaskLocal.get().getErrorMessageReport().append(formatMessage("手机号码分流失败"));
            logger.warn(mobileNumberResponse.getMsg());
            return null;
        }

        return mobileNumberResponse;
    }

    /**
     *  校验数据
     * 
     * @param message 队列消息
     * @return 校验结果
     */
    private boolean validate(Message message) {
        if (message == null) {
            return false;
        }

        MmsMtTask MmsMtTask = (MmsMtTask) messageConverter.fromMessage(message);
        if (MmsMtTask == null) {
            logger.error("待处理任务数据为空");
            return false;
        }

        MmsMtTask.setOriginMobile(MmsMtTask.getMobile());

        mmsMtTaskLocal.set(MmsMtTask);

        return true;
    }

    @Override
    @RabbitListener(queues = RabbitConstant.MQ_MMS_MT_WAIT_PROCESS)
    public void onMessage(Message message, Channel channel) throws Exception {
        if (!validate(message)) {
            return;
        }

        try {

            // 用户彩信配置中心数据
            UserMmsConfig mmsConfig = getMmsConfig();

            // 检查彩信发送模式下的模板信息
            if (!checkMmsModelOrBody()) {
                asyncSendTask();
                return;
            }

            // 校验同模板下手机号码是否超速，超量
            if (!isSameMobileOutOfRange(mmsConfig)) {
                asyncSendTask();
                return;
            }

            // 短信手机号码处理逻辑
            MobileCatagory mobileCatagory = findOutMatchedMobiles();
            if (mobileCatagory == null) {
                asyncSendTask();
                return;
            }

            // 获取用户路由（分省）通道信息
            MmsRoutePassage routePassage = getUserRoutePassage(mobileCatagory);

            // 通道分包逻辑
            doPassagePacketsFinished(mobileCatagory, routePassage);

        } catch (Exception e) {
            logger.error("MQ消费任务分包失败： {}", messageConverter.fromMessage(message), e);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            release();
        }
    }

    /**
     * 释放资源
     */
    private void release() {
        // 清除ThreadLocal对象，加速GC，减小内存压力
        mmsMtTaskLocal.remove();
        messageTemplateLocal.remove();
        // 置分包异常提示计数器清零
        errorNo.set(0);
    }

    /**
     *  验证数据有效性并返回用户短信配置信息
     */
    private UserMmsConfig getMmsConfig() {
        if (StringUtils.isEmpty(mmsMtTaskLocal.get().getMobile())) {
            throw new IllegalArgumentException("手机号码为空");
        }

        UserMmsConfig userMmsConfig = userMmsConfigService.getByUserId(mmsMtTaskLocal.get().getUserId());
        if (userMmsConfig == null) {
            userMmsConfig = new UserMmsConfig();
            userMmsConfig.setUserId(mmsMtTaskLocal.get().getUserId());
            userMmsConfig.setExtNumber(mmsMtTaskLocal.get().getExtNumber());

            userMmsConfigService.save(userMmsConfig);
            mmsMtTaskLocal.get().getErrorMessageReport().append(formatMessage("用户彩信配置为空，需要更新"));
        }

        return userMmsConfig;
    }

    /**
     *  保存子任务
     *
     * @param mobile 手机号码
     * @param passageAccess 可用通道信息
     */
    private void joinTaskPackets(String mobile, MmsPassageAccess passageAccess) {
        MmsMtTask task = mmsMtTaskLocal.get();

        MmsMtTaskPackets mmsMtTaskPackets = new MmsMtTaskPackets();
        mmsMtTaskPackets.setSid(task.getSid());
        mmsMtTaskPackets.setMobile(mobile);

        // 本次通道对应的运营商和省份代码
        if (passageAccess != null) {
            mmsMtTaskPackets.setCmcp(passageAccess.getCmcp());
            mmsMtTaskPackets.setProvinceCode(passageAccess.getProvinceCode());
        } else {
            mmsMtTaskPackets.setCmcp(CMCP.local(mobile).getCode());
        }

        mmsMtTaskPackets.setTitle(task.getTitle());
        mmsMtTaskPackets.setBody(task.getBody());
        mmsMtTaskPackets.setModelId(task.getModelId());

        mmsMtTaskPackets.setMobileSize(mobile.split(MobileCatagory.MOBILE_SPLIT_CHARCATOR).length);

        if (passageAccess != null) {
            mmsMtTaskPackets.setPassageId(passageAccess.getPassageId());
            mmsMtTaskPackets.setPassageCode(passageAccess.getPassageCode());
            mmsMtTaskPackets.setFinalPassageId(passageAccess.getPassageId());
            mmsMtTaskPackets.setPassageProtocol(passageAccess.getProtocol());
            mmsMtTaskPackets.setPassageUrl(passageAccess.getUrl());
            mmsMtTaskPackets.setPassageParameter(passageAccess.getParams());
            mmsMtTaskPackets.setResultFormat(passageAccess.getResultFormat());
            mmsMtTaskPackets.setPosition(passageAccess.getPosition());
            mmsMtTaskPackets.setSuccessCode(passageAccess.getSuccessCode());
            mmsMtTaskPackets.setPassageSpeed(passageAccess.getPacketsSize());
        }

        mmsMtTaskPackets.setRemark(task.getErrorMessageReport().toString());
        mmsMtTaskPackets.setForceActions(task.getForceActionsReport().toString());
        mmsMtTaskPackets.setRetryTimes(0);
        mmsMtTaskPackets.setCreateTime(new Date());

        // 如果账号是华时系统通知账号则直接通过
        // boolean isAvaiable = isHsAdmin(task.getAppKey());

        // 短信模板ID为空，短信包含敏感词及其他错误信息，短信通道为空 均至状态为 待人工处理
        if (passageAccess == null || StringUtils.isNotEmpty(mmsMtTaskPackets.getRemark())
            || messageTemplateLocal.get() == null || messageTemplateLocal.get().getId() == null) {
            mmsMtTaskPackets.setStatus(PacketsApproveStatus.WAITING.getCode());
        } else {
            mmsMtTaskPackets.setStatus(PacketsApproveStatus.AUTO_COMPLETE.getCode());
        }

        // 用户自定义内容，一般为他方子平台的开发者ID（渠道），用于标识
        mmsMtTaskPackets.setAttach(task.getAttach());
        // 设置用户自设置的扩展号码
        mmsMtTaskPackets.setExtNumber(task.getExtNumber());
        mmsMtTaskPackets.setCallback(task.getCallback());
        mmsMtTaskPackets.setUserId(task.getUserId());

        // 追加子任务
        task.getPackets().add(mmsMtTaskPackets);
    }

    /**
     *  追加错误信息
     */
    private String formatMessage(String message) {
        return String.format("%d)%s;", errorNo.incrementAndGet(), message);
    }

    /**
     * 拼接可操作动作代码
     * 
     * @param index 错误码下标
     * @param forceActions 错误码信息
     */
    private void refillForceActions(int index, StringBuilder forceActions) {
        // 异常分包情况下允许的操作，如000,010，第一位:未报备模板，第二位：敏感词，第三位：通道不可用
        char[] actions = forceActions.toString().toCharArray();

        actions[index] = PacketsActionActor.BROKEN.getActor();

        forceActions.setLength(0);
        forceActions.append(String.valueOf(actions));
    }

    /**
     *  获取短信路由类型 如果路由类型未确定，则按默认路由进行
     *
     * @return 路由类型
     */
    private Integer getMessageRouteType() {
        if (messageTemplateLocal.get() == null || messageTemplateLocal.get().getRouteType() == null) {
            return RouteType.DEFAULT.getValue();
        }

        return messageTemplateLocal.get().getRouteType();
    }

    /**
     *  获取运营商手机号码信息（手机号码分省分流）
     *
     * @param cmcp 运营商标识
     * @param mobileCatagory 手机号码包
     * @return 手机号码映射
     */
    private Map<Integer, String> getCmcpMobileNumbers(CMCP cmcp, MobileCatagory mobileCatagory) {
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
    private static final CMCP[] CMCPS = { CMCP.CHINA_MOBILE, CMCP.CHINA_TELECOM, CMCP.CHINA_UNICOM };

    /**
     * 根据运营商和路由通道寻找具体的通道信息
     *
     * @param mobileCatagory 分省后手机号码组
     * @return 路由通道
     */
    private MmsRoutePassage getUserRoutePassage(MobileCatagory mobileCatagory) {
        MmsRoutePassage routePassage = new MmsRoutePassage();
        routePassage.setUserId(mmsMtTaskLocal.get().getUserId());

        Integer routeType = getMessageRouteType();
        Map<Integer, String> provinceCmcpMobileNumbers;
        for (CMCP cmcp : CMCPS) {
            provinceCmcpMobileNumbers = getCmcpMobileNumbers(cmcp, mobileCatagory);
            if (MapUtils.isEmpty(provinceCmcpMobileNumbers)) {
                continue;
            }

            Set<Integer> provinceCodes = provinceCmcpMobileNumbers.keySet();
            for (Integer provinceCode : provinceCodes) {
                // 获取可用通道
                MmsPassageAccess passageAccess = getPassageAccess(routePassage.getUserId(), routeType, cmcp.getCode(),
                                                                  provinceCode);
                if (passageAccess != null) {
                    routePassage.addPassageMobilesMapping(passageAccess.getPassageId(),
                                                          provinceCmcpMobileNumbers.get(provinceCode));
                    routePassage.getPassaegAccesses().put(passageAccess.getPassageId(), passageAccess);
                } else {
                    // edit by 20180414 如果未找到相关CMCP的通道及全国通道，则分包异常
                    mmsMtTaskLocal.get().getErrorMessageReport().append(formatMessage("通道不可用"));
                    refillForceActions(MmsPacketsActionPosition.PASSAGE_NOT_AVAIABLE.getPosition(),
                                       mmsMtTaskLocal.get().getForceActionsReport());

                    // 如果没有可用通道，则直接将省份手机号码进行分配异常通道
                    routePassage.addPassageMobilesMapping(PassageContext.EXCEPTION_PASSAGE_ID,
                                                          provinceCmcpMobileNumbers.get(provinceCode));
                }
            }
        }
        return routePassage;
    }

    /**
     *  获取可用通道信息，如果当前分省后的省份通道不可用，则尝试用全国通道，如果均没有，则分包失败
     * 
     * @param userId 用户编号
     * @param routeType 路由类型
     * @param cmcp 运营商
     * @param provinceCode 省份代码
     * @return 可用通道
     */
    private MmsPassageAccess getPassageAccess(int userId, int routeType, int cmcp, int provinceCode) {
        MmsPassageAccess passageAccess = mmsPassageAccessService.get(userId, routeType, cmcp, provinceCode);
        if (isPassageAccessAvaiable(passageAccess)) {
            return passageAccess;
        }

        passageAccess = mmsPassageAccessService.get(userId, routeType, cmcp, Province.PROVINCE_CODE_ALLOVER_COUNTRY);
        if (isPassageAccessAvaiable(passageAccess)) {
            return passageAccess;
        }

        return null;
    }

    /**
     *  验证通道是否可用
     *
     * @param access 可用通道
     * @return true,false
     */
    private boolean isPassageAccessAvaiable(MmsPassageAccess access) {
        return access != null && access.getStatus() != null && access.getStatus() == PassageStatus.ACTIVE.getValue();
    }

    /**
     *  是否是模板发送模式
     * 
     * @return true,false
     */
    private boolean isMessageByModelSend() {
        return mmsMtTaskLocal.get().getIsModelSend() || StringUtils.isNotEmpty(mmsMtTaskLocal.get().getModelId());
    }

    /**
     *  检验发送模式相关
     * 
     * @return true,false
     */
    private boolean checkMmsModelOrBody() {
        MmsMessageTemplate template;
        if (isMessageByModelSend()) {
            template = mmsTemplateService.getByModelId(mmsMtTaskLocal.get().getModelId());
            if (template == null) {
                mmsMtTaskLocal.get().getErrorMessageReport().append(formatMessage(MmsPacketsActionPosition.TEMPLATE_MISSED.getTitle()));
                refillForceActions(MmsPacketsActionPosition.TEMPLATE_MISSED.getPosition(),
                                   mmsMtTaskLocal.get().getForceActionsReport());

                return false;
            } else if (ApproveStatus.SUCCESS.getValue() != template.getStatus()) {
                mmsMtTaskLocal.get().getErrorMessageReport().append(formatMessage(MmsPacketsActionPosition.TEMPLATE_BLOCKED.getTitle()));
                refillForceActions(MmsPacketsActionPosition.TEMPLATE_BLOCKED.getPosition(),
                                   mmsMtTaskLocal.get().getForceActionsReport());

                return false;
            }

            mmsMtTaskLocal.get().setTitle(template.getTitle());
            messageTemplateLocal.set(template);
        } else {
            mmsMtTaskLocal.get().getErrorMessageReport().append(formatMessage("自定义彩信发送审核"));
        }

        return true;
    }

    /**
     *  根据模板限速/限量值判断是否放行
     * 
     * @return true,false
     */
    private boolean checkPassedByThreshold() {
        // 如果提交频率为0并且一天内上限大于等于9999则不限制提交任何（也无需记录用户访问轨迹） edit by 20170813
        return messageTemplateLocal.get().getSubmitInterval() == 0 && messageTemplateLocal.get().getLimitTimes() >= 9999;
    }

    /**
     * 判断用户手机号码是超限/超速
     *
     * @param mmsConfig 彩信模板
     * @return true,false
     */
    private boolean isSameMobileOutOfRange(UserMmsConfig mmsConfig) {
        fillTemplateAttributes(mmsConfig);

        // 免校验放行 edit by 20180527
        if (checkPassedByThreshold()) {
            return true;
        }

        // 根据userId获取白名单手机号码数据
        Set<String> whiteMobiles = smsMobileWhiteListService.getByUserId(mmsMtTaskLocal.get().getUserId());

        // 转换手机号码数组
        List<String> mobiles = new ArrayList<>(Arrays.asList(mmsMtTaskLocal.get().getMobile().split(MobileCatagory.MOBILE_SPLIT_CHARCATOR)));

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
            int beyondExpected = smsMobileTablesService.checkMobileIsBeyondExpected(mmsMtTaskLocal.get().getUserId(),
                                                                                    messageTemplateLocal.get().getId(),
                                                                                    mobile,
                                                                                    messageTemplateLocal.get().getSubmitInterval(),
                                                                                    messageTemplateLocal.get().getLimitTimes());

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
            mmsMtTaskLocal.get().setMobile(StringUtils.join(mobiles, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
            doExceptionOverWithReport(benyondSpeedList,
                                      SmsPushCode.SMS_SAME_MOBILE_NUM_SEND_BY_HIGN_FREQUENCY.getCode());
            logger.warn("手机号码超速 {}", StringUtils.join(benyondSpeedList, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
        }

        if (CollectionUtils.isNotEmpty(benyondTimesList)) {
            // 移除需要执行的手机号码
            mobiles.removeAll(benyondTimesList);
            mmsMtTaskLocal.get().setMobile(StringUtils.join(mobiles, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
            doExceptionOverWithReport(benyondTimesList,
                                      SmsPushCode.SMS_SAME_MOBILE_NUM_BEYOND_LIMIT_IN_ONE_DAY.getCode());
            logger.warn("手机号码超量 {}", StringUtils.join(benyondSpeedList, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
        }

        return CollectionUtils.isNotEmpty(mobiles);
    }

    /**
     *  设置用户归属的彩信模板信息
     *
     * @param mmsConfig 彩信模板
     */
    private void fillTemplateAttributes(UserMmsConfig mmsConfig) {
        MmsMessageTemplate template = messageTemplateLocal.get();
        if (template == null) {
            template = new MmsMessageTemplate();
            template.setLimitTimes(mmsConfig.getLimitTimes());
            template.setSubmitInterval(mmsConfig.getSubmitInterval());
        } else {
            if (template.getLimitTimes() == null) {
                template.setLimitTimes(SmsTemplateContext.DEFAULT_LIMIT_TIMES);
            }

            if (template.getSubmitInterval() == null) {
                template.setSubmitInterval(SmsTemplateContext.DEFAULT_SUBMIT_INTERVAL);
            }

        }
        messageTemplateLocal.set(template);
    }

    /**
     *  执行异常结束逻辑(制造状态伪造包，需要判断是否需要状态报告)
     *
     * @param mobiles 手机号码集合
     * @param remark 备注
     */
    private void doExceptionOverWithReport(List<String> mobiles, String remark) {
        MmsMtTask task = mmsMtTaskLocal.get();
        MmsMtMessageSubmit submit = new MmsMtMessageSubmit();
        submit.setUserId(task.getUserId());
        submit.setSid(task.getSid());
        if (messageTemplateLocal.get() != null) {
            submit.setTemplateId(messageTemplateLocal.get().getId());
        }

        submit.setTitle(task.getTitle());
        submit.setContent(task.getBody());
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
        if (task.getAppType() != null && AppType.DEVELOPER.getCode() != task.getAppType()) {
            submit.setNeedPush(false);

        } else {
            PushConfig pushConfig = pushConfigService.getPushUrl(task.getUserId(), CallbackUrlType.MMS_STATUS.getCode(),
                                                                 task.getCallback());

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

            rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_MMS, RabbitConstant.MQ_MMS_MT_PACKETS_EXCEPTION,
                                          submit);
        }
    }

    /**
     *  通道分包逻辑 根据号码分流分省后得出的通道对应手机号码集合对应信息，进行重组子任务
     *
     * @param routePassage 用户运营商路由下对应的通道信息
     */
    private void subpackage(MmsRoutePassage routePassage) {
        String mobile;
        MmsPassageAccess passage;

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
                joinTaskPackets(mobile, null);
                logger.info("通道信息为空, sid: {}, mobile:{}", mmsMtTaskLocal.get().getSid(), mobile);
                continue;
            }

            // 手机号码只有一个则直接分成一个包提交
            if (mobiles.length == 1) {
                joinTaskPackets(mobile, passage);
                continue;
            }

            // 如果手机号码多于分包数量，需要对手机号码分包，重组子任务
            List<String> groupMobiles = regroupMobiles(mobiles, DEFAULT_REQUEST_MOBILE_PACKAGE_SIZE);
            if (CollectionUtils.isEmpty(groupMobiles)) {
                continue;
            }

            for (String gm : groupMobiles) {
                joinTaskPackets(gm, passage);
            }

        }
    }

}
