package com.huashi.common.user.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.config.redis.CommonRedisConstant;
import com.huashi.common.settings.context.SettingsContext.SystemConfigType;
import com.huashi.common.settings.domain.SystemConfig;
import com.huashi.common.settings.service.ISystemConfigService;
import com.huashi.common.user.context.UserBalanceConstant;
import com.huashi.common.user.context.UserSettingsContext.SmsReturnRule;
import com.huashi.common.user.dao.UserSmsConfigMapper;
import com.huashi.common.user.domain.UserSmsConfig;

@Service
public class UserSmsConfigService implements IUserSmsConfigService {

    @Autowired
    private UserSmsConfigMapper  userSmsConfigMapper;
    @Autowired
    private ISystemConfigService systemConfigService;
    @Resource
    private StringRedisTemplate  stringRedisTemplate;

    private Logger               logger         = LoggerFactory.getLogger(getClass());

    // 短信配置 KEY
    public static final String   SMS_CONFIG_KEY = "sms_words";

    @Override
    public UserSmsConfig getByUserId(int userId) {
        if (userId == 0) {
            return null;
        }

        try {
            String text = stringRedisTemplate.opsForValue().get(getKey(userId));
            if(StringUtils.isNotEmpty(text)) {
                return JSON.parseObject(text, UserSmsConfig.class);
            }
            
        } catch (Exception e) {
            logger.warn("REDIS获取用户短信配置失败", e);
        }

        return userSmsConfigMapper.selectByUserId(userId);
    }

    private static String getKey(Integer userId) {
        return CommonRedisConstant.RED_USER_SMS_CONFIG + ":" + userId;
    }

    /**
     * 
       * TODO 默认初始化配置
       * @return
     */
    private UserSmsConfig defaultConfig() {
        UserSmsConfig userSmsConfig = new UserSmsConfig();
        userSmsConfig.setCreateTime(new Date());
        userSmsConfig.setAutoTemplate(false);
        userSmsConfig.setMessagePass(false);
        userSmsConfig.setNeedTemplate(true);
        userSmsConfig.setSmsReturnRule(SmsReturnRule.NO.getValue());
        userSmsConfig.setSmsTimeout(0L);

        return userSmsConfig;
    }

    @Override
    public boolean save(int userId, int words, String extNumber) {
        UserSmsConfig userSmsConfig = defaultConfig();
        userSmsConfig.setUserId(userId);
        userSmsConfig.setExtNumber(extNumber);
        userSmsConfig.setSmsWords(words);

        return save(userSmsConfig);
    }

    private void setWords(UserSmsConfig userSmsConfig) {
        if (userSmsConfig.getSmsWords() == 0) {
            SystemConfig systemConfig = systemConfigService.findByTypeAndKey(SystemConfigType.SMS_WORDS_PER_NUM.name(),
                                                                             SMS_CONFIG_KEY);
            if (systemConfig == null) {
                userSmsConfig.setSmsWords(UserBalanceConstant.WORDS_SIZE_PER_NUM);
            } else {
                userSmsConfig.setSmsWords(Integer.parseInt(systemConfig.getAttrValue()));
            }
        }
    }

    @Override
    @Transactional
    public boolean save(UserSmsConfig userSmsConfig) {
        if (userSmsConfig == null) {
            return false;
        }

        try {
            setWords(userSmsConfig);
            userSmsConfig.setCreateTime(new Date());

            int result = userSmsConfigMapper.insertSelective(userSmsConfig);
            if (result > 0) {
                pushToRedis(userSmsConfig);
                return true;
            }

        } catch (Exception e) {
            logger.error("添加短信配置信息失败 {}", JSON.toJSONString(userSmsConfig), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            removeFromRedis(userSmsConfig.getUserId());
        }

        return false;
    }

    @Override
    @Transactional
    public boolean update(UserSmsConfig config) {
        config.setUpdateTime(new Date());
        pushToRedis(config);

        return userSmsConfigMapper.updateByPrimaryKey(config) > 0;
    }

    /**
     * TODO 添加至REDIS
     * 
     * @param userSmsConfig
     */
    private void pushToRedis(UserSmsConfig userSmsConfig) {
        try {
            stringRedisTemplate.opsForValue().set(getKey(userSmsConfig.getUserId()), JSON.toJSONString(userSmsConfig));

        } catch (Exception e) {
            logger.warn("REDIS 操作用户短信配置失败", e);
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
        List<UserSmsConfig> list = userSmsConfigMapper.selectAll();
        if (CollectionUtils.isEmpty(list)) {
            logger.error("用户短信配置数据为空");
            return true;
        }

        stringRedisTemplate.delete(stringRedisTemplate.keys(CommonRedisConstant.RED_USER_SMS_CONFIG + "*"));
        for (UserSmsConfig hwl : list) {
            pushToRedis(hwl);
        }

        return true;
    }

    @Override
    public int getSingleChars(int userId) {
        int wordsPerNum = UserBalanceConstant.WORDS_SIZE_PER_NUM;
        try {
            UserSmsConfig userSmsConfig = getByUserId(userId);
            if (userSmsConfig != null) {
                wordsPerNum = userSmsConfig.getSmsWords();
            }

        } catch (Exception e) {
            logger.warn("查询用户：{} 短信字数配置失败，将以默认每条字数：{}计费", userId, wordsPerNum, e);
        }
        return wordsPerNum;
    }

}
