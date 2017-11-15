package com.huashi.fs.passage.domain;

import java.io.Serializable;
import java.util.Date;

public class FluxPassageParameter implements Serializable{
	private static final long serialVersionUID = 5333612665849634309L;

	private Integer id;

    private Integer passageId;

    private String protocol;

    private Integer callType;

    private String url;

    private String paramsDefinition;

    private String params;

    private String resultFormat;

    private String successCode;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
        this.protocol = protocol == null ? null : protocol.trim();
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
        this.url = url == null ? null : url.trim();
    }

    public String getParamsDefinition() {
        return paramsDefinition;
    }

    public void setParamsDefinition(String paramsDefinition) {
        this.paramsDefinition = paramsDefinition == null ? null : paramsDefinition.trim();
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    public String getResultFormat() {
        return resultFormat;
    }

    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat == null ? null : resultFormat.trim();
    }

    public String getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(String successCode) {
        this.successCode = successCode == null ? null : successCode.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}