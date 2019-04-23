package com.huashi.common.config.async;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.huashi.common.config.redis.CommonRedisConstant;

@Service
public class AsyncService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private final Logger        logger = LoggerFactory.getLogger(getClass());

    @Async
    public void publishMobilesLocalToRedis() {
        long startTime = System.currentTimeMillis();
        
        stringRedisTemplate.execute(new RedisCallback<List<Object>>() {

            @Override
            public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
                connection.openPipeline();

                byte[] key = serializer.serialize(CommonRedisConstant.RED_PROVINCE_MOBILES_LOCAL);

                byte[] value = JSON.toJSONBytes(CommonRedisConstant.GLOBAL_MOBILES_LOCAL);
                
                connection.set(key, value);

                return connection.closePipeline();
            }

        }, false, true);

        logger.info("RED_PROVINCE_MOBILES_LOCAL redis 共耗时 :{} ms", (System.currentTimeMillis() - startTime));
    }

}
