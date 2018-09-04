package com.huashi.hsboss.annotation;

import com.huashi.hsboss.constant.EnumConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 视图展示类型
 * Author youngmeng
 * Created 2018-09-04 14:51
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ViewType {

    EnumConstant.ViewType type() default EnumConstant.ViewType.HTML;
}
