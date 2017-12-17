package com.huashi.sms.settings.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.util.DateUtil;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;

/**
 * 
  * TODO 手机号码防火墙设置服务实现
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2016年12月15日 上午10:08:47
 */
@Service
public class SmsMobileTablesService implements ISmsMobileTablesService {
	
	// 手机号码最后发送毫秒时间
	private static final String MOBILE_LAST_SEND_MILLIS = "last_send_millis";
	// 手机号码发送总数
	private static final String MOBILE_SEND_TOTAL_COUNT = "send_total_count";
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 
	   * TODO 组合生成辅助KEY
	   * @param keyname
	   * @param userId
	   * @param templateId
	   * @param mobile
	   * @return
	 */
	private String getAssistKey(String keyname, int userId, Long templateId, String mobile) {
		return String.format("%s:%d:%d:%s", keyname, userId, templateId, mobile);
	}

	/**
	 * 
	   * TODO 获取距离次日0点的毫秒时间差(REDIS TTL自动过期)
	   * @return
	 */
	private static long getH24TimeMillis() {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		try {
			Date endDate = dfs.parse(DateUtil.getDayGoXday(1) + " 00:00:00.000");
            
			return endDate.getTime() - System.currentTimeMillis();
		} catch (Exception e) {
			// 默认6小时
			return System.currentTimeMillis() + 6 * 60 * 60 * 1000;
		}
	}
	
	@Override
	public void setMobileSendRecord(int userId, Long templateId, String mobile, int sendCount) {
		if(userId == 0 || templateId == null || StringUtils.isEmpty(mobile)) {
			logger.error("参数数据为空，无法匹配该用户：{}, 模板：{}，手机号码：{} ", userId, templateId, mobile);
			return;
		}
		
		try {
			String key = getAssistKey(SmsRedisConstant.RED_MOBILE_GREEN_TABLES, userId, templateId, mobile);
			
			Map<Object, Object> map = new HashMap<>();
			map.put(MOBILE_LAST_SEND_MILLIS, System.currentTimeMillis() + "");
			map.put(MOBILE_SEND_TOTAL_COUNT, sendCount + "");
			
			stringRedisTemplate.opsForHash().putAll(key, map);
			stringRedisTemplate.expire(key, getH24TimeMillis(), TimeUnit.MILLISECONDS);
			
		} catch (Exception e) {
			logger.error("REDIS 手机号防火墙设置失败，请优先处理", e);
		}
	}
	
	@Override
	public int checkMobileIsBeyondExpected(int userId, Long templateId, String mobile, 
			int maxSpeed, int maxLimit) {
		
		// 如果提交频率为0并且一天内上限大于等于9999则不限制提交任何（也无需记录用户访问轨迹） edit by 20170813
		if(maxSpeed == 0 && maxLimit >= 9999) {
            return NICE_PASSED;
        }
		
		Integer _sendTotalCount = null;
		try {
			Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(getAssistKey(SmsRedisConstant.RED_MOBILE_GREEN_TABLES, 
					userId, templateId, mobile));
			if(MapUtils.isEmpty(map)) {
				setMobileSendRecord(userId, templateId, mobile, _sendTotalCount == null ? 0 : _sendTotalCount.intValue());
				return NICE_PASSED;
			}
			
			// 当前发送的总量
			Object sendTotalCount = map.get(MOBILE_SEND_TOTAL_COUNT);
			_sendTotalCount = Integer.parseInt(sendTotalCount.toString()) + 1;

			// 如果上限次数为0，则不允许提交任何
			if(maxLimit == 0) {
                return MOBILE_BEYOND_TIMES;
            }
			
			// 判断手机号码一天内发送总量是否已超过预设值
			if(_sendTotalCount >= maxLimit) {
                return MOBILE_BEYOND_TIMES;
            }
			
			// 如果提交频率为0，则不限制提交任何
			if(maxSpeed == 0) {
				setMobileSendRecord(userId, templateId, mobile, _sendTotalCount == null ? 0 : _sendTotalCount.intValue());
				return NICE_PASSED;
			}
			
			// 判断	手机号码上次发送时间距离当前时间否已超过预设值
			Object lastSendMillis = map.get(MOBILE_LAST_SEND_MILLIS);
			if(System.currentTimeMillis() - Long.parseLong(lastSendMillis.toString()) < maxSpeed * 1000) {
                return MOBILE_BEYOND_SPEED;
            }
			
			setMobileSendRecord(userId, templateId, mobile, _sendTotalCount == null ? 0 : _sendTotalCount.intValue());
		} catch (Exception e) {
			logger.warn("REDIS查询手机号码白名单失败，数据默认通过", e);
		}
		return NICE_PASSED;
	}

}
