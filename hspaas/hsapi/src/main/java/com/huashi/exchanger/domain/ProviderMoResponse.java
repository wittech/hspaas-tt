package com.huashi.exchanger.domain;

import java.io.Serializable;

/**
 * TODO 厂商上行回执信息
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年9月28日 下午6:10:26
 */
public class ProviderMoResponse implements Serializable {

    private static final long serialVersionUID = -8576510617914881558L;

    /**
     * 消息ID
     */
    private String            msgId;

    /**
     * 接收手机号码
     */
    private String            mobile;

    /**
     * 回复短信内容
     */
    private String            content;

    /**
     * SP码号
     */
    private String            spcode;

    /**
     * 发送时间
     */
    private String            sendTime;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSpcode() {
        return spcode;
    }

    public void setSpcode(String spcode) {
        this.spcode = spcode;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "ProviderMoResponse [msgId=" + msgId + ", mobile=" + mobile + ", content=" + content + ", spcode="
               + spcode + ", sendTime=" + sendTime + "]";
    }

}
