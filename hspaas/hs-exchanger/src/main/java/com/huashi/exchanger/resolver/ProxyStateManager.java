package com.huashi.exchanger.resolver;

/**
 * TODO 代理状态声明周期管理
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年11月6日 下午2:59:36
 */
public interface ProxyStateManager {

    /**
     * TODO 获取连接状态
     * 
     * @return
     */
    String getConnState();

    /**
     * TODO 关闭连接
     */
    void close();
}
