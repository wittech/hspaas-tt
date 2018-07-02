package com.huashi.listener.config;

import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServerConfiguration {

	@Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcatServletContainerFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatServletContainerFactory.addContextCustomizers(new TomcatContextCustomizer(){
 
            @Override
            public void customize(org.apache.catalina.Context context) {
                SecurityConstraint constraint = new SecurityConstraint();
                SecurityCollection collection = new SecurityCollection();
                //http方法
                collection.addMethod("PUT");
                collection.addMethod("DELETE");
                collection.addMethod("HEAD");
                collection.addMethod("OPTIONS");
                collection.addMethod("TRACE");
                //url匹配表达式
                collection.addPattern("/*");
                constraint.addCollection(collection);
                constraint.setAuthConstraint(true);
                context.addConstraint(constraint );
                
                //设置使用httpOnly
                context.setUseHttpOnly(true);
            }
        });
        return tomcatServletContainerFactory;
	}

}
