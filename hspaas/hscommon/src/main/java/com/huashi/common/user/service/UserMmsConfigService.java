package com.huashi.common.user.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.config.redis.CommonRedisConstant;
import com.huashi.common.settings.service.ISystemConfigService;
import com.huashi.common.user.context.UserSettingsContext.MmsReturnRule;
import com.huashi.common.user.dao.UserMmsConfigMapper;
import com.huashi.common.user.domain.UserMmsConfig;

@Service
public class UserMmsConfigService implements IUserMmsConfigService {

    @Autowired
    private UserMmsConfigMapper  userMmsConfigMapper;
    @Autowired
    private ISystemConfigService systemConfigService;
    @Resource
    private StringRedisTemplate  stringRedisTemplate;

    private final Logger         logger = LoggerFactory.getLogger(getClass());

    @Override
    public UserMmsConfig getByUserId(int userId) {
        if (userId == 0) {
            return null;
        }

        try {
            String text = stringRedisTemplate.opsForValue().get(getKey(userId));
            if (StringUtils.isNotEmpty(text)) {
                return JSON.parseObject(text, UserMmsConfig.class);
            }

        } catch (Exception e) {
            logger.warn("REDIS获取用户彩信配置失败", e);
        }

        return userMmsConfigMapper.selectByUserId(userId);
    }

    private static String getKey(Integer userId) {
        return CommonRedisConstant.RED_USER_MMS_CONFIG + ":" + userId;
    }

    /**
     * TODO 默认初始化配置
     * 
     * @return
     */
    private UserMmsConfig defaultConfig() {
        UserMmsConfig userMmsConfig = new UserMmsConfig();
        userMmsConfig.setCreateTime(new Date());
        userMmsConfig.setMessagePass(false);
        userMmsConfig.setMmsReturnRule(MmsReturnRule.NO.getValue());
        userMmsConfig.setSmsTimeout(0L);

        return userMmsConfig;
    }

    @Override
    public boolean save(int userId) {
        UserMmsConfig userMmsConfig = defaultConfig();
        userMmsConfig.setUserId(userId);

        return save(userMmsConfig);
    }

    @Override
    @Transactional
    public boolean save(UserMmsConfig userMmsConfig) {
        if (userMmsConfig == null) {
            logger.error("彩信配置信息为空");
            return false;
        }

        try {
            userMmsConfig.setCreateTime(new Date());
            userMmsConfig.setUpdateTime(new Date());

            int result = userMmsConfigMapper.insertSelective(userMmsConfig);
            if (result > 0) {
                pushToRedis(userMmsConfig);
                return true;
            }

        } catch (Exception e) {
            logger.error("添加彩信配置信息失败 {}", JSON.toJSONString(userMmsConfig), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            removeFromRedis(userMmsConfig.getUserId());
        }

        return false;
    }

    @Override
    @Transactional
    public boolean update(UserMmsConfig config) {
        config.setUpdateTime(new Date());
        
        int result = userMmsConfigMapper.updateByPrimaryKeySelective(config);
        if(result > 0) {
            pushToRedis(config);
            return true;
        }

        return false;
    }

    /**
     * TODO 添加至REDIS
     * 
     * @param userMmsConfig
     */
    private void pushToRedis(UserMmsConfig userMmsConfig) {
        try {
            stringRedisTemplate.opsForValue().set(getKey(userMmsConfig.getUserId()), JSON.toJSONString(userMmsConfig));

        } catch (Exception e) {
            logger.warn("REDIS 操作用户彩信配置失败", e);
        }
    }

    private void removeFromRedis(Integer userId) {
        try {
            stringRedisTemplate.delete(getKey(userId));

        } catch (Exception e) {
            logger.warn("REDIS 移除用户彩信配置失败", e);
        }
    }

    @Override
    public boolean reloadToRedis() {
        List<UserMmsConfig> list = userMmsConfigMapper.selectAll();
        if (CollectionUtils.isEmpty(list)) {
            logger.error("用户彩信配置数据为空");
            return true;
        }

        List<Object> con = stringRedisTemplate.execute(new RedisCallback<List<Object>>() {

            @Override
            public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
                connection.openPipeline();
                for (UserMmsConfig config : list) {
                    byte[] key = serializer.serialize(getKey(config.getUserId()));
                    connection.set(key, serializer.serialize(JSON.toJSONString(config)));
                }

                return connection.closePipeline();
            }

        }, false, true);

        return CollectionUtils.isNotEmpty(con);
    }

}
