package com.huashi.exchanger.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.RateLimiter;
import com.huashi.constants.CommonContext.ProtocolType;
import com.huashi.exchanger.resolver.cmpp.v2.CmppManageProxy;
import com.huashi.exchanger.resolver.cmpp.v2.CmppProxySender;
import com.huashi.exchanger.resolver.sgip.SgipManageProxy;
import com.huashi.exchanger.resolver.sgip.SgipProxySender;
import com.huashi.exchanger.resolver.sgip.constant.SgipConstant;
import com.huashi.exchanger.resolver.smgp.SmgpManageProxy;
import com.huashi.exchanger.resolver.smgp.SmgpProxySender;
import com.huashi.exchanger.template.handler.RequestTemplateHandler;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huawei.insa2.util.Args;

@Service
public class SmsProxyManageService implements ISmsProxyManageService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private final Object lock = new Object();

	@Autowired
	private CmppProxySender cmppProxySender;
	@Autowired
	private SgipProxySender sgipProxySender;
	@Autowired
	private SmgpProxySender smgpProxySender;

	/**
	 * CMPP/SGIP/SMGP通道代理发送实例
	 */
	public volatile static Map<Integer, Object> GLOBAL_PROXIES = new HashMap<>();
	//

	/**
	 * 通道PROXY 发送错误次数计数器 add by 20170903
	 */
	private volatile static Map<Integer, Integer> GLOBAL_PROXIES_ERROR_COUNTER = new HashMap<>();
	
	public volatile static Map<Integer, RateLimiter> GLOBAL_RATE_LIMITERS = new HashMap<>();

	/**
	 * 默认限流速度
	 */
	public static final int DEFAULT_LIMIT_SPEED = 500;

	/**
	 * 联通重连超时时间
	 */
	private static final int SGIP_RECONNECT_TIMEOUT = 60 * 1000;


	@Override
	public boolean startProxy(SmsPassageParameter parameter) {
		if (parameter == null) {
			logger.error("加载通道代理失败，通道参数为空");
			return false;
		}

		TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());
		if (MapUtils.isEmpty(tparameter)) {
			logger.error("通道 参数信息为空");
			return false;
		}

		ProtocolType protocolType = getPassageProtocolType(parameter);
		if (protocolType == null) {
			return false;
		}

		try {

			initManageProxy(protocolType, parameter.getPassageId(), tparameter, parameter.getPacketsSize());
			return true;
		} catch (Exception e) {
			logger.error("通道代理初始化失败，通道信息：{}", JSON.toJSONString(parameter), e);
			return false;
		}
	}

	/**
	 * 
	 * TODO 获取通道协议类型
	 * 
	 * @param parameter
	 * @return
	 */
	private ProtocolType getPassageProtocolType(SmsPassageParameter parameter) {
		if (StringUtils.isEmpty(parameter.getProtocol())) {
			logger.error("加载通道代理失败，通道协议类型为空");
			return null;
		}

		return ProtocolType.parse(parameter.getProtocol());
	}
	
	/**
	 * 
	 * TODO 初始化CMPP通道
	 * 
	 * @param passageId
	 * @param tparameter
	 * @param speed
	 *            限速
	 * @return
	 */
	private void initManageProxy(ProtocolType protocolType, Integer passageId, TParameter tparameter, Integer speed) {
		if (passageId == null) {
			return;
		}

		synchronized (lock) {
			Object proxy = null;
			switch (protocolType) {
			case CMPP2:
			case CMPP3: {
				proxy = loadCmppManageProxy(passageId, tparameter.getCmppConnectAttrs());
				break;
			}
			case SGIP: {
				proxy = loadSgipManageProxy(passageId, tparameter.getSgipConnectAttrs());
				break;
			}
			case SMGP: {
				proxy = loadSmgpManageProxy(passageId, tparameter.getSmgpConnectAttrs());
				break;
			}
			default:
				logger.warn("通道协议类型为：{} 无需加载通道代理", protocolType.name());
				return;
			}

			if (proxy == null) {
				return;
			}

			// 加载通道代理类信息
			GLOBAL_PROXIES.put(passageId, proxy);

			// 加载限速控制器
			RateLimiter limiter = RateLimiter.create((speed == null || speed == 0) ? DEFAULT_LIMIT_SPEED : speed);
			GLOBAL_RATE_LIMITERS.put(passageId, limiter);

		}
	}

	/**
	 * 
	 * TODO 加载CMPP代理信息
	 * 
	 * @param passageId
	 * @param attrs
	 * @return
	 */
	private CmppManageProxy loadCmppManageProxy(Integer passageId, Map<String, Object> attrs) {
		Object object = getManageProxy(passageId);
		if (object != null) {
			try {
				((CmppManageProxy) object).close();
				GLOBAL_PROXIES.remove(passageId);
			} catch (Exception e) {
				logger.error("CMPP关闭失败", e);
			}
		}

		CmppManageProxy cmppManageProxy =  null;
		try {
			cmppManageProxy = new CmppManageProxy(cmppProxySender, passageId, new Args(attrs));

			logger.info("CMPP通道：{} 初始化连接成功", passageId);

			return cmppManageProxy;
		} catch (Exception e) {
			if(cmppManageProxy != null && cmppManageProxy.getConn() != null) {
				cmppManageProxy.getConn().close();
			}
			
			logger.error("获取CMPP代理失败", e);
			return null;
		}
		
	}

	/**
	 * 
	 * TODO 加载联通SGIP代理信息
	 * 
	 * @param passageId
	 * @param attrs
	 * @return
	 */
	private SgipManageProxy loadSgipManageProxy(Integer passageId, Map<String, Object> attrs) {
		SgipManageProxy sgipManageProxy = null;
		if (isProxyAvaiable(passageId)) {
			sgipManageProxy = (SgipManageProxy) getManageProxy(passageId); 
			
			// 重连无需关闭监听连接 edit by 20170803
//			sgipManageProxy.stopService();
			sgipManageProxy.close();
			GLOBAL_PROXIES.remove(passageId);
		}
		
		
		try {
			// 如果代理类为空则重新初始化
			sgipManageProxy = new SgipManageProxy(sgipProxySender, passageId, new Args(attrs));
			sgipManageProxy.connect(attrs.get("login-name") == null ? "" : attrs.get("login-name").toString(),
					attrs.get("login-pass") == null ? "" : attrs.get("login-pass").toString());
			
			if(!sgipManageProxy.getConn().available()) {
				if(sgipManageProxy != null && sgipManageProxy.getConn() != null) {
					sgipManageProxy.stopService();
					sgipManageProxy.close();
				}
				
				logger.error("获取SGIP代理失败");
				return null;
			}

			logger.info("SGIP通道：{} 初始化连接成功", passageId);

			return sgipManageProxy;
		} catch (Exception e) {
			if(sgipManageProxy != null && sgipManageProxy.getConn() != null) {
				sgipManageProxy.stopService();
				sgipManageProxy.close();
			}
			
			logger.error("获取SGIP代理失败", e);
			return null;
		}
	}

	/**
	 * 
	 * TODO 加载电信SMGP代理信息
	 * 
	 * @param passageId
	 * @param attrs
	 * @return
	 */
	private SmgpManageProxy loadSmgpManageProxy(Integer passageId, Map<String, Object> attrs) {
		Object object = getManageProxy(passageId);
		if (object != null) {
			try {
				((SmgpManageProxy) object).close();
				GLOBAL_PROXIES.remove(passageId);
			} catch (Exception e) {
				logger.error("SMGP关闭失败", e);
			}
		}
		
		SmgpManageProxy smgpManageProxy =  null;
		try {
			smgpManageProxy = new SmgpManageProxy(smgpProxySender, passageId, new Args(attrs));

			logger.info("SMGP通道：{} 初始化连接成功", passageId);

			return smgpManageProxy;
		} catch (Exception e) {
			if(smgpManageProxy != null && smgpManageProxy.getConn() != null) {
				smgpManageProxy.getConn().close();
			}
			
			logger.error("获取SMGP代理失败", e);
			return null;
		}
	}
	
	@Override
	public boolean isProxyAvaiable(Integer passageId) {
		if (MapUtils.isEmpty(GLOBAL_PROXIES) || GLOBAL_PROXIES.get(passageId) == null) {
			return false;
		}

		Object passage = GLOBAL_PROXIES.get(passageId);
		if(passage == null) {
			return false;
		}
		
		if (passage instanceof CmppManageProxy) {
			CmppManageProxy prxoy = (CmppManageProxy) passage;
			if (prxoy.getConn() == null) {
				return false;
			}
			
		} else if (passage instanceof SgipManageProxy) {
			SgipManageProxy prxoy = (SgipManageProxy) passage;
			if (prxoy.getConn() == null) {
				return false;
			}
			
			// 上次发送时间，如果上次发送时间和当前时间差值在55秒内，则认为连接有效
			Long lastSendTime = SgipConstant.SGIP_RECONNECT_TIMEMILLS.get(passageId);
			
			logger.info("SGIP 状态：{}, 上次时间：{}， 时间差：{} ms", prxoy.getConnState(), lastSendTime,
					lastSendTime == null ? null : System.currentTimeMillis() - lastSendTime);
			if(lastSendTime != null && (System.currentTimeMillis() - lastSendTime > SGIP_RECONNECT_TIMEOUT)) {
				return false;
			}
			
			if (StringUtils.isNotEmpty(prxoy.getConnState())) {
				logger.error("SGIP连接错误，错误信息：{} ", prxoy.getConnState());
				return false;
			}
			
		} else if (passage instanceof SmgpManageProxy) {
			SmgpManageProxy prxoy = (SmgpManageProxy) passage;
			if (prxoy.getConn() == null) {
				return false;
			}
		}
		
		logger.info("当前通道ID： {} 发送错误次数：{}", passageId, GLOBAL_PROXIES_ERROR_COUNTER.get(passageId));
		// 判断该通道发送错误是否累计3次，如果累计三次，返回FALSE，需要重连 add by 20170903
		if(GLOBAL_PROXIES_ERROR_COUNTER.get(passageId) != null && GLOBAL_PROXIES_ERROR_COUNTER.get(passageId) >= 3) {
            return false;
        }

		return true;
	}
	
	/**
	 * 
	 * TODO 获取CMPP代理
	 * 
	 * @param passageId
	 * @return
	 */
	public static Object getManageProxy(Integer passageId) {
		return GLOBAL_PROXIES.get(passageId);
	}

	@Override
	public boolean stopProxy(Integer passageId) {
		if(!isProxyAvaiable(passageId)) {
			return true;
		}
		
		try {
			Object passage = GLOBAL_PROXIES.get(passageId);
			if (passage instanceof CmppManageProxy) {
				CmppManageProxy prxoy = (CmppManageProxy) passage;
				prxoy.close();
				
			} else if (passage instanceof SgipManageProxy) {
				SgipManageProxy prxoy = (SgipManageProxy) passage;
				prxoy.close();
				
			} else if (passage instanceof SmgpManageProxy) {
				SmgpManageProxy prxoy = (SmgpManageProxy) passage;
				prxoy.close();
			}
			
			GLOBAL_PROXIES.remove(passageId);
			return true;
		} catch (Exception e) {
			logger.error("停止代理异常", e);
			return false;
		}
	}
	
	@Override
	public void plusSendErrorTimes(Integer passageId) {
		Integer counter = GLOBAL_PROXIES_ERROR_COUNTER.get(passageId);
		if(counter == null) {
			counter = 0;
		}
		
		logger.info("当前通道 passageId : {} 代理失败次数 {}", passageId, counter+1);
		GLOBAL_PROXIES_ERROR_COUNTER.put(passageId, counter+1);
	}
	
	@Override
	public void clearSendErrorTimes(Integer passageId) {
		Integer counter = GLOBAL_PROXIES_ERROR_COUNTER.get(passageId);
		if(counter == null) {
			return;
		}
		
		GLOBAL_PROXIES_ERROR_COUNTER.remove(passageId);
	}

}
