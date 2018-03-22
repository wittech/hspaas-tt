package com.huashi.developer.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidateField {

    String value() default "";

    boolean required() default false;

    boolean number() default false;

    boolean utf8() default false;
}
