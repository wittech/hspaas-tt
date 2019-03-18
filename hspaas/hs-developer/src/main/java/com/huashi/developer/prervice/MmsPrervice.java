package com.huashi.developer.prervice;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.user.domain.UserBalance;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.util.IdGenerator;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode.CommonApiCode;
import com.huashi.developer.constant.RabbitConstant;
import com.huashi.developer.model.mms.MmsCustomContentSendRequest;
import com.huashi.developer.model.mms.MmsModelSendRequest;
import com.huashi.developer.response.mms.MmsBalanceResponse;
import com.huashi.developer.response.mms.MmsSendResponse;
import com.huashi.mms.task.domain.MmsMtTask;
import com.huashi.sms.task.exception.QueueProcessException;

/**
 * TODO 彩信前置服务
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
    @Resource
    private RabbitTemplate      rabbitTemplate;
    @Reference
    private IUserBalanceService userBalanceService;

    /**
     * 单条默认计费值
     */
    public static final Integer DEFAULT_FEE_IN_SINGLE = 1;

    /**
     * TODO 模板发送
     *
     * @param model
     * @return
     */
    public MmsSendResponse sendMessage(MmsModelSendRequest smsSendRequest) {
        MmsMtTask task = new MmsMtTask();

        BeanUtils.copyProperties(smsSendRequest, task);
        task.setAppType(smsSendRequest.getAppType());
        task.setIsModelSend(true);

        try {

            long sid = joinTask2Queue(task);
            if (sid != 0L) {
                return new MmsSendResponse(smsSendRequest.getTotalFee(), sid);
            }

        } catch (QueueProcessException e) {
            logger.error("发送短信至队列错误， {}", e);
        }

        return new MmsSendResponse(CommonApiCode.COMMON_SERVER_EXCEPTION);
    }

    /**
     * TODO 自定义内容发送
     * 
     * @param mmsCustomContentSendRequest
     * @return
     */
    public MmsSendResponse sendMessage(MmsCustomContentSendRequest mmsCustomContentSendRequest) {
        MmsMtTask task = new MmsMtTask();

        BeanUtils.copyProperties(mmsCustomContentSendRequest, task);
        task.setAppType(mmsCustomContentSendRequest.getAppType());

        // body中最终存的是OSS的相关关联结构体
        task.setBody(mmsCustomContentSendRequest.getContext());
        task.setIsModelSend(false);

        try {

            long sid = joinTask2Queue(task);
            if (sid != 0L) {
                return new MmsSendResponse(mmsCustomContentSendRequest.getTotalFee(), sid);
            }

        } catch (QueueProcessException e) {
            logger.error("发送彩信至队列错误， {}", e);
        }

        return new MmsSendResponse(CommonApiCode.COMMON_SERVER_EXCEPTION);
    }

    /**
     * TODO 获取短余额信息
     *
     * @return
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
     * TODO 提交任务到队列
     *
     * @param task
     * @return
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

            // 插入TASK任务（异步）
            String queueName = RabbitConstant.MQ_MMS_MT_WAIT_PROCESS;

            rabbitTemplate.convertAndSend(queueName, task, new CorrelationData(task.getSid().toString()));

            return task.getSid();
        } catch (Exception e) {
            logger.error("发送短信队列失败", e);
            throw new QueueProcessException("发送短信队列失败");
        }
    }

}
