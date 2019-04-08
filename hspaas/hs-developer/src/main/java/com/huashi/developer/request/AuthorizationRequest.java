package com.huashi.developer.request;

import java.io.Serializable;

import com.huashi.developer.annotation.ValidateField;

/**
 * TODO 鉴权通行证
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年3月21日 上午10:22:43
 */
public class AuthorizationRequest implements Serializable {

    private static final long serialVersionUID = -7218382770115176499L;

    /**
     * 开发者接口唯一标识，通行证
     */
    @ValidateField(value = "appkey", required = true)
    private String            appkey;

    /**
     * 开发者接口签名(经过签名算法的摘要信息)
     */
    @ValidateField(value = "appsecret", required = true)
    private String            appsecret;

    /**
     * 签名时间戳，用于签名时间有效性校验，防止同一消息暴力调用接口
     */
    @ValidateField(value = "timestamp", required = true)
    private String            timestamp;

    private transient Integer userId;
    private transient Integer fee;
    private transient Integer totalFee;
    private transient String  ip;
    private transient Integer appType;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
