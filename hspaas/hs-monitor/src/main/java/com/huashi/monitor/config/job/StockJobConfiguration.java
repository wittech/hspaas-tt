package com.huashi.monitor.config.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.huashi.monitor.job.StockSimpleJob;

//@Configuration
public class StockJobConfiguration {

    // @Autowired
    // private JobRegistryCenterConfiguration jobRegistryCenterConfiguration;
    @Autowired
    private ZookeeperRegistryCenter registryCenter;

    public StockJobConfiguration() {
    }

    @Bean
    public SimpleJob stockJob() {
        return new StockSimpleJob();
    }

    @Bean(initMethod = "init")
    public JobScheduler simpleJobScheduler(final SimpleJob simpleJob, @Value("${stockJob.cron}")
    final String cron, @Value("${stockJob.shardingTotalCount}")
    final int shardingTotalCount, @Value("${stockJob.shardingItemParameters}")
    final String shardingItemParameters) {
        return new SpringJobScheduler(simpleJob, registryCenter, getLiteJobConfiguration(simpleJob.getClass(), cron,
                                                                                         shardingTotalCount,
                                                                                         shardingItemParameters));
    }

    /**
     * @Description 任务配置类
     */
    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron,
                                                         final int shardingTotalCount,
                                                         final String shardingItemParameters) {

        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(
                                                                          JobCoreConfiguration.newBuilder(jobClass.getName(),
                                                                                                          cron,
                                                                                                          shardingTotalCount).shardingItemParameters(shardingItemParameters).build(),
                                                                          jobClass.getCanonicalName())).overwrite(true).build();

    }
}
