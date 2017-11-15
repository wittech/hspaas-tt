package com.huashi.developer.response.sms;

/**
 * 
 * TODO 请在此处添加注释
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2017年2月21日 下午9:50:00
 */
public class SmsBalanceResponse {

	private String code; // 状态码
	private String balance; // 余额
	private String type; // 付费方式: @Enum BalancePayType

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public SmsBalanceResponse() {
		super();
	}

	public SmsBalanceResponse(String code) {
		this.type = "0";
		this.balance = "0";
		this.code = code;
	}

	public SmsBalanceResponse(String code, int balance, int type) {
		super();
		this.code = code;
		this.balance = balance + "";
		this.type = type + "";
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
