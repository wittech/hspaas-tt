package com.huashi.common.user.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

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
            stringRedisTemplate.opsForValue().get(getKey(userId));
        } catch (Exception e) {
            logger.warn("REDIS获取用户短信配置失败", e);
        }

        return userSmsConfigMapper.selectByUserId(userId);
    }

    private static String getKey(Integer userId) {
        return CommonRedisConstant.RED_USER_SMS_CONFIG + ":" + userId;
    }

    @Override
    public UserSmsConfig save(int userId, int words, String extNumber) {
        UserSmsConfig userSmsConfig = new UserSmsConfig();
        userSmsConfig.setCreateTime(new Date());
        userSmsConfig.setAutoTemplate(false);
        userSmsConfig.setMessagePass(false);
        userSmsConfig.setNeedTemplate(true);
        userSmsConfig.setSmsReturnRule(SmsReturnRule.NO.getValue());
        userSmsConfig.setSmsTimeout(0L);
        userSmsConfig.setUserId(userId);
        userSmsConfig.setExtNumber(extNumber);
        if (words == 0) {
            SystemConfig systemConfig = systemConfigService.findByTypeAndKey(SystemConfigType.SMS_WORDS_PER_NUM.name(),
                                                                             SMS_CONFIG_KEY);
            if (systemConfig == null) {
                userSmsConfig.setSmsWords(UserBalanceConstant.WORDS_SIZE_PER_NUM);
            } else {
                userSmsConfig.setSmsWords(Integer.parseInt(systemConfig.getAttrValue()));
            }

        } else {
            userSmsConfig.setSmsWords(words);
        }
        pushToRedis(userSmsConfig);

        userSmsConfigMapper.insertSelective(userSmsConfig);

        return userSmsConfig;
    }

    @Override
    public boolean save(UserSmsConfig userSmsConfig) {
        if (userSmsConfig == null) {
            return false;
        }

        if (userSmsConfig.getSmsWords() == 0) {
            SystemConfig systemConfig = systemConfigService.findByTypeAndKey(SystemConfigType.SMS_WORDS_PER_NUM.name(),
                                                                             SMS_CONFIG_KEY);
            if (systemConfig == null) {
                userSmsConfig.setSmsWords(UserBalanceConstant.WORDS_SIZE_PER_NUM);
            } else {
                userSmsConfig.setSmsWords(Integer.parseInt(systemConfig.getAttrValue()));
            }

        }

        userSmsConfig.setCreateTime(new Date());
        pushToRedis(userSmsConfig);

        return userSmsConfigMapper.insertSelective(userSmsConfig) > 0;
    }

    @Override
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
