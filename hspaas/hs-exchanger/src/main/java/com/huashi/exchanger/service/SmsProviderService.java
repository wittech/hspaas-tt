package com.huashi.exchanger.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import com.huashi.common.util.MobileNumberCatagoryUtil;
import com.huashi.constants.CommonContext.ProtocolType;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.exception.DataEmptyException;
import com.huashi.exchanger.exception.ExchangeProcessException;
import com.huashi.exchanger.resolver.cmpp.v2.CmppProxySender;
import com.huashi.exchanger.resolver.http.HttpResolver;
import com.huashi.exchanger.resolver.sgip.SgipProxySender;
import com.huashi.exchanger.resolver.smgp.SmgpProxySender;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

@Service
public class SmsProviderService implements ISmsProviderService {

	@Autowired
	private HttpResolver httpResolver;
	@Autowired
	private CmppProxySender cmppProxySender;
	@Autowired
	private SgipProxySender sgipProxySender;
	@Autowired
	private SmgpProxySender smgpProxySender;
	@Autowired
	private ISmsProxyManageService smsProxyManageService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	// 网关每个包分包手机号码上限数
	@Value("${gateway.mobiles-per-sencond:10}")
	private int gatewayMobilesPerSecond;
	
	@Override
	public List<ProviderSendResponse> doTransport(SmsPassageParameter parameter, String mobile, String content,
			Integer fee, String extNumber) throws ExchangeProcessException {
		
		validate(parameter);
		
		// 判断是否需要限流
		if(!isNeedLimitSpeed(parameter.getProtocol())) {
            return submitData2Gateway(parameter, mobile, content, extNumber);
        }

		// 获取通道对应的流速设置
		RateLimiter rateLimiter = getRateLimiter(parameter.getPassageId(), parameter.getPacketsSize());
		String[] mobiles =  mobile.split(MobileNumberCatagoryUtil.DATA_SPLIT_CHARCATOR);
		
		// 目前HTTP用途并不是很大（因为取决于HTTP自身的瓶颈）
		List<String[]> packets = doLimitSpeedInSecond(mobiles, getRateLimiterAmount(parameter.getProtocol(), fee), parameter.getPacketsSize());

		// 如果手机号码同时传递多个，需要对流速进行控制，多余流速的需要分批提交
		List<ProviderSendResponse> list = new ArrayList<ProviderSendResponse>(packets.size());
		for(String[] m : packets) {
			// 设置acquire 当前分包后的数量
			rateLimiter.acquire(Integer.parseInt(m[0]));
			List<ProviderSendResponse> tlist = submitData2Gateway(parameter, m[1], content, extNumber);
			if(CollectionUtils.isEmpty(tlist)) {
                continue;
            }
			
			list.addAll(tlist);
		}
		
		return list;
	}
	
	/**
	 * 默认限流计流数
	 */
	private static final Integer DEFAULT_RATE_LIMITER_TIMES = 1;
	
	/**
	 * 
	   * TODO 计算限流总次数
	   * 
	   * 根据协议类型判断多条短信是否以多次限流计数（直连按照计费条数计数，如4条（长短信）计4次，其他协议，如HTTP则按照一次计数）
	   * 
	   * @param protocol
	   * @param fee
	   * @return
	 */
	private static Integer getRateLimiterAmount(String protocol, Integer fee) {
		// 如果协议类型无法识别或者为空则按照 1次计数
		if(StringUtils.isEmpty(protocol)) {
            return DEFAULT_RATE_LIMITER_TIMES;
        }
		
		ProtocolType protocolType = ProtocolType.parse(protocol);
		if(protocolType != null && (protocolType == ProtocolType.CMPP2 || protocolType == ProtocolType.SGIP || protocolType == ProtocolType.SMGP)) {
            return fee;
        }
		
		return DEFAULT_RATE_LIMITER_TIMES;
	}
	
	/**
	 * 
	   * TODO 是否需要限流
	   * @param protocol
	   * @return
	 */
	private boolean isNeedLimitSpeed(String protocol) {
		if(StringUtils.isEmpty(protocol)) {
            return false;
        }
		
		ProtocolType protocolType = ProtocolType.parse(protocol);

		return !(protocolType == null || protocolType == ProtocolType.HTTP || protocolType == ProtocolType.WEBSERVICE);
	}
	
	/**
	 * 
	   * TODO 提交数据到网关（此网关可能是真正网关也可能是上家渠道） 
	   * @param parameter
	   * @param mobile
	   * @param content
	   * @param extNumber
	   * @return
	 */
	private List<ProviderSendResponse> submitData2Gateway(SmsPassageParameter parameter, String mobile, String content,
			String extNumber) {

		ProtocolType pt = ProtocolType.parse(parameter.getProtocol());
		if(pt == null) {
			logger.warn("协议类型未解析，跳过");
			return null;
		}

		List<ProviderSendResponse> list = null;
		switch (pt) {
		case HTTP: {
			list = httpResolver.post(parameter, mobile, content, extNumber);
			break;
		}
		case WEBSERVICE: {
			break;
		}
		// 当前CMPP2和CMPP3均走统一逻辑
		case CMPP2 :
		case CMPP3 : {
			list = cmppProxySender.send(parameter, extNumber, mobile, content);
			break;
		}
		case SGIP : {
			list = sgipProxySender.send(parameter, extNumber, mobile, content);
			break;
		}
		case SMGP : {
			list = smgpProxySender.send(parameter, extNumber, mobile, content);
			break;
		}
		default:
			logger.warn("协议类型未解析，跳过");
			break;
		}
		
		return list;
	}
	
	/**
	 * 
	   * TODO 对提交网关的数据进行1秒内限流
	   * 
	   * @param mobiles
	   * 		手机号码
	   * @param amount
	   * 		单个手机号码分流计数器数量
	   * @param packetsSize
	   * 		限流次数
	   * @return 
	   * 		数组：[0]手机号码个数，[1]逗号拼接后的手机号码
	 */
	public static List<String[]> doLimitSpeedInSecond(String[] mobiles, Integer amount, Integer packetsSize) {
		// 每组手机号码个数 ，分包数/短信条数，如 50/3,则每个包手机号码数
		int groupMobileSize = packetsSize / amount;
		// 总手机号码数
		int totalMobileSize = mobiles.length;
		// 分组数
		int groupSize = (totalMobileSize % groupMobileSize == 0) ? (totalMobileSize / groupMobileSize) : (totalMobileSize / groupMobileSize + 1);

		List<String[]> mobileData = new ArrayList<>(groupSize);
		StringBuilder builder;
		String[] report;
		int index;
		for (int i = 0; i < groupSize; i++) {
			int roundSize = 0;
			report = new String[2];
			builder = new StringBuilder();

			index = i * groupMobileSize;
			for (int j = 0; j < groupMobileSize && index < totalMobileSize; j++) {
				builder.append(mobiles[index++]).append(MobileNumberCatagoryUtil.DATA_SPLIT_CHARCATOR);
				roundSize ++;
			}
			
			report[0] = (roundSize * amount) + "";
			report[1] = builder.substring(0, builder.length() - 1);

			// 第0位为本次处理手机号码总个数，第1位为手机号码信息
			mobileData.add(report);
		}
		
		return mobileData;
	}
	
	/**
	 * 
	   * TODO 传递参数前校验
	   * 
	   * @param parameter
	   * @throws DataEmptyException
	 */
	private void validate(SmsPassageParameter parameter) throws DataEmptyException{
		if (StringUtils.isEmpty(parameter.getUrl())) {
            throw new DataEmptyException("调用URL或IP为空");
        }

		if (StringUtils.isEmpty(parameter.getParams())) {
            throw new DataEmptyException("调用参数为空");
        }

	}

	@Override
	public List<SmsMtMessageDeliver> doStatusReport(SmsPassageAccess access, JSONObject report) {
		try {
			return httpResolver.deliver(access, report);
		} catch (Exception e) {
			logger.error("解析回执数据失败", e);
			throw new RuntimeException("解析回执数据失败");
		}
	}
	
	@Override
	public List<SmsMtMessageDeliver> doPullStatusReport(
			SmsPassageAccess access) {
		try {
			return httpResolver.deliver(access);
		} catch (Exception e) {
			logger.error("解析回执数据失败", e);
			throw new RuntimeException("解析回执数据失败");
		}
	}

	@Override
	public List<SmsMoMessageReceive> doMoReport(SmsPassageAccess access, JSONObject report) {
		try {
			return httpResolver.mo(access, report);
		} catch (Exception e) {
			logger.error("解析上行数据失败", e);
			throw new RuntimeException("解析上行数据失败");
		}
	}

	@Override
	public List<SmsMoMessageReceive> doPullMoReport(SmsPassageAccess access) {
		try {
			return httpResolver.mo(access);
		} catch (Exception e) {
			logger.error("解析上行数据失败", e);
			throw new RuntimeException("解析上行数据失败");
		}
	}
	
	/**
     * 
       * TODO  获取限速信息
       * 
       * @param passageId
       * @param speed
       * @return
     */
    private static RateLimiter getRateLimiter(Integer passageId, Integer speed) {
        RateLimiter limiter = SmsProxyManageService.GLOBAL_RATE_LIMITERS.get(passageId);
        if (limiter == null) {
            ReentrantLock reentrantLock = new ReentrantLock();
            reentrantLock.tryLock();
            try {
                limiter = RateLimiter.create((speed == null || speed == 0) ? SmsProxyManageService.DEFAULT_LIMIT_SPEED : speed);
                SmsProxyManageService.GLOBAL_RATE_LIMITERS.put(passageId, limiter);
            } finally {
                reentrantLock.unlock();
            }
        }

        return limiter;
    }
	
}
