package com.huashi.mms.passage.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.passage.dto.ParseParamDto;
import com.huashi.common.passage.dto.RequestParamDto;

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

    /**
     * 最大连接数
     */
    private Integer connectionSize;

    /**
     * 读取数据流超时时间（针对已经和目标服务器建立连接，对方处理时间过慢，相应超时时间）
     */
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

    public Integer getPacketsSize() {
        return packetsSize;
    }

    public void setPacketsSize(Integer packetsSize) {
        this.packetsSize = packetsSize;
    }

    public Integer getConnectionSize() {
        return connectionSize;
    }

    public void setConnectionSize(Integer connectionSize) {
        this.connectionSize = connectionSize;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public List<RequestParamDto> getRequestParams() {
        List<RequestParamDto> list = new ArrayList<RequestParamDto>();
        if (StringUtils.isNotBlank(paramsDefinition)) {
            list = JSONArray.parseArray(paramsDefinition, RequestParamDto.class);
        }
        return list;
    }

    public List<ParseParamDto> getParseParams() {
        List<ParseParamDto> list = new ArrayList<ParseParamDto>();
        if (StringUtils.isNotBlank(position)) {
            Map<String, String> map = JSONObject.parseObject(position, new TypeReference<Map<String, String>>() {
            });
            for (Map.Entry<String, String> m : map.entrySet()) {
                ParseParamDto dto = new ParseParamDto();
                dto.setPosition(m.getValue());
                dto.setParseName(m.getKey());
                dto.setShowName("");
            }
        }
        return list;
    }

    public String getShowResultFormat() {
        if (StringUtils.isNotBlank(resultFormat)) {
            resultFormat = resultFormat.replaceAll("\"", "&quot;");
            resultFormat = resultFormat.replaceAll("'", "&#39;");
            resultFormat = resultFormat.replaceAll("<", "&lt;");
            resultFormat = resultFormat.replaceAll(">", "&gt;");
            return resultFormat;
        }
        return null;
    }

}
