package com.huashi.common.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

/**
 * 
  * TODO 调用日志切面设置
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2016年9月20日 下午4:26:34
 */
@Aspect
@Order(1)
@Component
public class VisitLogAop {

	ThreadLocal<Long> startTime = new ThreadLocal<>();
	Logger logger = LoggerFactory.getLogger(getClass());

	@Pointcut("execution(public * com.huashi.common.*.service..*.*(..))")
	public void pointcut() {
	}

	@Before("pointcut()")
	public void doBefore(JoinPoint joinPoint) throws Throwable {
		logger.info("调用方法：{}, 参数信息： {}", joinPoint.getSignature().getName(), JSON.toJSONString(joinPoint.getArgs()));
		startTime.set(System.currentTimeMillis());
	}

	@AfterReturning(returning = "ret", pointcut = "pointcut()")
	public void doAfterReturning(Object ret) throws Throwable {
		// 处理完请求，返回内容
		logger.info("响应数据：{}, 处理耗时 : {} 毫秒", ret, (System.currentTimeMillis() - startTime.get()));
		logger.info("--------------------------------------");
	}

}
