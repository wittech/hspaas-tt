package com.huashi.developer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

import io.undertow.Undertow.Builder;

//@Configuration
public class WebServerConfiguration {
	
	@Value("${developer.server.port:8080}")
	private int serverPort;
	
	@Value("${developer.server.session-timeout:30}")
	private int sessionTimeout;
	
	@Value("${developer.server.context-path:/}")
	private String contextPath;
	

	@Bean
	public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() {
		UndertowEmbeddedServletContainerFactory undertowFactory = new UndertowEmbeddedServletContainerFactory();
		undertowFactory.setPort(serverPort);
		undertowFactory.setSessionTimeout(sessionTimeout);
		undertowFactory.setContextPath(contextPath);
//		undertowFactory.setWorkerThreads(workerThreads);
		
//		undertowFactory.setIoThreads(ioThreads);
//		undertowFactory.setInitializers(initializers);
		undertowFactory.addBuilderCustomizers(new UndertowBuilderCustomizer() {

			@Override
			public void customize(Builder builder) {
				// TODO Auto-generated method stub
				
			}});
		
		return undertowFactory;
		
//		TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
//		tomcatFactory.setPort(8081);
//		tomcatFactory.addConnectorCustomizers(new MyTomcatConnectorCustomizer());
//		return tomcatFactory;
	}

	class DeveloperConnectorCustomizer implements UndertowBuilderCustomizer {
		
		
//		@Override
//		public void customize(Connector arg0) {
//			Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
//			// 设置最大连接数
//			protocol.setMaxConnections(2000);
//			// 设置最大线程数
//			protocol.setMaxThreads(2000);
//			protocol.setConnectionTimeout(30000);
//		}

		@Override
		public void customize(Builder builder) {
			
		}
	}
}
