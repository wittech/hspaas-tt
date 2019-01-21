package com.huashi.exchanger.resolver.sms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.huashi.exchanger.service.ISmsProxyManager;
import com.huashi.exchanger.service.template.SmsProxyManagerTemplate;
import com.huashi.sms.passage.domain.SmsPassageParameter;

/**
 * TODO 直连抽象类
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年8月5日 上午12:21:38
 */
public abstract class AbstractSmProxySender {

    @Autowired
    protected ISmsProxyManager         smsProxyManageService;

    @Resource
    protected RabbitTemplate           rabbitTemplate;

    protected final Logger             logger             = LoggerFactory.getLogger(getClass());

    /**
     * 通道ID锁
     */
    private final Map<Integer, Object> passageLockMonitor = new ConcurrentHashMap<>();

    private void addPassageLockMonitor(Integer passageId) {
        passageLockMonitor.putIfAbsent(passageId, new Object());
    }

    /**
     * TODO 获取直连对象的代理信息（不同通道间初始化互不影响）
     * 
     * @param parameter
     * @return
     */
    protected Object getSmManageProxy(SmsPassageParameter parameter) {
        addPassageLockMonitor(parameter.getPassageId());

        synchronized (passageLockMonitor.get(parameter.getPassageId())) {
            if (smsProxyManageService.isProxyAvaiable(parameter.getPassageId())) {
                return SmsProxyManagerTemplate.getManageProxy(parameter.getPassageId());
            }

            boolean isOk = smsProxyManageService.startProxy(parameter);
            if (!isOk) {
                return null;
            }

            // 重新初始化后将错误计数器归零
            smsProxyManageService.clearSendErrorTimes(parameter.getPassageId());

            return SmsProxyManagerTemplate.GLOBAL_PROXIES.get(parameter.getPassageId());
        }
    }

    /**
     * TODO 断开直连协议连接
     * 
     * @param passageId
     */
    public void onTerminate(Integer passageId) {
        smsProxyManageService.stopProxy(passageId);
    }
}
