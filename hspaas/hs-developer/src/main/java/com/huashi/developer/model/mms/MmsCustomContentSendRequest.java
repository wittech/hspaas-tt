package com.huashi.developer.model.mms;

import com.huashi.developer.model.PassportModel;
import com.huashi.developer.validator.annotation.ValidateField;

/**
 * TODO 彩信数据模型
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年3月11日 下午11:03:24
 */
public class MmsCustomContentSendRequest extends PassportModel {

    private static final long serialVersionUID = 443695756826614380L;

    /**
     * 手机号码
     */
    @ValidateField(value = "mobile", required = true)
    private String            mobile;

    /**
     * 彩信标题
     */
    @ValidateField(value = "title", required = true)
    private String            title;

    /**
     * 彩信报文（数组模式 如：[{mediaName:”test.jpg”, mediaType:”image”,content:”=bS39888993#jajierj*...”}]）
     */
    @ValidateField(value = "body", required = true)
    private String            body;

    /**
     * 扩展码号
     */
    @ValidateField(value = "extNumber", number = true)
    private String            extNumber;

    /**
     * 用于调用者自定义内容，主要为了实现调用侧的自行业务逻辑标识，如调用方自己的 会员标识，最终我方会原样返回
     */
    @ValidateField(value = "attach")
    private String            attach;

    /**
     * 回调URL
     */
    @ValidateField(value = "callback")
    private String            callback;

    /**
     * 转义BODY生成的报文数据
     */
    private transient String  context;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getExtNumber() {
        return extNumber;
    }

    public void setExtNumber(String extNumber) {
        this.extNumber = extNumber;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

}
