package com.huashi.monitor.config.redis;

import java.time.Duration;

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
import org.springframework.data.redis.core.StringRedisTemplate;

import redis.clients.jedis.JedisPoolConfig;

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

}
