package com.huashi.mms.config.cache.redis;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

import com.huashi.mms.config.cache.redis.constant.MmsRedisConstant;
import com.huashi.mms.config.cache.redis.pubsub.MmsPassageAccessListener;
import com.huashi.mms.config.cache.redis.serializer.RedisObjectSerializer;

@Configuration
@EnableCaching
@Order(1)
public class RedisConfiguration extends CachingConfigurerSupport {

    @Value("${redis.host}")
    private String  host;

    @Value("${redis.port}")
    private int     port;

    @Value("${redis.password}")
    private String  password;

    @Value("${redis.pool.maxActive}")
    private Integer maxTotal;

    @Value("${redis.pool.minIdle}")
    private Integer minIdle;

    @Value("${redis.pool.maxIdle}")
    private Integer maxIdle;

    @Value("${redis.pool.maxWaitMillis}")
    private Integer maxWaitMillis;

    @Value("${redis.client.connectionTimeout}")
    private Integer timeout;

    @Value("${redis.database}")
    private int     database;

    @Value("${redis.pool.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${redis.pool.testOnReturn}")
    private boolean testOnReturn;
    @Value("${redis.pool.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${redis.pool.timeBetweenEvictionRunsMillis}")
    private Integer timeBetweenEvictionRunsMillis;
    @Value("${redis.pool.minEvictableIdleTimeMillis}")
    private Integer minEvictableIdleTimeMillis;
    @Value("${redis.pool.softMinEvictableIdleTimeMillis}")
    private Integer softMinEvictableIdleTimeMillis;
    @Value("${redis.pool.numTestsPerEvictionRun}")
    private Integer numTestsPerEvictionRun;

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setMaxWaitMillis(maxWaitMillis);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setTestWhileIdle(testWhileIdle);
        config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        config.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
        config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        return config;
    }

    @Bean(name = "jedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig());
        jedisConnectionFactory.setUsePool(true);
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPort(port);
        jedisConnectionFactory.setPassword(password);
        jedisConnectionFactory.setDatabase(database);
        jedisConnectionFactory.setTimeout(timeout);

        return jedisConnectionFactory;
    }

    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(jedisConnectionFactory());

        return template;
    }

    @Bean
    public KeyGenerator wiselyKeyGenerator() {
        return new KeyGenerator() {

            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    @Bean
    public CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        // Number of seconds before expiration. Defaults to unlimited (0)
        cacheManager.setDefaultExpiration(10); // 设置key-value超时时间
        return cacheManager;
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("jedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * TODO 可用通道变更广播通知监听配置
     * 
     * @return
     */
    @Bean
    MessageListenerAdapter mmsPassageAccessMessageListener() {
        return new MessageListenerAdapter(new MmsPassageAccessListener());
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(mmsPassageAccessMessageListener(), passageAccessTopic());
        return container;
    }

    @Bean
    ChannelTopic messageTemplateTopic() {
        return new ChannelTopic(MmsRedisConstant.BROADCAST_MESSAGE_TEMPLATE_TOPIC);
    }

    @Bean
    ChannelTopic passageAccessTopic() {
        return new ChannelTopic(MmsRedisConstant.BROADCAST_PASSAGE_ACCESS_TOPIC);
    }

}
