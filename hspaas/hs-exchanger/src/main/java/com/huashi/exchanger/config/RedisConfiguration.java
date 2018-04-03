package com.huashi.exchanger.config;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableCaching
@Order(1)
public class RedisConfiguration extends CachingConfigurerSupport {

	@Value("${redis.password}")
	private String password;

	@Value("${redis.pool.maxActive}")
	private Integer maxTotal;

	@Value("${redis.pool.maxIdle}")
	private Integer maxIdle;

	@Value("${redis.pool.maxWaitMillis}")
	private Integer maxWaitMillis;

	@Value("${redis.client.connectionTimeout}")
	private Integer timeout;

	@Value("${redis.database}")
	private int database;

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
	
	@Value("${redis.cluster.nodes}")
    private String  clusterNodes;

    @Value("${redis.cluster.timeout}")
    private Integer clusterTimeout;

    @Value("${redis.cluster.maxRedirects:5}")
    private Integer clusterMaxRedirects;

	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
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

	public Iterable<RedisNode> redisNodes() {
        if (StringUtils.isEmpty(clusterNodes)) {
            throw new IllegalArgumentException("Redis cluster nodes can not be null");
        }

        Set<RedisNode> redisNoes = new HashSet<>();
        String[] nodes = clusterNodes.split(",");
        for (String node : nodes) {
            String[] ipPort = node.split(":");
            redisNoes.add(new RedisNode(ipPort[0].trim(), Integer.valueOf(ipPort[1])));
        }

        return redisNoes;
    }

    @Bean
    public RedisClusterConfiguration redisClusterConfiguration() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        redisClusterConfiguration.setClusterNodes(redisNodes());
        redisClusterConfiguration.setMaxRedirects(clusterMaxRedirects);

        return redisClusterConfiguration;
    }

    @Bean(name = "jedisConnectionFactory")
    public JedisConnectionFactory redisSmsConnectionFactory(RedisClusterConfiguration redisClusterConfiguration,
                                                            JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration, jedisPoolConfig);
//      jedisConnectionFactory.setHostName(host);
//      jedisConnectionFactory.setPort(port);
        jedisConnectionFactory.setPassword(password);
        jedisConnectionFactory.setUsePool(true);
        jedisConnectionFactory.setDatabase(database);
        
        return jedisConnectionFactory;
    }

	@Bean(name = "stringRedisTemplate")
	public StringRedisTemplate stringRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(jedisConnectionFactory);
		
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
	public RedisTemplate<String, Object> redisTemplate(
			@Qualifier("jedisConnectionFactory") RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new RedisObjectSerializer());
		template.afterPropertiesSet();
		return template;
	}
}
