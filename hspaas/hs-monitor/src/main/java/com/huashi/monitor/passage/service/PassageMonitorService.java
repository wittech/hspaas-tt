package com.huashi.monitor.passage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.mms.passage.domain.MmsPassageAccess;
import com.huashi.mms.passage.service.IMmsPassageAccessService;
import com.huashi.monitor.config.redis.MonitorRedisConstant;
import com.huashi.monitor.constant.MonitorConstant;
import com.huashi.monitor.job.ElasticJobManager;
import com.huashi.monitor.job.mms.MmsPassageMoReportPullJob;
import com.huashi.monitor.job.mms.MmsPassageMtReportPullJob;
import com.huashi.monitor.job.sms.SmsPassageMoReportPullJob;
import com.huashi.monitor.job.sms.SmsPassageMtReportPullJob;
import com.huashi.monitor.job.sms.SmsPassageReachRateReportJob;
import com.huashi.monitor.passage.model.PassagePullReport;
import com.huashi.monitor.passage.model.PassageReachRateReport;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.passage.domain.SmsPassageReachrateSettings;
import com.huashi.sms.passage.service.ISmsPassageAccessService;
import com.huashi.sms.passage.service.ISmsPassageReachrateSettingsService;

@Service
public class PassageMonitorService implements IPassageMonitorService {

    @Resource
    private StringRedisTemplate                 stringRedisTemplate;

    @Autowired
    private ElasticJobManager                   elasticJobManager;

    @Value("${elasticJob.item.commonLevelPull.cron}")
    private String                              commonLevelPullCron;
    @Value("${elasticJob.item.commonLevelPull.shardingCount:1}")
    private int                                 commonLevelPullShardingCount;
    @Value("${elasticJob.item.commonLevelPull.shardingItemParameters}")
    private String                              commonLevelPullShardingItemParameters;

    @Value("${elasticJob.item.lowLevelPull.cron}")
    private String                              lowLevelPullCron;
    @Value("${elasticJob.item.lowLevelPull.shardingCount:1}")
    private int                                 lowLevelPullShardingCount;
    @Value("${elasticJob.item.lowLevelPull.shardingItemParameters}")
    private String                              lowLevelPullShardingItemParameters;

    /**
     * 每秒钟间隔单位值
     */
    private static final int                    TIME_UNIT = 60;

    @Autowired
    private SmsPassageMtReportPullJob           smsPassageMtReportPullJob;
    @Autowired
    private SmsPassageMoReportPullJob           smsPassageMoReportPullJob;
    @Autowired
    private SmsPassageReachRateReportJob        smsPassageReachRateReportJob;

    @Autowired
    private MmsPassageMtReportPullJob           mmsPassageMtReportPullJob;
    @Autowired
    private MmsPassageMoReportPullJob           mmsPassageMoReportPullJob;

    @Reference(mock = "return null")
    private ISmsPassageAccessService            smsPassageAccessService;
    @Reference(mock = "return null")
    private IMmsPassageAccessService            mmsPassageAccessService;
    @Reference
    private ISmsPassageReachrateSettingsService smsPassageReachrateSettingsService;

    private final Logger                        logger    = LoggerFactory.getLogger(getClass());

    /**
     * TODO 通道下行状态扫描
     */
    private void doSmsPassageStatusPulling() {
        List<SmsPassageAccess> list = smsPassageAccessService.findWaitPulling(PassageCallType.MT_STATUS_RECEIPT_WITH_SELF_GET);
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("未检索到短信通道下行状态报告回执");
            return;
        }

        for (SmsPassageAccess access : list) {
            elasticJobManager.addJobScheduler(smsPassageMtReportPullJob, commonLevelPullCron,
                                              commonLevelPullShardingCount, commonLevelPullShardingItemParameters,
                                              JSON.toJSONString(access));
        }
    }

    /**
     * TODO 通道上行回执数据扫描
     */
    private void doSmsPassageMoPulling() {
        List<SmsPassageAccess> list = smsPassageAccessService.findWaitPulling(PassageCallType.MO_REPORT_WITH_SELF_GET);
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("未检索到短信通道上行报告回执");
            return;
        }

        for (SmsPassageAccess access : list) {
            elasticJobManager.addJobScheduler(smsPassageMoReportPullJob, lowLevelPullCron, lowLevelPullShardingCount,
                                              lowLevelPullShardingItemParameters, JSON.toJSONString(access));
        }
    }

    /**
     * 彩信通道下行状态扫描
     */
    private void doMmsPassageStatusPulling() {
        List<MmsPassageAccess> list = mmsPassageAccessService.findWaitPulling(PassageCallType.MT_STATUS_RECEIPT_WITH_SELF_GET);
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("未检索到彩信通道下行状态报告回执");
            return;
        }

        for (MmsPassageAccess access : list) {
            elasticJobManager.addJobScheduler(mmsPassageMtReportPullJob, commonLevelPullCron,
                                              commonLevelPullShardingCount, commonLevelPullShardingItemParameters,
                                              JSON.toJSONString(access));
        }
    }

    /**
     * TODO 彩信通道上行回执数据扫描
     */
    private void doMmsPassageMoPulling() {
        List<MmsPassageAccess> list = mmsPassageAccessService.findWaitPulling(PassageCallType.MO_REPORT_WITH_SELF_GET);
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("未检索到短信通道上行报告回执");
            return;
        }

        for (MmsPassageAccess access : list) {
            elasticJobManager.addJobScheduler(mmsPassageMoReportPullJob, lowLevelPullCron, lowLevelPullShardingCount,
                                              lowLevelPullShardingItemParameters, JSON.toJSONString(access));
        }
    }

    private void doSmsPassageMonitorPulling() {
        try {
            StringBuilder passage = new StringBuilder();
            List<SmsPassageReachrateSettings> list = smsPassageReachrateSettingsService.getByUseable();
            for (SmsPassageReachrateSettings model : list) {
                elasticJobManager.addJobScheduler(smsPassageReachRateReportJob, secondsToCron(model.getInterval()),
                                                  lowLevelPullShardingCount, lowLevelPullShardingItemParameters,
                                                  model.getId().toString());
            }

            logger.info("通道回执率监控已开启：{}", passage.toString());
        } catch (Exception e) {
            logger.info("通道回执率监控启动失败：{}", e);
        }

    }

    @Override
    public List<PassagePullReport> findPassagePullReport() {
        try {
            Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP);
            if (MapUtils.isNotEmpty(map)) {
                List<PassagePullReport> reports = new ArrayList<>();

                map.forEach((k, v) -> {
                    reports.add(JSON.parseObject(v.toString(), PassagePullReport.class));
                });
                return reports;
            }

        } catch (Exception e) {
            logger.error("查询带轮训通道队列报告失败", e);
        }

        return null;
    }

    @Override
    public void flushPullReport() {
        try {
            Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP);
            logger.info("通道轮训重启清除数据：{}", MapUtils.isEmpty(map) ? "无" : map);

            stringRedisTemplate.delete(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP);

        } catch (Exception e) {
            logger.error("清除轮训通道数据异常", e);
        }

    }

    @Override
    public boolean startPassagePull() {
        try {
            flushPullReport();

            doSmsPassageStatusPulling();

            doSmsPassageMoPulling();

            doSmsPassageMonitorPulling();

            doMmsPassageStatusPulling();

            doMmsPassageMoPulling();

            return true;
        } catch (Exception e) {
            logger.error("开启通道轮训总开关失败", e);
            return false;
        }
    }

    @Override
    public boolean addPassagePull(SmsPassageAccess access) {
        String key = null;
        try {
            // key = getPassageThreadName(access.getPassageId(), access.getCallType());
            //
            // // 如果运行中通道已经有此通道信息了，则不需要再启动一个线程
            // if (BaseThread.PASSAGES_IN_RUNNING.containsKey(key) && BaseThread.PASSAGES_IN_RUNNING.get(key)) {
            // logger.info("当前线程组中已经存在此通道：{}信息，无需重新开启", key);
            // return true;
            // }

            logger.info("运行中轮训通道：{} 增加通道完成");

            return true;
        } catch (Exception e) {
            logger.info("运行中轮训通道：{} 增加通道失败", key, e);
        }

        return false;
    }

    @Override
    public boolean removePasagePull(SmsPassageAccess access) {
        try {
            // key = getPassageThreadName(access.getPassageId(), access.getCallType());
            //
            // BaseThread.PASSAGES_IN_RUNNING.put(key, false);
            //
            // Object obj = stringRedisTemplate.opsForHash().get(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP, key);
            // if (obj == null) {
            // logger.warn("REDIS 移除轮训通道：{} 失败，数据为空（已被移除或从未生效）");
            // return true;
            // }
            //
            // PassagePullReport report = JSON.parseObject(obj.toString(), PassagePullReport.class);
            // report.setStatus(PassagePullRunnintStatus.NO.getCode());
            //
            // stringRedisTemplate.opsForHash().put(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP,
            // key,
            // JSON.toJSONString(report, SerializerFeature.WriteMapNullValue,
            // SerializerFeature.WriteNullStringAsEmpty));
            //
            // logger.info("运行中轮训通道：{} 终止操作完成", key);

            return true;
        } catch (Exception e) {
            logger.info("运行中轮训通道：{} 终止操作失败", e);
        }

        return false;
    }

    @Override
    public boolean updatePullReport(PassagePullReport report) {
        return false;
    }

    @Override
    public boolean updatePullReportToRedis(String key, PassagePullReport report) {
        try {
            Object obj = stringRedisTemplate.opsForHash().get(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP, key);
            if (obj == null) {
                logger.warn("REDIS 移除轮训通道：{} 失败，数据为空（已被移除或从未生效）");
                return true;
            }

            PassagePullReport plainReport = JSON.parseObject(obj.toString(), PassagePullReport.class);
            plainReport.setIntevel(report.getIntevel());
            plainReport.setLastTime(report.getLastTime());
            plainReport.setLastAmount(report.getLastAmount());
            plainReport.setCostTime(report.getCostTime());
            plainReport.setPullAvaiableTimes(report.getPullAvaiableTimes());

            stringRedisTemplate.opsForHash().put(MonitorConstant.RD_PASSAGE_PULL_THREAD_GROUP,
                                                 key,
                                                 JSON.toJSONString(plainReport, SerializerFeature.WriteMapNullValue,
                                                                   SerializerFeature.WriteNullStringAsEmpty));

            return true;

        } catch (Exception e) {
            logger.error("更新通道轮训线程：{} 失败", e);
        }
        return false;
    }

    @Override
    public boolean addSmsPassageMonitor(SmsPassageReachrateSettings model) {
        try {
            elasticJobManager.addJobScheduler(smsPassageReachRateReportJob, secondsToCron(model.getInterval()),
                                              lowLevelPullShardingCount, lowLevelPullShardingItemParameters,
                                              model.getId().toString());

            return true;
        } catch (Exception e) {
            logger.error("添加通道监控失败！", e);
        }
        return false;
    }

    /**
     * TODO 从REDIS KEY中摘取通道ID
     * 
     * @param key
     * @return
     */
    private static Integer pickPassageIdFromKey(String key) {
        try {
            return Integer.parseInt(key.split(":")[1]);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<Integer, List<PassageReachRateReport>> findReachrateReport(Integer passageId) {
        try {
            Map<Integer, List<PassageReachRateReport>> report = new HashMap<Integer, List<PassageReachRateReport>>();
            List<PassageReachRateReport> list = null;

            if (passageId == null) {
                // 查询所有符合条件的KEYS
                Set<String> keys = stringRedisTemplate.keys(MonitorRedisConstant.RED_PASSAGE_REACHRATE_REPORT + "*");
                if (CollectionUtils.isEmpty(keys)) {
                    return null;
                }

                for (String key : keys) {
                    passageId = pickPassageIdFromKey(key);
                    if (passageId == null) {
                        continue;
                    }

                    list = getReachrateReport(key);
                    if (CollectionUtils.isEmpty(list)) {
                        continue;
                    }

                    report.put(passageId, list);
                }

                return report;
            }

            list = getReachrateReport(String.format("%s:%d", MonitorRedisConstant.RED_PASSAGE_REACHRATE_REPORT,
                                                    passageId));
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }

            report.put(passageId, list);

            return report;
        } catch (Exception e) {
            logger.error("通道到达率统计报告查询REDIS失败", e);
            return null;
        }
    }

    private List<PassageReachRateReport> getReachrateReport(String key) {
        try {
            Set<String> container = stringRedisTemplate.opsForZSet().reverseRangeByScore(key, 0,
                                                                                         System.currentTimeMillis());
            if (CollectionUtils.isEmpty(container)) {
                return null;
            }

            List<PassageReachRateReport> list = new ArrayList<PassageReachRateReport>(container.size());
            for (String report : container) {
                list.add(JSON.parseObject(report, PassageReachRateReport.class));
            }

            return list;
        } catch (Exception e) {
            logger.error("REDIS key : {} 获取通道到达率报告失败", e);
            return null;
        }
    }

    /**
     * 秒值转CRON表达式
     */
    private String secondsToCron(long senconds) {
        if (senconds == 0) {
            return lowLevelPullCron;
        }

        // 目前页面配置的均为分钟且不超过60分钟
        return minuteCron(senconds / TIME_UNIT);
    }

    private static String minuteCron(long minutes) {

        if (minutes / TIME_UNIT > (TIME_UNIT)) {
            return String.format("* * */%d * * ?", minutes);
        }

        return String.format("* */%d * * * ?", minutes);
    }

}
