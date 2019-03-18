package com.huashi.common.user.service;

import com.huashi.common.user.domain.UserMmsConfig;

/**
 * TODO 用户彩信配置信息
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月14日 下午2:35:58
 */
public interface IUserMmsConfigService {

    /**
     * TODO 根据用户ID查询彩信配置信息
     * 
     * @param userId
     * @return
     */
    UserMmsConfig getByUserId(int userId);

    /**
     * TODO 添加彩信设置
     * 
     * @param userId
     * @return
     */
    boolean save(int userId);
    
    /**
     * 修改
     * 
     * @param config
     * @return
     */
    boolean update(UserMmsConfig config);

    /**
     * TODO 添加彩信设置
     * 
     * @param userMmsConfig
     * @return
     */
    boolean save(UserMmsConfig userMmsConfig);

    /**
     * TODO 重新载入REDIS
     * 
     * @return
     */
    boolean reloadToRedis();

}
