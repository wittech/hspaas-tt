package com.huashi.common.passage.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.huashi.common.passage.dto.ParseParamDto;
import com.huashi.common.passage.dto.RequestParamDto;

public class PassageTemplateDetail implements Serializable{
	
	private static final long serialVersionUID = -7254241584406107515L;

	private Integer id;

    private Integer templateId;

    private Integer callType;

    private String url;

    private String params;

    private String position;
    
    private String resultFormat;

    private String successCode;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
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

    public String getShowResultFormat(){
        if(StringUtils.isNotBlank(resultFormat)){
            resultFormat = resultFormat.replaceAll("\"","&quot;");
            resultFormat = resultFormat.replaceAll("'","&#39;");
            resultFormat = resultFormat.replaceAll("<","&lt;");
            resultFormat = resultFormat.replaceAll(">","&gt;");
            return resultFormat;
        }
        return null;
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
		this.position = position;
	}
	
	public List<RequestParamDto> getRequestParams(){
		List<RequestParamDto> list = new ArrayList<RequestParamDto>();
		if(StringUtils.isNotBlank(params)){
			list = JSONArray.parseArray(params, RequestParamDto.class);
		}
		return list;
	}
	
	public List<ParseParamDto> getParseParams(){
		List<ParseParamDto> list = new ArrayList<ParseParamDto>();
		if(StringUtils.isNotBlank(position)){
			list = JSONArray.parseArray(position, ParseParamDto.class);
		}
		return list;
	}
	
}