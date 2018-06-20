package com.huashi.developer.prervice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.common.third.model.MobileCatagory;
import com.huashi.common.user.context.UserBalanceConstant;
import com.huashi.common.user.context.UserContext.UserStatus;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.util.IdGenerator;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode;
import com.huashi.constants.OpenApiCode.ApiReponseCode;
import com.huashi.developer.constant.RabbitConstant;
import com.huashi.developer.exception.ValidateException;
import com.huashi.developer.response.sms.SmsApiResponse;
import com.huashi.sms.record.service.ISmsApiFaildRecordService;
import com.huashi.sms.task.context.TaskContext.TaskSubmitType;
import com.huashi.sms.task.domain.SmsMtTask;
import com.huashi.sms.task.exception.QueueProcessException;
import com.huashi.sms.template.domain.MessageTemplate;

/**
 * TODO 短信前置服务
 *
 * @author zhengying
 * @version V1.0
 * @date 2017年4月6日 下午1:29:15
 */
@Service
public class SmsPrervice extends AbstractPrervice {

    private final Logger              logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IdGenerator               idGenerator;

    @Autowired
    private JmsMessagingTemplate      jmsMessagingTemplate;
    @Reference
    private IUserBalanceService       userBalanceService;
    @Reference
    private ISmsApiFaildRecordService smsApiFaildRecordService;

    @Autowired
    private SmsTemplatePrervice       smsTemplatePrervice;

    /**
     * TODO 发送短信信息
     *
     * @param model
     * @return
     */
    public SmsApiResponse sendMessage(Map<String, String[]> paramsMap, String ip) {
        try {
            // 判断参数是否正确
            if (MapUtils.isEmpty(paramsMap)) {
                throw new ValidateException(ApiReponseCode.REQUEST_EXCEPTION);
            }

            // 用户ID
            String appId = getValue("appId", paramsMap);
            // 模板ID
            String modeId = getValue("modeId", paramsMap);
            // 手机号码，不能超狗500个
            String mobile = getValue("mobile", paramsMap);
            // 签名
            String sign = getValue("sign", paramsMap);

            // 1、判断是否必填参数为空
            checkIfNecessary(appId, modeId, mobile, sign);

            SmsMtTask task = new SmsMtTask();

            UserDeveloper userDeveloper = userDeveloperService.getByUserId(Integer.parseInt(appId));
            // 应用不存在
            if (userDeveloper == null) {
                throw new ValidateException(ApiReponseCode.APPKEY_INVALID);
            }

            // 客户账号停用
            if (UserStatus.YES.getValue() != userDeveloper.getStatus()) {
                throw new ValidateException(ApiReponseCode.API_DEVELOPER_INVALID);
            }

            task.setUserId(Integer.parseInt(appId));

            // 校验签名是否正确
            verifySign(appId, userDeveloper.getAppSecret(), mobile, sign);

            // 变量内容
            String vars = getValue("vars", paramsMap);

            MessageTemplate messageTemplate = smsTemplatePrervice.getById(Long.valueOf(modeId), vars);
            task.setContent(messageTemplate.getContent());
            task.setMessageTemplateId(messageTemplate.getId());

            if (StringUtils.isEmpty(mobile) || mobile.trim().length() < 11) {
                throw new ValidateException(ApiReponseCode.MOBILE_INVALID);
            }

            if (mobile.split(MobileCatagory.MOBILE_SPLIT_CHARCATOR).length > 500) {
                throw new ValidateException(ApiReponseCode.MOBILES_OUT_RANGE);
            }

            task.setMobile(mobile);

            // 判断余额是否够用
            checkBalanceAvaiable(task);

            // 若不传，视为立即发送；若传值，则会在sendTime表示的时间点开始发送。格式为：yyyyMMddHHmmss如：20161123143022
            // String sendTime = getValue("sendTime", paramsMap);

            // 状态通知地址
            String notifyUrl = getValue("notifyUrl", paramsMap);
            // 备用字段
            String userParams = getValue("userParams", paramsMap);
            task.setCallback(notifyUrl);
            task.setAttach(userParams);
            task.setAppType(AppType.DEVELOPER.getCode());
            task.setIp(ip);

            // 接入号
            // String fromNo = getValue("fromNo", paramsMap);
            // task.setExtNumber(fromNo);

            long sid = joinTask2Queue(task);
            if (sid != 0L) {
                task.setSid(sid);
                return formatResonse(task);
            }

            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION); 
        } catch (Exception e) {
            if(e instanceof ValidateException) {
                ValidateException ve = (ValidateException)e;
                return new SmsApiResponse(ve.getApiReponseCode().getCode(), ve.getApiReponseCode().getMessage());
            }
            
            logger.error("发送个短信: [{}] 错误", JSON.toJSONString(paramsMap), e);
            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);
        }
    }

    /**
     * TODO 校验数据拼接签名是否匹配
     * 
     * @param appId
     * @param appKey
     * @param mobile
     * @param sign
     * @throws ValidateException
     */
    private void verifySign(String appId, String appKey, String mobile, String sign) throws ValidateException {
        String targetSign = appKey + appId + mobile;
        try {
            targetSign = sign(targetSign);
            if (!targetSign.equals(sign)) {
                throw new ValidateException(ApiReponseCode.AUTHENTICATION_FAILED);
            }

        } catch (Exception e) {
            logger.error("校验签名失败", e);
            throw new ValidateException(ApiReponseCode.AUTHENTICATION_FAILED);
        }
    }

    /**
     * TODO 校验短信余额是否够用
     * 
     * @param task
     * @throws ValidateException
     */
    private void checkBalanceAvaiable(SmsMtTask task) throws ValidateException {
        // 获取本次短信内容计费条数
        int fee = userBalanceService.calculateSmsAmount(task.getUserId(), task.getContent());
        if (UserBalanceConstant.CONTENT_WORDS_EXCEPTION_COUNT_FEE == fee) {
            throw new ValidateException(ApiReponseCode.BALANCE_NOT_ENOUGH);
        }

        // 计费总条数
        int totalFee = fee * task.getMobile().split(MobileCatagory.MOBILE_SPLIT_CHARCATOR).length;

        // 此处需加入是否为后付款，如果为后付则不需判断余额
        // f.用户余额不足（通过计费微服务判断，结合4.1.6中的用户计费规则）
        boolean balanceEnough = userBalanceService.isBalanceEnough(task.getUserId(), PlatformType.SEND_MESSAGE_SERVICE,
                                                                   (double) totalFee);
        if (!balanceEnough) {
            throw new ValidateException(ApiReponseCode.BALANCE_NOT_ENOUGH);
        }

        task.setFee(fee);
        task.setTotalFee(totalFee);
    }

    /**
     * TODO 判断必填项是否为空
     * 
     * @param appId
     * @param modeId
     * @param mobile
     * @param sign
     * @throws ValidateException
     */
    private void checkIfNecessary(String appId, String modeId, String mobile, String sign) throws ValidateException {
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(modeId) || StringUtils.isEmpty(mobile)
            || StringUtils.isEmpty(sign)) {
            throw new ValidateException(ApiReponseCode.REQUEST_EXCEPTION);
        }
    }

    /**
     * 
       * TODO 组装成功回执信息
       * 
       * @param task
       * @return
     */
    private SmsApiResponse formatResonse(SmsMtTask task) {
        List<JSONObject> rets = new ArrayList<>();
        for(String mobile : task.getMobiles()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("respCode", OpenApiCode.SUCCESS);
            jsonObject.put("mobile", mobile);
            jsonObject.put("sendId", task.getSid().toString());
            
            rets.add(jsonObject);
        }
        return new SmsApiResponse(OpenApiCode.SUCCESS, "", rets);
    }

    /**
     * TODO 提交任务到队列
     *
     * @param task
     * @return
     */
    private long joinTask2Queue(SmsMtTask task) {
        try {
            // 更新用户余额
            boolean isSuccess = userBalanceService.deductBalance(task.getUserId(), -task.getTotalFee(),
                                                                 PlatformType.SEND_MESSAGE_SERVICE.getCode(), null);
            if (!isSuccess) {
                logger.error("用户ID:{} 扣除短信余额：{} 失败", task.getUserId(), task.getTotalFee());
                throw new QueueProcessException("发送短信扣除短信余额失败");
            }

            task.setSid(idGenerator.generate());
            task.setCreateTime(new Date());
            task.setCreateUnixtime(task.getCreateTime().getTime());

            // 插入TASK任务（异步）

            // 判断队列的优先级别
            // if(WordsPriority.L10.getLevel() == priority) {
            // stringRedisTemplate.opsForList().rightPush(SmsConstant.RED_TASK_PERSISTENCE_HIGH,
            // JSON.toJSONString(task));
            // } else {
            // if(WordsPriority.L1.getLevel() == priority)
            // stringRedisTemplate.opsForList().rightPush(SmsConstant.RED_TASK_PERSISTENCE_LOW,
            // JSON.toJSONString(task));
            // if(WordsPriority.L1.getLevel() == priority)
            // stringRedisTemplate.opsForList().leftPush(SmsConstant.RED_TASK_PERSISTENCE_LOW,
            // JSON.toJSONString(task));
            // }

            String queueName = RabbitConstant.MQ_SMS_MT_WAIT_PROCESS;
            if (TaskSubmitType.POINT_TO_POINT.getCode() == task.getSubmitType()
                || TaskSubmitType.TEMPLATE_POINT_TO_POINT.getCode() == task.getSubmitType()) {
                queueName = RabbitConstant.MQ_SMS_MT_P2P_WAIT_PROCESS;
            }

            // 发送至activeMq
            jmsMessagingTemplate.convertAndSend(queueName, task);

            return task.getSid();
        } catch (Exception e) {
            logger.error("发送短信队列失败", e);
            throw new QueueProcessException("发送短信队列失败");
        }
    }

}
