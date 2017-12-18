package com.huashi.listener.config;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.huashi.common.util.DateUtil;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
  * TODO 开发者用户调用日志切面设置
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2016年9月20日 下午4:26:34
 */
@Aspect
@Order(1)
@Component
public class VisitLogAop {

	private AtomicLong startTime = new AtomicLong();
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Pointcut("execution(public * com.huashi.listener.controller..*.*(..))")
	public void pointcut() {
	}

	@Before("pointcut()")
	public void doBefore(JoinPoint joinPoint) throws Throwable {
		// 接收到请求，记录请求内容
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();

		logger.info("Invoke action report -------- {} ------------------------------ ", DateUtil.getNow());
		logger.info("请求地址 : {}, 调用IP: {}, 调用方式：{}", request.getRequestURL().toString(), 
				request.getRemoteAddr(), request.getMethod());
		
		if(HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
            {
                logger.info("请求参数 : {} ", request.getQueryString());
            }
        } else {
            {
                logger.info("请求参数 : {} ", JSON.toJSONString(request.getParameterMap()));
            }
        }
		
		
		startTime.set(System.currentTimeMillis());
	}

	@AfterReturning(returning = "ret", pointcut = "pointcut()")
	public void doAfterReturning(Object ret) throws Throwable {
		// 处理完请求，返回内容
		logger.info("响应数据：{}, 处理耗时 : {} 毫秒", ret, (System.currentTimeMillis() - startTime.get()));
		logger.info("--------------------------------------------------------------------------------");
	}

}
