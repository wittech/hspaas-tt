package com.huashi.mms.template.domain;

import java.util.Date;

public class MmsPassageMessageTemplate {

    private Long    id;

    private Long    templateId;

    private Integer passageId;

    private String  passageModelId;

    private Integer status;

    private Date    createTime;

    private Date    updateTime;

    private String  remark;

    private String  passageName;
    private String  statusText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPassageId() {
        return passageId;
    }

    public void setPassageId(Integer passageId) {
        this.passageId = passageId;
    }

    public String getPassageModelId() {
        return passageModelId;
    }

    public void setPassageModelId(String passageModelId) {
        this.passageModelId = passageModelId == null ? null : passageModelId.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getPassageName() {
        return passageName;
    }

    public void setPassageName(String passageName) {
        this.passageName = passageName;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

}
