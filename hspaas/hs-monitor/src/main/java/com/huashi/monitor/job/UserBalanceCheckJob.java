package com.huashi.monitor.job;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.huashi.common.user.context.UserContext.BalanceStatus;
import com.huashi.common.user.domain.UserBalance;
import com.huashi.common.user.model.UserModel;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.user.service.IUserService;
import com.huashi.sms.passage.service.ISmsPassageService;

/**
 * TODO 用于余额告警任务
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年8月16日 下午7:01:25
 */
@Service("userBalanceCheckJob")
public class UserBalanceCheckJob extends AbstractJob {

    @Reference
    private IUserService        userService;
    @Reference
    private IUserBalanceService userBalanceService;
    @Reference
    private ISmsPassageService  smsPassageService;

    private final Logger        logger                   = LoggerFactory.getLogger(getClass());

    private static final String WARNING_MESSAGE_TEMPLATE = "【华时科技】[%s][%s]余额到达告警阈值，当前余额为%d，请关注";

    /**
     * TODO 10分钟轮训一次
     */
    @Override
    public void run(ShardingContext shardingContext) {
        List<UserBalance> list = userBalanceService.findAvaibleUserBalace();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        // logger.info("Users " + JSON.toJSONString(list,new SimplePropertyPreFilter("userId,balance")) +
        // " balance checking");

        for (UserBalance ub : list) {
            if (!isNeedAndReachWarning(ub)) {
                continue;
            }

            if (!sendMessage(ub.getUserId(), ub.getBalance(), ub.getMobile())) {
                continue;
            }

            // 更新余额状态为高警中
            updateBalanceStatus(ub.getId());
        }
    }

    /**
     * TODO 是否需要告警并且达到了告警条件
     * 
     * @param userBalance
     * @return
     */
    private static boolean isNeedAndReachWarning(UserBalance userBalance) {
        if (userBalance == null) {
            return false;
        }

        // 如果未设置或者设置为0不告警， 如果手机号码为空也不告警
        if (userBalance.getThreshold() == null || userBalance.getThreshold() == 0
            || StringUtils.isBlank(userBalance.getMobile())) {
            return false;
        }

        // 如果余额大于预设的阈值，则不告警
        if (userBalance.getBalance() > userBalance.getThreshold()) {
            return false;
        }

        return true;
    }

    /**
     * TODO 发送余额告警短信
     * 
     * @param userId
     * @param balance
     * @param mobile
     * @return
     */
    private boolean sendMessage(Integer userId, Double balance, String mobile) {
        try {
            UserModel userModel = userService.getByUserId(userId);
            if (userModel == null) {
                logger.error("用户ID：{} 数据为空，发送余额告警信息失败，当前余额：{}", userId, balance.intValue());
                return false;
            }

            String messageContent = String.format(WARNING_MESSAGE_TEMPLATE, userModel.getName(), userModel.getAppkey(),
                                                  balance.intValue());

            boolean isOk = smsPassageService.doMonitorSmsSend(mobile, messageContent);
            if (!isOk) {
                logger.error("发送用于余额告警失败，用户ID：{} 余额告警短信：{}", userId, messageContent);
                return false;
            }

            return true;
        } catch (Exception e) {
            logger.error("发送用于余额告警失败，用户ID：{} 当前余额：{}", userId, balance.intValue(), e);
            return false;
        }
    }

    /**
     * TODO 更细用户余额状态
     * 
     * @param id
     */
    private void updateBalanceStatus(Integer id) {
        try {
            userBalanceService.updateStatus(id, BalanceStatus.WARNING.getValue());
        } catch (Exception e) {
            logger.error("更新用户余额状态为告警中失败", e);
        }
    }

}
