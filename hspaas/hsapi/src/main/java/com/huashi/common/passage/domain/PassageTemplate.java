package com.huashi.common.passage.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PassageTemplate implements Serializable{
	private static final long serialVersionUID = -3775126767851307177L;

	private Integer id;

    private String name;

    private String protocol;

    private Integer passageType;

    private Date createTime;
    
    private List<PassageTemplateDetail> detailList = new ArrayList<PassageTemplateDetail>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol == null ? null : protocol.trim();
    }

    public Integer getPassageType() {
        return passageType;
    }

    public void setPassageType(Integer passageType) {
        this.passageType = passageType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public List<PassageTemplateDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<PassageTemplateDetail> detailList) {
		this.detailList = detailList;
	}
    
    
}