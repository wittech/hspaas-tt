package com.huashi.monitor.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

/**
 * TODO 任务抽象类
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年11月13日 下午5:58:20
 */
public abstract class AbtractJob implements SimpleJob {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public final void execute(ShardingContext context) {
        long startTime = System.currentTimeMillis();

        try {
            run(context);
        } finally {
            // System.out.println(String.format("------Thread ID: %s, 任务总片数: %s, " + "当前分片项: %s.当前参数: %s,"
            // + "当前任务名称: %s.当前任务参数: %s", Thread.currentThread().getId(),
            // shardingContext.getShardingTotalCount(),
            // shardingContext.getShardingItem(), shardingContext.getShardingParameter(),
            // shardingContext.getJobName(), shardingContext.getJobParameter()));
            long cost = System.currentTimeMillis() - startTime;
            logger.info("[" + context.getShardingTotalCount() + "-" + context.getShardingParameter()
                        + "] executing cost " + cost + " ms");

        }

    }

    public abstract void run(ShardingContext shardingContext);
}
