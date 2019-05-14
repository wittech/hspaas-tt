package com.huashi.sms.record.vo;

import java.io.Serializable;

public class SmsPushReport implements Serializable {

    private static final long serialVersionUID = 2663247719726229823L;

    /**
     * 提交总数
     */
    private int               submitCount;

    /**
     * 上家回执总数
     */
    private int               deliverCount;

    /**
     * 上家未回执总数
     */
    private int               undeliverCount;

    /**
     * 不需要推送的数量（推送设置）
     */
    private int               unecessaryPushCount;

    /**
     * 准备推送数量
     */
    private int               readyPushCount;

    /**
     * 推送成功数
     */
    private int               pushedSuccessCount;

    /**
     * 推送失败数
     */
    private int               pushedFailedCount;

    /**
     * 回执当前批次
     */
    private Long              serialNo         = 0L;

    public int getSubmitCount() {
        return submitCount;
    }

    public void setSubmitCount(int submitCount) {
        this.submitCount = submitCount;
    }

    public int getDeliverCount() {
        return deliverCount;
    }

    public void setDeliverCount(int deliverCount) {
        this.deliverCount = deliverCount;
    }

    public int getPushedSuccessCount() {
        return pushedSuccessCount;
    }

    public void setPushedSuccessCount(int pushedSuccessCount) {
        this.pushedSuccessCount = pushedSuccessCount;
    }

    public int getPushedFailedCount() {
        return pushedFailedCount;
    }

    public void setPushedFailedCount(int pushedFailedCount) {
        this.pushedFailedCount = pushedFailedCount;
    }

    public int getUndeliverCount() {
        return undeliverCount;
    }

    public void setUndeliverCount(int undeliverCount) {
        this.undeliverCount = undeliverCount;
    }

    public int getUnecessaryPushCount() {
        return unecessaryPushCount;
    }

    public void setUnecessaryPushCount(int unecessaryPushCount) {
        this.unecessaryPushCount = unecessaryPushCount;
    }

    public int getReadyPushCount() {
        return readyPushCount;
    }

    public void setReadyPushCount(int readyPushCount) {
        this.readyPushCount = readyPushCount;
    }

    public Long getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(Long serialNo) {
        this.serialNo = serialNo;
    }

}
