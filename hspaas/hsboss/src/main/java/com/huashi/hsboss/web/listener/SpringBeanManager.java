package com.huashi.hsboss.web.listener;

import org.springframework.context.ApplicationContext;

/**
 * springbean管理器
 * @author ym
 * @created_at 2016年6月22日下午2:12:28
 */
public class SpringBeanManager {

	private static ApplicationContext context;

	public static void initContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static ApplicationContext getContext() {
		return context;
	}

	public static Object getBean(String name) {
		return context.getBean(name);
	}

	public static <T> T getBean(String name, Class<T> cls) {
		return context.getBean(name, cls);
	}
}
