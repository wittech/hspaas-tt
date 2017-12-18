package com.huashi.hsboss.config.plugin.spring;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;

/**
 * @author tenx
 * IocInterceptor.
 */
public class IocInterceptor implements Interceptor {

    static ApplicationContext ctx;


    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        Field[] fields = controller.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object bean;
            if (field.isAnnotationPresent(Inject.BY_NAME.class)) {
                bean = ctx.getBean(field.getName());
            } else if (field.isAnnotationPresent(Inject.BY_TYPE.class)) {
                bean = ctx.getBean(field.getType());
            } else {
                continue;
            }

            try {
                if (bean != null) {
                    field.setAccessible(true);
                    field.set(controller, bean);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        inv.invoke();

    }
}
