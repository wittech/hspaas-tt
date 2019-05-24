package com.huashi.sms.config.cache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.cache.redis.pubsub.SmsMessageTemplateListener;
import com.huashi.sms.config.cache.redis.pubsub.SmsMobileBlacklistListener;
import com.huashi.sms.config.cache.redis.pubsub.SmsPassageAccessListener;
import com.huashi.sms.config.cache.redis.serializer.RedisObjectSerializer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Configuration
@EnableCaching
@Order(1)
public class RedisConfiguration extends CachingConfigurerSupport {

    @Value("${spring.redis.host}")
    private String              host;
    @Value("${spring.redis.port}")
    private int                 port;
    @Value("${spring.redis.password}")
    private String              password;
    @Value("${spring.redis.database}")
    private int                 database;
    @Value("${spring.redis.timeout}")
    private Long                timeout;

    @Value("${spring.redis.jedis.pool.max-active}")
    private Integer             maxActive;
    @Value("${spring.redis.jedis.pool.min-idle}")
    private Integer             minIdle;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private Integer             maxIdle;
    @Value("${spring.redis.jedis.pool.max-wait}")
    private Integer             maxWait;

    /**
     * jedis 原生实例
     */
    private static Jedis        jedis  = null;

    private final static Logger LOGGER = LoggerFactory.getLogger(RedisConfiguration.class);

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWait);

        return jedisPoolConfig;
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(database);
        redisStandaloneConfiguration.setPassword(password);

        return redisStandaloneConfiguration;
    }

    @Bean(name = "jedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration) {

        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
        jedisClientConfiguration.connectTimeout(Duration.ofMillis(timeout));

        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
    }

    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(jedisConnectionFactory);

        return template;
    }

    @Bean
    public KeyGenerator wiselyKeyGenerator() {
        return (target, method, params) -> {

            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();

        };
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory).build();
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("jedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 黑名单数据变更广播通知监听配置
     * 
     * @return 消息监听适配器
     */
    @Bean
    MessageListenerAdapter smsMobileBlacklistMessageListener(StringRedisTemplate stringRedisTemplate) {
        return new SmsMobileBlacklistListener(stringRedisTemplate);
    }

    /**
     * 短信模板变更广播通知监听配置
     * 
     * @return 短信模板监听器
     */
    @Bean
    MessageListenerAdapter smsMessageTemplateMessageListener() {
        return new SmsMessageTemplateListener();
    }

    /**
     * 可用通道变更广播通知监听配置
     * 
     * @return 可用通道监听器
     */
    @Bean
    MessageListenerAdapter smsPassageAccessMessageListener() {
        return new SmsPassageAccessListener();
    }

    @Bean
    RedisMessageListenerContainer redisContainer(StringRedisTemplate stringRedisTemplate,
                                                 JedisConnectionFactory jedisConnectionFactory) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        container.addMessageListener(smsMobileBlacklistMessageListener(stringRedisTemplate), mobileBlacklistTopic());
        container.addMessageListener(smsMessageTemplateMessageListener(), messageTemplateTopic());
        container.addMessageListener(smsPassageAccessMessageListener(), passageAccessTopic());
        return container;
    }

    @Bean
    Topic mobileBlacklistTopic() {
        return new PatternTopic(SmsRedisConstant.BROADCAST_MOBILE_BLACKLIST_TOPIC);
    }

    @Bean
    Topic messageTemplateTopic() {
        return new PatternTopic(SmsRedisConstant.BROADCAST_MESSAGE_TEMPLATE_TOPIC);
    }

    @Bean
    Topic passageAccessTopic() {
        return new PatternTopic(SmsRedisConstant.BROADCAST_PASSAGE_ACCESS_TOPIC);
    }

    /**
     * 设置JEDIS实例
     */
    @PostConstruct
    public void initJedis() {
        try {
            jedis = new Jedis(host, port);
            jedis.auth(password);
            jedis.select(database);

        } catch (Exception e) {
            LOGGER.error("Set new jedis instance failed", e);
        }
    }

    public static Jedis getJedis() {
        return jedis;
    }

}
