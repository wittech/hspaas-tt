package com.huashi.sms.config.worker;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

public class BaseWorker {

	protected StringRedisTemplate stringRedisTemplate;
	protected Logger logger = LoggerFactory.getLogger(getClass());
	

	// 批量扫描大小
	protected static final int SCAN_BATCH_SIZE = 2000;
	
	// 当前时间计时
	protected final AtomicLong timer = new AtomicLong(0);
}
