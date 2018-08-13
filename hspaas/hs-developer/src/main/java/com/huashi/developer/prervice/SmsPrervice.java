package com.huashi.developer.prervice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.third.model.MobileCatagory;
import com.huashi.common.user.context.UserBalanceConstant;
import com.huashi.common.user.context.UserContext.UserStatus;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.user.service.IUserDeveloperService;
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
    @Reference
    protected IUserDeveloperService   userDeveloperService;

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
            String mobileParams = getValue("mobilepara", paramsMap);
            // 签名
            String sign = getValue("sign", paramsMap);

            // 1、判断是否必填参数为空
            checkIfNecessary(appId, modeId, mobileParams, sign);

            // 若不传，视为立即发送；若传值，则会在sendTime表示的时间点开始发送。格式为：yyyyMMddHHmmss如：20161123143022
            String sendTime = getValue("sendTime", paramsMap);
            if (StringUtils.isNotEmpty(sendTime)) {
                throw new ValidateException(ApiReponseCode.SNED_TIME_INVALID);
            }

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
            verifySign(appId, userDeveloper.getAppSecret(), mobileParams, sign);
            
            List<String> paramCol = JSON.parseObject(mobileParams, new TypeReference<List<String>>(){});
            if(CollectionUtils.isEmpty(paramCol)) {
                throw new ValidateException(ApiReponseCode.MOBILE_INVALID);
            }
            
            List<SmsMtTask> tasks = new ArrayList<SmsMtTask>();
            for(String param : paramCol) {
                SmsMtTask target = new SmsMtTask();
                BeanUtils.copyProperties(task, target);
                
                Map<String, String> paramMap = JSON.parseObject(param, new TypeReference<Map<String, String>>(){});
                // 手机号码
                String mobile =  paramMap.get("mobile");
                
                String parameter = paramMap.get("parameter");
                
                MessageTemplate messageTemplate = smsTemplatePrervice.getById(Long.valueOf(modeId), parameter);
                target.setContent(messageTemplate.getContent());
                target.setMessageTemplateId(messageTemplate.getId());

                if (StringUtils.isEmpty(mobile) || mobile.trim().length() < 11) {
                    throw new ValidateException(ApiReponseCode.MOBILE_INVALID);
                }

                if (mobile.split(MobileCatagory.MOBILE_SPLIT_CHARCATOR).length > 500) {
                    throw new ValidateException(ApiReponseCode.MOBILES_OUT_RANGE);
                }

                target.setMobile(mobile);

                // 判断余额是否够用
                checkBalanceAvaiable(target);

                // 状态通知地址
                String notifyUrl = getValue("notifyUrl", paramsMap);
                // 备用字段
                String userParams = getValue("userParams", paramsMap);
                target.setCallback(notifyUrl);
                target.setAttach(userParams);
                target.setAppType(AppType.DEVELOPER.getCode());
                target.setIp(ip);

                // 接入号
                // String fromNo = getValue("fromNo", paramsMap);
                // target.setExtNumber(fromNo);

                tasks.add(target);
            }
            
            // 遍历所有通过判断的任务信息，并发送至短信中心
            List<JSONObject> rets = new ArrayList<>();
            for(SmsMtTask tt : tasks) {
                long sid = joinTask2Queue(tt);
                if (sid != 0L) {
                    tt.setSid(sid);
                    rets.addAll(formatResonse(tt));
                }
            }
            
            // 如果有效的回执信息不为空，则表明数据正常
            if(CollectionUtils.isNotEmpty(rets)) {
                return new SmsApiResponse(OpenApiCode.SUCCESS, "", rets);
            }

            return new SmsApiResponse(ApiReponseCode.SERVER_EXCEPTION);
        } catch (Exception e) {
            if (e instanceof ValidateException) {
                ValidateException ve = (ValidateException) e;
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
            targetSign = sign(targetSign, false);
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
     * @param mobileParams
     * @param sign
     * @throws ValidateException
     */
    private void checkIfNecessary(String appId, String modeId, String mobileParams, String sign) throws ValidateException {
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(modeId) || StringUtils.isEmpty(mobileParams)
            || StringUtils.isEmpty(sign)) {
            throw new ValidateException(ApiReponseCode.REQUEST_EXCEPTION);
        }
    }

    /**
     * TODO 组装成功回执信息
     * 
     * @param task
     * @return
     */
    private List<JSONObject> formatResonse(SmsMtTask task) {
        List<JSONObject> rets = new ArrayList<>();
        for (String mobile : task.getMobiles()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("respCode", OpenApiCode.SUCCESS);
            jsonObject.put("mobile", mobile);
            jsonObject.put("sendId", task.getSid().toString());

            rets.add(jsonObject);
        }
        return rets;
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
