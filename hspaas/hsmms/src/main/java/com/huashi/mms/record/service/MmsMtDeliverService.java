package com.huashi.mms.record.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.mms.config.cache.redis.constant.MmsRedisConstant;
import com.huashi.mms.record.dao.MmsMtMessageDeliverMapper;
import com.huashi.mms.record.domain.MmsMtMessageDeliver;

/**
 * TODO 彩信回执服务实现
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月15日 下午5:59:16
 */
@Service
public class MmsMtDeliverService implements IMmsMtDeliverService {

    @Resource
    private StringRedisTemplate       stringRedisTemplate;
    @Autowired
    private MmsMtMessageDeliverMapper mmsMtMessageDeliverMapper;
    @Reference
    private IPushConfigService        pushConfigService;
    @Autowired
    private IMmsMtPushService         mmsMtPushService;
    @Resource
    private ThreadPoolTaskExecutor    threadPoolTaskExecutor;

    private Logger                    logger = LoggerFactory.getLogger(getClass());

    @Override
    public MmsMtMessageDeliver findByMobileAndMsgid(String mobile, String msgId) {
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(msgId)) {
            return null;
        }

        return mmsMtMessageDeliverMapper.selectByMobileAndMsgid(msgId, mobile);
    }

    @Override
    public void batchInsert(List<MmsMtMessageDeliver> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        mmsMtMessageDeliverMapper.batchInsert(list);
    }

    @Override
    public int doFinishDeliver(List<MmsMtMessageDeliver> delivers) {
        try {

            if (CollectionUtils.isEmpty(delivers)) {
                return 0;
            }

            // 将待推送消息发送至用户队列进行处理（2017-03-20 合包处理），异步执行
            mmsMtPushService.compareAndPushBody(delivers);
            
            batchInsert(delivers);

            return delivers.size();
        } catch (Exception e) {
            logger.error("处理待回执信息REDIS失败，失败信息：{}", JSON.toJSONString(delivers), e);
            throw new RuntimeException("状态报告回执处理失败");
        }
    }

    @Override
    public boolean doDeliverToException(JSONObject obj) {
        try {
            return stringRedisTemplate.opsForList().rightPush(MmsRedisConstant.RED_MMS_MO_RECEIPT_EXCEPTION_LIST,
                                                              JSON.toJSONString(obj)) > 0;
        } catch (Exception e) {
            logger.error("发送回执错误信息失败 {}", JSON.toJSON(obj), e);
            return false;
        }
    }

}
