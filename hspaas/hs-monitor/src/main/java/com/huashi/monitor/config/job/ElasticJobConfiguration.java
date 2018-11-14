package com.huashi.monitor.config.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.huashi.monitor.job.SmsSubmitReportBornJob;
import com.huashi.monitor.job.UserBalanceCheckJob;

/**
 * TODO 分布式调度配置
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年11月13日 下午5:54:27
 */
@Configuration
public class ElasticJobConfiguration {

    // @Autowired
    // private JobRegistryCenterConfiguration jobRegistryCenterConfiguration;
    @Autowired
    private ZookeeperRegistryCenter registryCenter;

    @Autowired
    @Qualifier("smsSubmitReportBornJob")
    private SmsSubmitReportBornJob  smsSubmitReportBornJob;

    @Autowired
    @Qualifier("userBalanceCheckJob")
    private UserBalanceCheckJob     userBalanceCheckJob;

    /**
     * TODO 短信下行小时报表调度器
     * 
     * @param cron
     * @param shardingCount
     * @param shardingItemParameters
     * @return
     */
    @Bean(initMethod = "init")
    public JobScheduler smsSubmitReportJobScheduler(@Value("${elasticJob.item.submitReport.cron}")
    final String cron, @Value("${elasticJob.item.submitReport.shardingCount:1}")
    final int shardingCount, @Value("${elasticJob.item.submitReport.shardingItemParameters}")
    final String shardingItemParameters) {
        return new SpringJobScheduler(smsSubmitReportBornJob, registryCenter,
                                      getLiteJobConfiguration(smsSubmitReportBornJob.getClass(), cron, shardingCount,
                                                              shardingItemParameters));
    }

    @Bean(initMethod = "init")
    public JobScheduler userBalanceCheckJobScheduler(@Value("${elasticJob.item.userBalanceCheck.cron}")
    final String cron, @Value("${elasticJob.item.userBalanceCheck.shardingCount:1}")
    final int shardingCount, @Value("${elasticJob.item.userBalanceCheck.shardingItemParameters}")
    final String shardingItemParameters) {
        return new SpringJobScheduler(userBalanceCheckJob, registryCenter,
                                      getLiteJobConfiguration(userBalanceCheckJob.getClass(), cron, shardingCount,
                                                              shardingItemParameters));
    }

    /**
     * TODO 任务配置类
     * 
     * @param jobClass
     * @param cron
     * @param shardingCount
     * @param shardingItemParameters
     * @return
     */
    private static LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass,
                                                                final String cron, final int shardingCount,
                                                                final String shardingItemParameters) {

        JobCoreConfiguration builder = JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingCount).shardingItemParameters(shardingItemParameters).build();

        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(builder, jobClass.getCanonicalName())).overwrite(true).build();

    }
}
