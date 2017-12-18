package com.huashi.hsboss.config.plugin.spring;

import com.jfinal.plugin.IPlugin;
import org.springframework.context.ApplicationContext;

/**
 * @author tenx
 * SpringPlugin.
 */
public class SpringPlugin implements IPlugin {

    private ApplicationContext ctx;

    public SpringPlugin(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public boolean start() {
        if (ctx != null) {
            IocInterceptor.ctx = ctx;
        }
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
