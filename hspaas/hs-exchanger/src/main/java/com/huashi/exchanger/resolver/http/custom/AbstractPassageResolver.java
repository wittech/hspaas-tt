package com.huashi.exchanger.resolver.http.custom;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.huashi.common.util.DateUtil;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

public abstract class AbstractPassageResolver {
	
	@Resource
	protected StringRedisTemplate stringRedisTemplate;

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	// 下行状态HTTP状态报告REDIS前置（主要用于状态回执报告中没有手机号码，顾发送短信需要提前设置MSG_ID和MOBILE对应关系）
	protected static final String REDIS_MT_REPORT_HTTP_PRIFIX_KEY = "mt_http_map";

	// 公共状态回执成功码
	public static final String COMMON_MT_STATUS_SUCCESS_CODE = "DELIVRD";

	/**
	 * 
	 * TODO 发送短信（提交至通道商）
	 * 
	 * @param parameter
	 *            通道参数
	 * @param mobile
	 *            手机号码
	 * @param content
	 *            短信内容
	 * @param extNumber
	 *            用户扩展号码
	 * @return
	 */
	public abstract List<ProviderSendResponse> send(SmsPassageParameter parameter, String mobile, String content,
			String extNumber);

	/**
	 * 
	 * TODO 下行状态报告回执(推送)
	 * 
	 * @param report
	 * @return
	 */
	public abstract List<SmsMtMessageDeliver> mtDeliver(String report, String successCode);

	/**
	 * 
	 * TODO 下行状态报告回执（自取）
	 * 
	 * @param report
	 * @return
	 */
	public abstract List<SmsMtMessageDeliver> mtPullDeliver(TParameter tparameter, String url, String successCode);

	/**
	 * 
	 * TODO 上行短信状态回执
	 * 
	 * @param report
	 * @return
	 */
	public abstract List<SmsMoMessageReceive> moReceive(String report, Integer passageId);

	/**
	 * 
	 * TODO 上行短信状态回执
	 * 
	 * @param report
	 * @return
	 */
	public abstract List<SmsMoMessageReceive> moPullReceive(TParameter tparameter, String url, Integer passageId);

	/**
	 * 
	 * TODO 用户余额查询
	 * 
	 * @param param
	 * @return
	 */
	public abstract Object balance(Object param);

	/**
	 * 
	 * TODO 简码
	 * 
	 * @return
	 */
	public abstract String code();

	/**
	 * 
	 * TODO unixtime 转时间戳
	 * 
	 * @param timestampString
	 * @return
	 */
	protected static String unixtimeStamp2Date(Object timestampString) {
		if(timestampString == null || StringUtils.isEmpty(timestampString.toString()))
			return DateUtil.getNow();
		
		try {
			Long timestamp = Long.parseLong(timestampString.toString()) * 1000;
			return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp));
		} catch (Exception e) {
			return DateUtil.getNow();
		}
	}
	
	/**
	 * 
	   * TODO 时间数字格式（yyyyMMddHHmmss，如20110115105822）转换格式为yyyy-MM-dd HH:mm:ss 字符
	   * 
	   * @param dataNumber
	   * @return
	 */
	protected static String dateNumberFormat(Object dataNumber) {
		if(dataNumber == null || StringUtils.isEmpty(dataNumber.toString()))
			return DateUtil.getNow();
		
		try {
			SimpleDateFormat ff =  new java.text.SimpleDateFormat("yyyyMMddHHmmss");
			return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ff.parse(dataNumber.toString()));
		} catch (Exception e) {
			return DateUtil.getNow();
		}
	}
	
	/**
	 * 
	   * TODO 获取HTTP通道发送 消息ID对应手机号码REDIS KEY
	   * @param msgId
	   * @return
	 */
	private String getRedisMtMsgIdKey(String msgId) {
		return String.format("%s:%s:%s", REDIS_MT_REPORT_HTTP_PRIFIX_KEY, code(), msgId);
	}
	
	/**
	 * 
	   * TODO 设置发送报告MSG_ID和手机号码对应关系至REDIS
	   * @param msgId
	   * @param mobile
	 */
	protected void setReportMsgIdWithMobile(String msgId, String mobile) {
		try {
			stringRedisTemplate.opsForValue().set(getRedisMtMsgIdKey(msgId), mobile);
		} catch (Exception e) {
			logger.error("Redis 设置发送状态MSG_ID: {} 和MOBILE : {} 对应关系失败", msgId, mobile, e);
		}
	}
	
	/**
	 * 
	   * TODO 获取发送报告MSG_ID和手机号码对应关系至REDIS
	   * @param msgId
	   * @return
	 */
	protected String getReportMsgIdWithMobile(String msgId) {
		try {
			Object obj = stringRedisTemplate.opsForValue().get(getRedisMtMsgIdKey(msgId));
			if(obj == null) {
				logger.error("Redis 获取发送状态MSG_ID: {} 数据为空", msgId);
				return null;
			}
				
			
			return obj.toString();
		} catch (Exception e) {
			logger.error("Redis 获取发送状态MSG_ID: {} 和MOBILE对应关系失败", msgId, e);
			return null;
		}
	}
	
	/**
	 * 
	   * TODO 移除发送报告MSG_ID和手机号码对应关系至REDIS
	   * @param msgId
	 */
	protected void removeReportMsgIdWithMobile(String msgId) {
		try {
			stringRedisTemplate.delete(getRedisMtMsgIdKey(msgId));
			
		} catch (Exception e) {
			logger.error("Redis 移除发送状态MSG_ID: {} 和MOBILE对应关系失败", msgId, e);
		}
	}
	
}
