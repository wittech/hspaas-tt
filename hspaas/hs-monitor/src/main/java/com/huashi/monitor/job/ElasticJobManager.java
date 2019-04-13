package com.huashi.monitor.job;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.huashi.monitor.job.sms.SmsSubmitReportBornJob;

/**
 * TODO 分布式调度配置
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年11月13日 下午5:54:27
 */
@Component
public class ElasticJobManager {

    private final Logger            logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ZookeeperRegistryCenter registryCenter;

    @Value("${elasticJob.item.submitReport.cron}")
    private String                  submitReportCron;
    @Value("${elasticJob.item.submitReport.shardingCount:1}")
    private int                     submitReportShardingCount;
    @Value("${elasticJob.item.submitReport.shardingItemParameters}")
    private String                  submitReportShardingItemParameters;

    @Value("${elasticJob.item.userBalanceCheck.cron}")
    private String                  userBalanceCheckCron;
    @Value("${elasticJob.item.userBalanceCheck.shardingCount:1}")
    private int                     userBalanceCheckShardingCount;
    @Value("${elasticJob.item.userBalanceCheck.shardingItemParameters}")
    private String                  userBalanceCheckShardingItemParameters;

    @Value("${elasticJob.item.commonLevelPull.cron}")
    private String                  commonLevelPullCron;
    @Value("${elasticJob.item.commonLevelPull.shardingCount:1}")
    private int                     commonLevelPullShardingCount;
    @Value("${elasticJob.item.commonLevelPull.shardingItemParameters}")
    private String                  commonLevelPullShardingItemParameters;

    @Autowired
    @Qualifier("smsSubmitReportBornJob")
    private SmsSubmitReportBornJob  smsSubmitReportBornJob;

    @Autowired
    @Qualifier("userBalanceCheckJob")
    private UserBalanceCheckJob     userBalanceCheckJob;

    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron,
                                                         final int shardingCount, final String shardingItemParameters) {
        return getLiteJobConfiguration(jobClass, cron, shardingCount, shardingItemParameters, null);
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
    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron,
                                                         final int shardingCount, final String shardingItemParameters,
                                                         final String jobParameter) {

        JobCoreConfiguration builder = JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingCount).shardingItemParameters(shardingItemParameters).jobParameter(jobParameter).build();

        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(builder, jobClass.getCanonicalName())).overwrite(true).build();

    }

    /**
     * 添加JOB
     * 
     * @param elasticJob
     * @param cron
     * @param shardingCount
     * @param shardingItemParameters
     */
    public void addJobScheduler(SimpleJob elasticJob, String cron, int shardingCount, String shardingItemParameters) {
        addJobScheduler(elasticJob, cron, shardingCount, shardingItemParameters, null);
    }

    public void addJobScheduler(SimpleJob elasticJob, String cron, int shardingCount, String shardingItemParameters,
                                String jobParameter) {
        new SpringJobScheduler(elasticJob, registryCenter,
                               getLiteJobConfiguration(elasticJob.getClass(), cron, shardingCount,
                                                       shardingItemParameters, jobParameter)).init();
    }

    public void addJobScheduler(SimpleJob elasticJob) {
        new SpringJobScheduler(elasticJob, registryCenter,
                               getLiteJobConfiguration(elasticJob.getClass(), commonLevelPullCron,
                                                       commonLevelPullShardingCount,
                                                       commonLevelPullShardingItemParameters)).init();
    }

    @PostConstruct
    public void starter() {
        try {
            smsSubmitReportJobScheduler();

            userBalanceCheckJobScheduler();

            logger.info("Elastic job [smsSubmitReportJobScheduler, userBalanceCheckJobScheduler] init finished");

        } catch (Exception e) {
            logger.info("Elastic job [smsSubmitReportJobScheduler, userBalanceCheckJobScheduler] init failed", e);
        }
    }

    /**
     * TODO 短信小时报表生成
     */
    private void smsSubmitReportJobScheduler() {
        addJobScheduler(smsSubmitReportBornJob, submitReportCron, submitReportShardingCount,
                        submitReportShardingItemParameters);
    }

    /**
     * 用户余额检查
     */
    private void userBalanceCheckJobScheduler() {
        addJobScheduler(userBalanceCheckJob, userBalanceCheckCron, userBalanceCheckShardingCount,
                        userBalanceCheckShardingItemParameters);
    }
}
