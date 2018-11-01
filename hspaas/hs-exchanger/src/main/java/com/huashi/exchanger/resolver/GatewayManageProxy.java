package com.huashi.exchanger.resolver;


public interface GatewayManageProxy {

    /**
     * TODO 获取连接
     * 
     * @return
     */
//    <T extends PSocketConnection> T getConn();

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
