package com.huashi.developer.response.sms;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.huashi.constants.OpenApiCode;
import com.huashi.constants.OpenApiCode.ApiReponseCode;

public class SmsApiResponse implements Serializable {

    private static final long serialVersionUID = 746495195378955865L;
    private String            code             = ApiReponseCode.SUCCESS.getCode(); // 状态码
    private String            msg;                                                // 成功发送的短信计费条数
    private List<JSONObject>  rets             = null;                            // 处理结果

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<JSONObject> getRets() {
        return rets;
    }

    public void setRets(List<JSONObject> rets) {
        this.rets = rets;
    }

    public void setResponse(ApiReponseCode code) {
        this.code = code.getCode();
        this.msg = code.getMessage();
    }

    public SmsApiResponse() {
        super();
    }

    public SmsApiResponse(String code, String msg, List<JSONObject> rets) {
        super();
        this.code = code;
        this.msg = msg;
        this.rets = rets;
    }

    public SmsApiResponse(String code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }
    
    public SmsApiResponse(OpenApiCode.ApiReponseCode apiCode) {
        super();
        this.code = apiCode.getCode();
        this.msg = apiCode.getMessage();
    }

}
