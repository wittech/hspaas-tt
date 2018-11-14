package com.huashi.monitor.config.job;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

@Configuration
@ConditionalOnExpression("'${elasticJob.regCenter.servers}'.length() > 0")
public class JobRegistryCenterConfiguration {

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter regCenter(@Value("${elasticJob.regCenter.servers}")
    final String serverList, @Value("${elasticJob.regCenter.namespace}")
    final String namespace) {
        return new ZookeeperRegistryCenter(new ZookeeperConfiguration(serverList, namespace));
    }
}
