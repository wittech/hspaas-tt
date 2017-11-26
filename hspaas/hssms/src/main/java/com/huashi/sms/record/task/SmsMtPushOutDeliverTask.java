package com.huashi.sms.record.task;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.service.ISmsMtPushService;

@Component
public class SmsMtPushOutDeliverTask {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private ISmsMtPushService smsMtPushService;

	/**
	 * 
	   * TODO 针对已回执数据但 下发消息未及时入库数据做 补偿对账 (每5秒执行一次)
	   * @return
	 */
	@Scheduled(cron = "0/2 * * * * ?")
	public void doPushInSubmitNotFound() {
		try {
//			stringRedisTemplate.watch(SmsRedisConstant.RED_MESSAGE_DELIVED_WAIT_PUSH_LIST);
//				listOperations.rightPush(SmsRedisConstant.RED_MESSAGE_DELIVED_WAIT_PUSH_LIST, getHotPushKey(deliver.getMsgId(), deliver.getMobile()), 
//						JSON.toJSONString(deliver, new SimplePropertyPreFilter("mobile", "statusCode", "deliverTime")));
			long start = System.currentTimeMillis();
			List<String> list = stringRedisTemplate.opsForList().range(SmsRedisConstant.RED_MESSAGE_DELIVED_WAIT_PUSH_LIST, 0, 1999);
			
			if(CollectionUtils.isEmpty(list))
				return;
			
			logger.info("本次补偿记录中数据共 {} 条" , list.size());
			start = System.currentTimeMillis();
			List<SmsMtMessageDeliver> delivers = new ArrayList<SmsMtMessageDeliver>();
			for (String t : list) {
				List<SmsMtMessageDeliver> dd = JSON.parseObject(t, new TypeReference<List<SmsMtMessageDeliver>>(){});
				if(CollectionUtils.isEmpty(dd))
					continue;
				
				delivers.addAll(dd);
			}
			
			logger.info("deliver组装耗时：{}" , System.currentTimeMillis() -start);
			
			start = System.currentTimeMillis();
			// 如果待处理数据不为空，则发送至相关推送中
			if(CollectionUtils.isNotEmpty(delivers))
				smsMtPushService.sendToWaitPushQueue(delivers);
			
			logger.info("sendToWaitPushQueue耗时：{}" , System.currentTimeMillis() -start);
			
			start = System.currentTimeMillis();
			stringRedisTemplate.opsForList().trim(SmsRedisConstant.RED_MESSAGE_DELIVED_WAIT_PUSH_LIST, list.size(), -1);
			logger.info("trim耗时：{}" , System.currentTimeMillis() -start);
			
			delivers = null;
		} catch (Exception e) {
			logger.error("推送补偿对账异常", e);
		}
	}
	
}
