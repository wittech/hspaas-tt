package com.huashi.developer.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
public class AuthenticateAop {

	@Pointcut("execution(public * com.huashi.developer.sms.prervice..*.*(..))")
	public void smsPrervicePointcut() {
	}

	@Pointcut("execution(public * com.huashi.developer.fs.prervice..*.*(..))")
	public void fsPrervicePointcut() {
	}

	@Pointcut("execution(public * com.huashi.developer.vs.prervice..*.*(..))")
	public void vsPrervicePointcut() {
	}
	
	
	
	
	
	
	
	
	
	

}
