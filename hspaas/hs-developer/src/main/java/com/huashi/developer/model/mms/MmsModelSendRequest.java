package com.huashi.developer.model.mms;

import com.huashi.developer.model.PassportModel;
import com.huashi.developer.validator.annotation.ValidateField;

/**
 * TODO 彩信模板发送请求
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年3月11日 下午11:03:24
 */
public class MmsModelSendRequest extends PassportModel {

    private static final long serialVersionUID = -7341804898737200123L;

    /**
     * 手机号码
     */
    @ValidateField(value = "mobile", required = true)
    private String            mobile;

    /**
     * 模板ID
     */
    @ValidateField(value = "modelId", required = true)
    private String            modelId;

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
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

}
