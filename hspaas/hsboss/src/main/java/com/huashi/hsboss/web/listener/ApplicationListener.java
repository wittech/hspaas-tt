package com.huashi.hsboss.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 应用监听器
 * @author ym
 * @created_at 2016年6月22日下午2:12:10
 */
public class ApplicationListener implements ServletContextListener{

	
	private final static Logger LOG = LoggerFactory.getLogger(ApplicationListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOG.info("start load spring....");
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		SpringBeanManager.initContext(context);
		
//		AnnotationBean bean = context.getBean(AnnotationBean.class);
//		bean.setApplicationContext(context);
		
//		String[] names = context.getBeanDefinitionNames();
//		for(String name : names){
//			System.out.println("name----:"+name);
//		}R
		
//		AnnotationBean annotation = context.getBean(AnnotationBean.class);
//		System.out.println("----:"+annotation.getPackage());
		LOG.info("load spring finished...");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}


}
