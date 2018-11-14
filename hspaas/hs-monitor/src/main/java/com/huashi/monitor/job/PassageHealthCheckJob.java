package com.huashi.monitor.job;

import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.api.ShardingContext;

/**
 * TODO 通道健康检查
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2017年2月22日 下午8:32:46
 */
@Service("passageHealthCheckJob")
public class PassageHealthCheckJob extends AbtractJob {

    @Override
    public void run(ShardingContext shardingContext) {
        // 后续实现
    }

}
