package com.huashi.developer.response.mms;

/**
 * TODO 彩信余额回执
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年3月3日 下午5:21:06
 */
public class MmsBalanceResponse {

    /**
     * 状态码
     */
    private String code;

    /**
     * 余额
     */
    private String balance;

    /**
     * 付费方式: @Enum BalancePayType
     */
    private String type;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MmsBalanceResponse() {
        super();
    }

    public MmsBalanceResponse(String code) {
        this.type = "0";
        this.balance = "0";
        this.code = code;
    }

    public MmsBalanceResponse(String code, int balance, int type) {
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
