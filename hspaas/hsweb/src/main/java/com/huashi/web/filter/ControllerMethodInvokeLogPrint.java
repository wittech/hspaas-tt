package com.huashi.web.filter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 
  * TODO 控制器方法调用日志输出
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2016年7月7日 下午11:29:21
 */
@Component
@Aspect
public class ControllerMethodInvokeLogPrint {
	
	private Logger logger = LoggerFactory.getLogger(ControllerMethodInvokeLogPrint.class);
	
	public ControllerMethodInvokeLogPrint() {}
	
	@Pointcut("within(@org.springframework.stereotype.Controller *)")
	public void controllerBean(){}
	
	@Pointcut("execution(* org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter.handle(..))")
	public void hh(){}
	
	@Pointcut("execution(* *(..))")
	public void methodPointcut(){}
	
	@Pointcut("@annotation(org.springframework.stereotype.Controller)")
	public void controllerLog(){}
	
	@Around("controllerBean()")
	public Object arround(ProceedingJoinPoint point){
		try {

			Object obj;
			// header 被访问的太多了.
			if (point.getSignature().getName().equals("head")){
				obj = point.proceed();
			} else {
				logger.info(String.format("执行方法: %s::%s",
						point.getThis().getClass().getCanonicalName(),
						point.getSignature().getName()));
				long start=System.nanoTime();
				obj= point.proceed();
				String result = String.format("执行结果: %s",obj);
				if(result != null && result.length() < 500) {
					logger.info(result);
				}
				long end=System.nanoTime();
				logger.info(String.format("执行时间: %d ms",(TimeUnit.MILLISECONDS.convert(end-start, TimeUnit.NANOSECONDS))));
			}

			return obj;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
