package com.huashi.developer.prervice;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huashi.common.user.domain.UserBalance;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.util.IdGenerator;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode.CommonApiCode;
import com.huashi.constants.OpenApiCode.MmsApiCode;
import com.huashi.developer.constant.RabbitConstant;
import com.huashi.developer.request.mms.MmsModelApplyRequest;
import com.huashi.developer.request.mms.MmsSendByModelRequest;
import com.huashi.developer.request.mms.MmsSendRequest;
import com.huashi.developer.response.mms.MmsBalanceResponse;
import com.huashi.developer.response.mms.MmsModelApplyResponse;
import com.huashi.developer.response.mms.MmsSendResponse;
import com.huashi.mms.task.domain.MmsMtTask;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.exception.ModelApplyException;
import com.huashi.mms.template.service.IMmsTemplateService;
import com.huashi.sms.task.exception.QueueProcessException;

/**
 * 彩信前置服务
 *
 * @author zhengying
 * @version V1.0
 * @date 2019年3月3日 下午5:18:09
 */
@Service
public class MmsPrervice {

    private final Logger        logger                = LoggerFactory.getLogger(getClass());

    @Autowired
    private IdGenerator         idGenerator;
    @Reference
    private IUserBalanceService userBalanceService;
    @Reference
    private IMmsTemplateService mmsTemplateService;
    @Resource(name = "mmsRabbitTemplate")
    private RabbitTemplate      mmsRabbitTemplate;

    /**
     * 单条默认计费值
     */
    public static final Integer DEFAULT_FEE_IN_SINGLE = 1;

    /**
     * 模板发送
     *
     * @param mmsSendByModelRequest 模板蔡雄请求报文
     * @return 响应信息
     */
    public MmsSendResponse sendMessageByModel(MmsSendByModelRequest mmsSendByModelRequest) {
        MmsMtTask task = new MmsMtTask();

        BeanUtils.copyProperties(mmsSendByModelRequest, task);
        task.setAppType(mmsSendByModelRequest.getAppType());
        task.setIsModelSend(true);

        try {
            long sid = joinTask2Queue(task);
            if (sid != 0L) {
                return new MmsSendResponse(mmsSendByModelRequest.getTotalFee(), sid);
            }

        } catch (QueueProcessException e) {
            logger.error("发送彩信至队列错误， {}", e);
        }

        return new MmsSendResponse(CommonApiCode.COMMON_SERVER_EXCEPTION);
    }

    /**
     * 自定义内容发送
     *
     * @param msSendRequest 普通彩信请求
     * @return 响应信息
     */
    public MmsSendResponse sendMessage(MmsSendRequest msSendRequest) {
        MmsMtTask task = new MmsMtTask();

        BeanUtils.copyProperties(msSendRequest, task);
        task.setAppType(msSendRequest.getAppType());

        // body中最终存的是OSS的相关关联结构体

        task.setIsModelSend(false);

        try {

            long sid = joinTask2Queue(task);
            if (sid != 0L) {
                return new MmsSendResponse(msSendRequest.getTotalFee(), sid);
            }

        } catch (QueueProcessException e) {
            logger.error("发送彩信至队列错误， {}", e);
        }

        return new MmsSendResponse(CommonApiCode.COMMON_SERVER_EXCEPTION);
    }

    /**
     * 模板报备
     * 
     * @param mmsModelApplyRequest 彩信模板申请
     * @return 响应信息
     */
    public MmsModelApplyResponse applyModel(MmsModelApplyRequest mmsModelApplyRequest) {
        try {
            MmsMessageTemplate template = new MmsMessageTemplate();

            BeanUtils.copyProperties(mmsModelApplyRequest, template);
            template.setAppType(mmsModelApplyRequest.getAppType());
            template.setBodies(mmsModelApplyRequest.getMmsMessageTemplateBodies());

            String modelId = mmsTemplateService.save(template);
            if (StringUtils.isBlank(modelId)) {
                return new MmsModelApplyResponse(MmsApiCode.MMS_MODEL_APPLY_FAILED.getCode(),
                                            MmsApiCode.MMS_MODEL_APPLY_FAILED.getMessage());
            }

            return new MmsModelApplyResponse(CommonApiCode.COMMON_SUCCESS.getCode(),
                                        CommonApiCode.COMMON_SUCCESS.getMessage(), modelId);

        } catch (ModelApplyException e) {
            logger.error("报备模板失败， {}", e);
            return new MmsModelApplyResponse(MmsApiCode.MMS_MODEL_APPLY_FAILED.getCode(),
                                        MmsApiCode.MMS_MODEL_APPLY_FAILED.getMessage());
        }

    }

    /**
     * 获取短余额信息
     *
     * @return 用户余额响应
     */
    public MmsBalanceResponse getBalance(int userId) {
        UserBalance userBalance = userBalanceService.getByUserId(userId, PlatformType.MULTIMEDIA_MESSAGE_SERVICE);
        if (userBalance == null) {
            logger.error("用户ID：{} 查询彩信余额失败，用户余额数据为空", userId);
            return new MmsBalanceResponse(CommonApiCode.COMMON_SERVER_EXCEPTION.getCode());
        }

        return new MmsBalanceResponse(CommonApiCode.COMMON_SUCCESS.getCode(), userBalance.getBalance().intValue(),
                                      userBalance.getPayType());
    }

    /**
     * 提交任务到队列
     *
     * @param task 彩信任务
     * @return 消息ID
     */
    private long joinTask2Queue(MmsMtTask task) {
        try {
            // 更新用户余额
            boolean isSuccess = userBalanceService.deductBalance(task.getUserId(), -task.getTotalFee(),
                                                                 PlatformType.MULTIMEDIA_MESSAGE_SERVICE.getCode(),
                                                                 "developer call");
            if (!isSuccess) {
                logger.error("用户ID: [" + task.getUserId() + "] 扣除短信余额[ " + task.getTotalFee() + "] 失败");
                throw new QueueProcessException("发送彩信扣除余额失败");
            }

            task.setSid(idGenerator.generate());
            task.setCreateTime(new Date());
            task.setCreateUnixtime(task.getCreateTime().getTime());

            mmsRabbitTemplate.convertAndSend(RabbitConstant.MQ_MMS_MT_WAIT_PROCESS, task,
                                             new CorrelationData(task.getSid().toString()));

            return task.getSid();
        } catch (Exception e) {
            logger.error("发送彩信队列失败", e);
            throw new QueueProcessException("发送彩信队列失败");
        }
    }

}
