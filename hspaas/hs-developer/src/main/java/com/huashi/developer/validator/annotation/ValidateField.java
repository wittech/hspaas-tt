package com.huashi.developer.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidateField {

    String value() default "";

    /**
     * 
       * TODO 是否必填项
       * @return
     */
    boolean required() default false;

    /**
     * 
       * TODO 是否为数字
       * @return
     */
    boolean number() default false;

    /**
     * 
       * TODO 是否必须UTF-8编码（暂未用）
       * @return
     */
    boolean utf8() default false;
}
