package com.huashi.hsboss.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 每个控制器标记对应的菜单码
 * @author ym
 * @created_at 2016年6月28日下午5:23:57
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ViewMenu {

	String[] code() default {};
	
	String[] name() default {};
}
