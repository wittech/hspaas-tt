package com.huashi.sms.config.dubbo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.alibaba.dubbo.config.ApplicationConfig;

//@Configuration
public class DubboConfiguration {
	
	@Value("${dubbo.appname}")
	private String applicationName;

	@Bean
	public ApplicationConfig applicationConfig() {
		ApplicationConfig applicationConfig = new ApplicationConfig(applicationName);
		applicationConfig.setLogger("slf4j");
//		applicationConfig.setOrganization("huashi");
		
		return applicationConfig;
	}
	
	
//	<dubbo:application name="hssms-provider" logger="slf4j" />
//
//    <dubbo:registry protocol="zookeeper" address="${zk.address}" timeout="15000" file="${user.home}/applications/hssms-dubbo.cache"/>
//
//    <dubbo:provider timeout="100000" retries="0" protocol="dubbo" default="true"/>
//    <dubbo:protocol name="dubbo" serialization="kryo" port="-1"/>
//
//    <dubbo:protocol name="hessian" port="-1"/>
//
//    <dubbo:annotation package="com.huashi"/>
//
//    <!-- 定义全局的配置，测试中@Reference的check=false不起作用，在这里配置 -->
//    <dubbo:consumer check="false"/>
}
