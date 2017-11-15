package com.huashi.bill.order.model;

import java.io.Serializable;

import com.huashi.bill.order.constant.ExchangeOrderContext.ExchangeType;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.constants.CommonContext.PlatformType;

/**
 * 
 * TODO 转存订单参数
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年9月18日 下午3:23:42
 */
public class ExchangeOrderModel implements Serializable {

	private static final long serialVersionUID = 4517369645804908464L;
	private int userId;
	// 操作人（针对BOSS系统操作，操作工号）
	private String operator;
	private ExchangeType exchangeType;
	private PlatformType platformType = PlatformType.UNDEFINED;

	// 调用类型
	private AppType appType;

	private Double productFee;
	// 转赠人
	private Integer fromUserId;
	// 客户端调用IP
	private String ip;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Double getProductFee() {
		return productFee;
	}

	public void setProductFee(Double productFee) {
		this.productFee = productFee;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(Integer fromUserId) {
		this.fromUserId = fromUserId;
	}

	public ExchangeType getExchangeType() {
		return exchangeType;
	}

	public void setExchangeType(ExchangeType exchangeType) {
		this.exchangeType = exchangeType;
	}

	public PlatformType getPlatformType() {
		return platformType;
	}

	public void setPlatformType(PlatformType platformType) {
		this.platformType = platformType;
	}

	public AppType getAppType() {
		return appType;
	}

	public void setAppType(AppType appType) {
		this.appType = appType;
	}

}
