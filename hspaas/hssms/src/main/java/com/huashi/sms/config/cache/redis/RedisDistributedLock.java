package com.huashi.sms.config.cache.redis;

import org.springframework.stereotype.Component;

@Component
public class RedisDistributedLock {

	public boolean setLock(String lockName) {
		
		
		return true;
	}
	
	
	public void releaseLock(String lockName) {
		
	}
	
}
