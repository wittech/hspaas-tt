package com.huashi.mms.config.cache.redis.constant;

/**
 * TODO 彩信REDIS防止重复提交常量类
 *
 * @author zhengying
 * @version V1.0
 * @date 2018年02月07日 下午16:57:30
 */
public class MmsRepeatSubmitConstant {

    /**
     * 任务审核通过执行中KEY定义
     */
    public static final String DOING_TASK_APPROVED = "doing:task_approved:";

    /**
     * 任务驳回执行中KEY定义
     */
    public static final String DOING_TASK_REJECT   = "doing:task_reject:";

}
