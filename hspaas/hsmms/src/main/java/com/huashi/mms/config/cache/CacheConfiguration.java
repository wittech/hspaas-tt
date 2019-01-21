package com.huashi.mms.config.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * TODO 缓存配置
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年9月30日 上午10:32:46
 */
@Configuration
@EnableCaching
public class CacheConfiguration {


	/**
	 * ehcache 主要的管理器
	 * 
	 * @param bean
	 * @return
//	 */
//	@Bean
//	public EhCacheCacheManager ehCacheCacheManager(
//			EhCacheManagerFactoryBean bean) {
//		return new EhCacheCacheManager();
//	}

	/*
	 * 据shared与否的设置, Spring分别通过CacheManager.create() 或new
	 * CacheManager()方式来创建一个ehcache基地.
	 * 
	 * 也说是说通过这个来设置cache的基地是这里的Spring独用,还是跟别的(如hibernate的Ehcache共享)
	 */
//	@Bean
//	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
//		EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
//		cacheManagerFactoryBean.setConfigLocation(new ClassPathResource(
//				"ehcache.xml"));
//		cacheManagerFactoryBean.setShared(true);
//		return cacheManagerFactoryBean;
//	}

}
