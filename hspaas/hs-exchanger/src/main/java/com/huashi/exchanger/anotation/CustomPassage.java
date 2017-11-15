package com.huashi.exchanger.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.huashi.constants.CommonContext.ProtocolType;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomPassage {

	String value() default "";

	String name() default "";

	ProtocolType protocol() default ProtocolType.HTTP;
}
