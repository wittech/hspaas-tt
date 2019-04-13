package com.huashi.exchanger.model;

import java.io.Serializable;

public class SmsProviderModel implements Serializable {

    private static final long serialVersionUID = 949793666271089571L;
    private Integer           passageId;

    /**
     * 协议类型
     */
    private String            protocol;

    /**
     * 调用类型
     */
    private Integer           callType;

    /**
     * 调用的URL
     */
    private String            url;

    private String            paramsDefinition;

    /**
     * 调用的参数值
     */
    private String            params;

    /**
     * 结果模板
     */
    private String            resultFormat;

    /**
     * 成功状态码
     */
    private String            successCode;

    /**
     * 结果模板定位
     */
    private String            position;

    public Integer getPassageId() {
        return passageId;
    }

    public void setPassageId(Integer passageId) {
        this.passageId = passageId;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getCallType() {
        return callType;
    }

    public void setCallType(Integer callType) {
        this.callType = callType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParamsDefinition() {
        return paramsDefinition;
    }

    public void setParamsDefinition(String paramsDefinition) {
        this.paramsDefinition = paramsDefinition;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getResultFormat() {
        return resultFormat;
    }

    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat;
    }

    public String getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(String successCode) {
        this.successCode = successCode;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

}
