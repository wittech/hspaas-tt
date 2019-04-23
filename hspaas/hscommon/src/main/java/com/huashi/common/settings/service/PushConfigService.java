/**
 * 
 */
package com.huashi.common.settings.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.config.redis.CommonRedisConstant;
import com.huashi.common.settings.context.SettingsContext.PushConfigStatus;
import com.huashi.common.settings.dao.PushConfigMapper;
import com.huashi.common.settings.domain.PushConfig;

/**
 * 推送设置服务接口实现类
 * 
 * @author Administrator
 */
@Service
public class PushConfigService implements IPushConfigService {

    @Autowired
    private PushConfigMapper    pushConfigMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private Logger              logger = LoggerFactory.getLogger(getClass());

    private String getAssistKey(int userId, int type) {
        return String.format("%s:%d:%d", CommonRedisConstant.RED_USER_PUSH_CONFIG, userId, type);
    }

    @Override
    public boolean update(PushConfig record) {
        if (record.getId() == null) {
            return false;
        }

        record.setModifyTime(new Date());
        pushToRedis(record.getUserId(), record.getType(), record);

        return pushConfigMapper.updateByPrimaryKey(record) > 0;
    }

    @Override
    public List<PushConfig> findByUserId(int userId) {
        return pushConfigMapper.selectByUserId(userId);
    }

    @Override
    public boolean save(PushConfig record) {
        record.setCreateTime(new Date());
        int id = pushConfigMapper.insertSelective(record);
        PushConfig c = pushConfigMapper.selectByUserIdAndType(record.getUserId(), record.getType());
        record.setId(c.getId());
        pushToRedis(record.getUserId(), record.getType(), record);
        return id > 0;
    }

    @Override
    public PushConfig getByUserId(int userId, int type) {
        try {
            Object object = stringRedisTemplate.opsForValue().get(getAssistKey(userId, type));
            if (object != null) {
                return JSON.parseObject(object.toString(), PushConfig.class);
            }

        } catch (Exception e) {
            logger.warn("REDIS 查询推送配置失败", e);
        }
        return pushConfigMapper.selectByUserIdAndType(userId, type);
    }

    @Override
    public PushConfig getPushUrl(int userId, int callbackUrlType, String customUrl) {
        PushConfig config = getByUserId(userId, callbackUrlType);
        if (config == null) {
            return null;
        }

        if (config.getStatus() == PushConfigStatus.YES_WITH_POST.getCode()) {
            config.setUrl(customUrl);
        }

        return config;
    }

    /**
     * TODO 添加到REDIS
     * 
     * @param userId
     * @param type
     * @param pc
     */
    private void pushToRedis(int userId, int type, PushConfig pc) {
        try {
            stringRedisTemplate.opsForValue().set(getAssistKey(userId, type), JSON.toJSONString(pc));
        } catch (Exception e) {
            logger.warn("REDIS 推送配置操作失败", e);
        }
    }

    @Override
    public boolean reloadToRedis() {
        List<PushConfig> list = pushConfigMapper.selectAll();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("用户推送设置数据查询为空");
            return false;
        }
        
        List<Object> con = stringRedisTemplate.execute(new RedisCallback<List<Object>>() {

            @Override
            public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
                connection.openPipeline();
                for (PushConfig config : list) {
                    byte[] key = serializer.serialize(getAssistKey(config.getUserId(), config.getType()));
                    connection.set(key, serializer.serialize(JSON.toJSONString(config)));
                }

                return connection.closePipeline();
            }

        }, false, true);

        return CollectionUtils.isNotEmpty(con);
    }

    @Override
    public int updateByUserId(PushConfig pushConFig) {
        int result = 0;
        try {
            // 判断如果用户推送记录中没有数据，则插入推送信息
            PushConfig pushConfig = pushConfigMapper.selectByUserIdAndType(pushConFig.getUserId(), pushConFig.getType());
            if (pushConfig == null) {
                pushConFig.setCreateTime(new Date());
                result = pushConfigMapper.insert(pushConFig);
            }

            pushConFig.setModifyTime(new Date());
            result = pushConfigMapper.updateByUserId(pushConFig);

            // 查询修改后数据存储到缓存中
            PushConfig cf = pushConfigMapper.selectByUserIdAndType(pushConFig.getUserId(), pushConFig.getType());
            if (cf == null) {
                {
                    return 0;
                }
            }

            pushToRedis(cf.getUserId(), cf.getType(), cf);
        } catch (Exception e) {
            logger.error("更新用户推送信息失败：{}", JSON.toJSONString(pushConFig), e);
        }

        return result;
    }
}
