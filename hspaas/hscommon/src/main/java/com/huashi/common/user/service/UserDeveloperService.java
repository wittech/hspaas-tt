package com.huashi.common.user.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.config.redis.CommonRedisConstant;
import com.huashi.common.user.context.UserContext.UserStatus;
import com.huashi.common.user.dao.UserDeveloperMapper;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.util.IdBuilder;
import com.huashi.common.util.RandomUtil;
import com.huashi.common.util.SecurityUtil;

@Service
public class UserDeveloperService implements IUserDeveloperService {

    @Autowired
    private UserDeveloperMapper userDeveloperMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private Logger              logger = LoggerFactory.getLogger(getClass());

    @Override
    public UserDeveloper getByUserId(int userId) {
        return userDeveloperMapper.selectByUserId(userId);
    }

    @Override
    public UserDeveloper getByAppkey(String appkey) {
        try {
            Object d = stringRedisTemplate.opsForValue().get(String.format("%s:%s",
                                                                           CommonRedisConstant.RED_DEVELOPER_LIST,
                                                                           appkey));
            if (d != null) {
                return JSON.parseObject(d.toString(), UserDeveloper.class);
            }
        } catch (Exception e) {
            logger.error("REDIS获取开发者数据失败, appkey: {}", appkey, e);
        }
        return userDeveloperMapper.selectByAppkey(appkey);
    }

    @Override
    public UserDeveloper getByAppkey(String appkey, String appSecret) {
        if (StringUtils.isEmpty(appkey) || StringUtils.isEmpty(appSecret)) {
            return null;
        }

        return userDeveloperMapper.selectByAppkeyAndSecret(appkey, appSecret);
    }

    @Override
    public UserDeveloper saveWithReturn(int userId) {
        UserDeveloper developer = new UserDeveloper();
        developer.setUserId(userId);
        developer.setAppKey(IdBuilder.developerKeyBuider());
        developer.setSalt(BCrypt.gensalt());
        developer.setAppSecret(SecurityUtil.encode(buildSecretPassword(), developer.getSalt()));
        developer.setStatus(UserStatus.YES.getValue());
        developer.setCreateTime(new Date());
        if (userDeveloperMapper.insert(developer) > 0) {
            pushToRedis(developer);
            return developer;
        }

        return null;
    }

    @Override
    public boolean save(int userId) {
        return saveWithReturn(userId) != null;
    }

    /**
     * TODO 生成密钥密码
     * 
     * @return
     */
    private String buildSecretPassword() {
        return RandomUtil.randomMix(12);
    }

    @Override
    public boolean reloadToRedis() {
        List<UserDeveloper> list = findAll();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("可用开发者数据为空");
            return false;
        }

        try {
            stringRedisTemplate.delete(stringRedisTemplate.keys(CommonRedisConstant.RED_DEVELOPER_LIST + "*"));
        } catch (Exception e) {
            logger.error("清除REDIS 开发者数据失败", e);
        }

        for (UserDeveloper developer : list) {
            if (!pushToRedis(developer)) {
                return false;
            }
        }

        return true;
    }

    private boolean pushToRedis(UserDeveloper developer) {
        try {
            stringRedisTemplate.opsForValue().set(String.format("%s:%s", CommonRedisConstant.RED_DEVELOPER_LIST,
                                                                developer.getAppKey()), JSON.toJSONString(developer));
            return true;
        } catch (Exception e) {
            logger.warn("REDIS 加载用户映射数据失败", e);
            return false;
        }
    }

    private boolean removeRedis(String appkey) {
        try {
            stringRedisTemplate.delete(String.format("%s:%s", CommonRedisConstant.RED_DEVELOPER_LIST, appkey));
            return true;
        } catch (Exception e) {
            logger.warn("REDIS 移除用户映射数据失败, appkey : {}", appkey, e);
            return false;
        }
    }

    @Override
    public List<UserDeveloper> findAll() {
        return userDeveloperMapper.selectAll();
    }

    @Override
    @Transactional
    public boolean update(UserDeveloper developer) {
        // developer secret not null,update developer secret and salt
        UserDeveloper d = userDeveloperMapper.selectByPrimaryKey(developer.getId());
        // 2017-02-27 判断加密串是否有变化，如果
        if (StringUtils.isNotEmpty(developer.getAppSecret()) && !developer.getAppSecret().equals(d.getAppSecret())) {
            d.setSalt(developer.getSalt());
            d.setAppSecret(developer.getAppSecret());
        }

        // 判断APPKEY是否发生变化
        if (StringUtils.isNotEmpty(developer.getAppKey()) && !developer.getAppKey().equals(d.getAppKey())) {
            // 如果APPKEY 发生变化，则需要移除原有的缓存，产生新的缓存信息
            removeRedis(d.getAppKey());
            d.setAppKey(developer.getAppKey());
        }

        int result = userDeveloperMapper.updateByPrimaryKey(d);
        if (result > 0) {
            pushToRedis(d);
        }

        return result > 0;
    }

}
