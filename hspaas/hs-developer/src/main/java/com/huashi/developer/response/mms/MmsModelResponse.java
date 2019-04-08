package com.huashi.developer.response.mms;

import com.alibaba.fastjson.JSONObject;
import com.huashi.constants.OpenApiCode.CommonApiCode;

/**
 * TODO 彩信模版报备回执
 *
 * @author zhengying
 * @version V1.0
 * @date 2019年3月28日 下午5:20:45
 */
public class MmsModelResponse {

    /**
     * 状态码 #com.huashi.constants.OpenApiCode
     */
    private String code;

    /**
     * 回执信息描述（中文）
     */
    private String message;

    /**
     * 模版ID
     */
    private String modelId = "";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public MmsModelResponse(String code, String message, String modelId) {
        super();
        this.code = code;
        this.message = message;
        this.modelId = modelId;
    }

    public MmsModelResponse() {
        super();
    }

    public MmsModelResponse(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }
    
    public MmsModelResponse(JSONObject jsonObject) {
        super();
        try {
            this.code = jsonObject.getString("code");
            this.message = jsonObject.getString("message");
        } catch (Exception e) {
            this.code = CommonApiCode.COMMON_SERVER_EXCEPTION.getCode();
            this.message = CommonApiCode.COMMON_SERVER_EXCEPTION.getMessage();
        }
    }

}
