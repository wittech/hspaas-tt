package com.huashi.exchanger.service;

import java.net.BindException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.RateLimiter;
import com.huashi.constants.CommonContext.ProtocolType;
import com.huashi.exchanger.exception.DataEmptyException;
import com.huashi.exchanger.resolver.ProxyStateManager;
import com.huashi.exchanger.resolver.cmpp.v2.CmppManageProxy;
import com.huashi.exchanger.resolver.cmpp.v2.CmppProxySender;
import com.huashi.exchanger.resolver.cmpp.v3.Cmpp3ManageProxy;
import com.huashi.exchanger.resolver.cmpp.v3.Cmpp3ProxySender;
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

    private final Logger                                    logger                       = LoggerFactory.getLogger(getClass());
    // private final Object lock = new Object();

    @Autowired
    private CmppProxySender                                 cmppProxySender;
    @Autowired
    private Cmpp3ProxySender                                cmpp3ProxySender;

    @Autowired
    private SgipProxySender                                 sgipProxySender;
    @Autowired
    private SmgpProxySender                                 smgpProxySender;

    /**
     * CMPP/SGIP/SMGP通道代理发送实例
     */
    public static volatile Map<Integer, ProxyStateManager> GLOBAL_PROXIES               = new ConcurrentHashMap<>();

    /**
     * 通道PROXY 发送错误次数计数器 add by 20170903
     */
    private static final Map<Integer, Integer>              GLOBAL_PROXIES_ERROR_COUNTER = new HashMap<>();

    /**
     * 通道对应的限流器容器
     */
    public static final Map<Integer, RateLimiter>           GLOBAL_GATEWAY_RATE_LIMITERS = new HashMap<>();

    /**
     * 默认限流速度
     */
    public static final int                                 DEFAULT_LIMIT_SPEED          = 500;

    /**
     * 联通重连超时时间
     */
    private static final int                                SGIP_RECONNECT_TIMEOUT       = 60 * 1000;

    @Override
    public boolean startProxy(SmsPassageParameter parameter) {
        try {
            if (parameter == null) {
                throw new IllegalArgumentException("SmsPassageParameter is empty");
            }

            TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());
            if (MapUtils.isEmpty(tparameter)) {
                throw new IllegalArgumentException("SmsPassageParameter's params are empty");
            }

            return setupPassageProxyIfNecessary(getPassageProtocolType(parameter), parameter.getPassageId(),
                                                tparameter, parameter.getPacketsSize());
        } catch (Exception e) {
            logger.error("Starting passage's proxy failed, args[" + JSON.toJSONString(parameter) + "]", e);
            return false;
        }
    }

    /**
     * TODO 获取通道协议类型
     * 
     * @param parameter
     * @return
     */
    private ProtocolType getPassageProtocolType(SmsPassageParameter parameter) {
        if (StringUtils.isEmpty(parameter.getProtocol())) {
            throw new IllegalArgumentException("SmsPassageParameter's protocolType is empty");
        }

        ProtocolType protocolType = ProtocolType.parse(parameter.getProtocol());
        if (protocolType == null) {
            throw new IllegalArgumentException("SmsPassageParameter's protocolType[" + parameter.getProtocol()
                                               + "] is not matched");
        }

        return protocolType;
    }

    /**
     * TODO 初始化CMPP通道
     * 
     * @param passageId
     * @param tparameter
     * @param speed 限速
     * @return
     */
    private boolean setupPassageProxyIfNecessary(ProtocolType protocolType, Integer passageId, TParameter tparameter,
                                                 Integer speed) {
        // 如果协议非直连协议（如HTTP，WebService等）
        if (!ProtocolType.isBelongtoDirect(protocolType.name())) {
            return true;
        }

        boolean isLoadSucceed = loadManageProxy(passageId, tparameter, protocolType);
        if (!isLoadSucceed) {
            return false;
        }

        bindPassageRateLimiter(passageId, speed);

        return true;
    }

    /**
     * TODO 加载限速控制器
     * 
     * @param passageId
     * @param speed
     */
    private void bindPassageRateLimiter(Integer passageId, Integer speed) {
        RateLimiter limiter = RateLimiter.create((speed == null || speed == 0) ? DEFAULT_LIMIT_SPEED : speed);
        GLOBAL_GATEWAY_RATE_LIMITERS.put(passageId, limiter);
    }

    /**
     * TODO 关闭原有的代理连接(错误或无效连接，需要重新开启新连接)
     * 
     * @param passageId
     * @return
     */
    private void closeInvalidProxyIfAlive(Integer passageId) {
        ProxyStateManager manageProxy = getProxyManager(passageId);
        if (manageProxy != null) {
            try {
                manageProxy.close();
                GLOBAL_PROXIES.remove(passageId);
            } catch (Exception e) {
                logger.error("Closing manageProxy[" + manageProxy + "] failed", e);
            }
        }
    }

    /**
     * TODO 加载代理信息
     * 
     * @param passageId
     * @param tparameter
     * @param protocolType
     * @return
     */
    private boolean loadManageProxy(Integer passageId, TParameter tparameter, ProtocolType protocolType) {

        closeInvalidProxyIfAlive(passageId);

        ProxyStateManager gatewayManageProxy = null;

        try {
            switch (protocolType) {
                case CMPP2: {
                    gatewayManageProxy = new CmppManageProxy(cmppProxySender, passageId,
                                                             new Args(tparameter.getCmppConnectAttrs()));
                    break;
                }
                case CMPP3: {
                    gatewayManageProxy = new Cmpp3ManageProxy(cmpp3ProxySender, passageId,
                                                              new Args(tparameter.getCmppConnectAttrs()));
                    break;
                }
                case SMGP: {
                    gatewayManageProxy = new SmgpManageProxy(smgpProxySender, passageId,
                                                             new Args(tparameter.getSmgpConnectAttrs()));
                    break;
                }
                case SGIP: {
                    gatewayManageProxy = loadSgipManageProxy(passageId, tparameter.getSgipConnectAttrs());
                    break;
                }
                default: {
                    break;
                }
            }

            if (gatewayManageProxy == null) {
                return false;
            }

            // 加载通道代理类信息
            GLOBAL_PROXIES.put(passageId, gatewayManageProxy);

            logger.info("Cmpp passage[" + passageId + "] protocol[" + protocolType.name()
                        + "]  setup succeed with args [" + JSON.toJSONString(tparameter) + "]");

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO 加载联通SGIP代理信息
     * 
     * @param passageId
     * @param attrs
     * @return
     */
    private SgipManageProxy loadSgipManageProxy(Integer passageId, Map<String, Object> attrs) {
        SgipManageProxy sgipManageProxy = null;
        try {
            // 如果代理类为空则重新初始化
            sgipManageProxy = new SgipManageProxy(sgipProxySender, passageId, new Args(attrs));
            sgipManageProxy.connect(attrs.get("login-name") == null ? "" : attrs.get("login-name").toString(),
                                    attrs.get("login-pass") == null ? "" : attrs.get("login-pass").toString());

            if (!sgipManageProxy.getConn().available()) {
                if (sgipManageProxy != null && sgipManageProxy.getConn() != null) {
                    sgipManageProxy.stopService();
                    sgipManageProxy.close();
                }

                throw new DataEmptyException("Sgip proxy load failed");
            }

            return sgipManageProxy;
        } catch (Exception e) {
            if (e instanceof BindException) {
                // 如果错误信息为绑定异常，则睡眠1秒后，递归重试方法
                logger.error("Sgip bind failed, msg[" + e.getMessage() + "] ,it will sleep 1000 ms, retry..");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e1) {
                }
                return loadSgipManageProxy(passageId, attrs);
            }

            if (sgipManageProxy != null && sgipManageProxy.getConn() != null) {
                sgipManageProxy.stopService();
                sgipManageProxy.close();
            }

            throw new DataEmptyException("Sgip proxy connection failed", e);
        }
    }

    @Override
    public boolean isProxyAvaiable(Integer passageId) {
        ProxyStateManager passage = getProxyManager(passageId);
        if (passage == null) {
            return false;
        }

//        if (passage.getConn() == null) {
//            return false;
//        }

        if (passage instanceof SgipManageProxy) {
            // 上次发送时间，如果上次发送时间和当前时间差值在55秒内，则认为连接有效
            Long lastSendTime = SgipConstant.SGIP_RECONNECT_TIMEMILLS.get(passageId);

            logger.info("SGIP 状态：{}, 上次时间：{}， 时间差：{} ms", passage.getConnState(), lastSendTime,
                        lastSendTime == null ? null : System.currentTimeMillis() - lastSendTime);
            if (lastSendTime != null && (System.currentTimeMillis() - lastSendTime > SGIP_RECONNECT_TIMEOUT)) {
                return false;
            }

            if (StringUtils.isNotEmpty(passage.getConnState())) {
                logger.error("SGIP连接错误，错误信息：{} ", passage.getConnState());
                return false;
            }

        }

        // 判断该通道发送错误是否累计3次，如果累计三次，返回FALSE，需要重连 add by 20170903
        if (GLOBAL_PROXIES_ERROR_COUNTER.get(passageId) != null && GLOBAL_PROXIES_ERROR_COUNTER.get(passageId) >= 3) {
            logger.info("当前通道ID： {} 发送错误次数：{}", passageId, GLOBAL_PROXIES_ERROR_COUNTER.get(passageId));
            return false;
        }

        return true;
    }

    /**
     * TODO 获取CMPP代理
     * 
     * @param passageId
     * @return
     */
    public static ProxyStateManager getProxyManager(Integer passageId) {
        return GLOBAL_PROXIES.get(passageId);
    }

    @Override
    public boolean stopProxy(Integer passageId) {
        logger.info("stopProxy, passageId : {} ", passageId);
        if (!isProxyAvaiable(passageId)) {
            logger.warn("PassageId [" + passageId + "] has shutdown ");
            return true;
        }

        try {
            ProxyStateManager prxoy = GLOBAL_PROXIES.get(passageId);
            if (prxoy instanceof SgipManageProxy) {
                SgipManageProxy sgipManageProxy = (SgipManageProxy) prxoy;
                sgipManageProxy.stopService();
            }

            prxoy.close();
            GLOBAL_PROXIES.remove(passageId);

            logger.info("Passage[" + passageId + "] shutdown finished");
            return true;
        } catch (Exception e) {
            logger.error("停止代理异常", e);
            return false;
        }
    }

    @Override
    public void plusSendErrorTimes(Integer passageId) {
        synchronized (GLOBAL_PROXIES_ERROR_COUNTER) {
            Integer counter = GLOBAL_PROXIES_ERROR_COUNTER.get(passageId);
            if (counter == null) {
                counter = 0;
            }

            logger.info("当前通道 passageId : {} 代理失败次数 {}", passageId, counter + 1);
            GLOBAL_PROXIES_ERROR_COUNTER.put(passageId, counter + 1);
        }
    }

    @Override
    public void clearSendErrorTimes(Integer passageId) {
        synchronized (GLOBAL_PROXIES_ERROR_COUNTER) {
            Integer counter = GLOBAL_PROXIES_ERROR_COUNTER.get(passageId);
            if (counter == null) {
                return;
            }

            GLOBAL_PROXIES_ERROR_COUNTER.put(passageId, 0);
        }
    }

}
