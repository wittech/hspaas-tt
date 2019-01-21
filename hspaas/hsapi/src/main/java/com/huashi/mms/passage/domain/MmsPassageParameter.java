package com.huashi.mms.passage.domain;

import java.util.Date;

public class MmsPassageParameter {

    private Integer id;

    private Integer passageId;

    /**
     * 通道代码（伪列）
     */
    private String  passageCode;

    private String  protocol;

    private Integer callType;

    private String  url;

    private String  paramsDefinition;

    private String  params;

    private String  resultFormat;

    private String  successCode;

    private String  position;

    private Date    createTime;
    
    /**
     * 限流速度
     */
    private Integer packetsSize;
    
    // 通道方短信模板ID（提前报备）
    private String smsTemplateId;
    // 变量参数，专指用于类似点对点短信数组/或者JSON变量传递 add by zhengying 20170825
    private String[] variableParamNames;
    
    private String[] variableParamValues;
    
    
    // 最大连接数
    private Integer connectionSize;
    // 读取数据流超时时间（针对已经和目标服务器建立连接，对方处理时间过慢，相应超时时间）
    private Integer readTimeout;

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position == null ? null : position.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getPassageCode() {
        return passageCode;
    }

    public void setPassageCode(String passageCode) {
        this.passageCode = passageCode;
    }

}
