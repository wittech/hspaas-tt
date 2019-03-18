package com.huashi.mms.passage.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MmsPassage {

    private Integer                   id;

    private String                    name;

    private String                    code;

    private Integer                   cmcp;

    private Integer                   priority;

    private Integer                   hspaasTemplateId;

    private Integer                   status;

    private String                    remark;

    private String                    accessCode;

    private String                    account;

    private Integer                   payType;

    private Integer                   balance;

    private Integer                   mobileSize;

    private Integer                   packetsSize;

    private Integer                   connectionSize;

    private Integer                   readTimeout;

    private Integer                   extNumber;

    private Integer                   bornTerm;

    private Date                      createTime;

    private Date                      updateTime;

    /**
     * 参数集合信息
     */
    private List<MmsPassageParameter> parameterList = new ArrayList<>();

    /**
     * 省份集合信息
     */
    private List<MmsPassageProvince>  provinceList  = new ArrayList<>();

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Integer getCmcp() {
        return cmcp;
    }

    public void setCmcp(Integer cmcp) {
        this.cmcp = cmcp;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode == null ? null : accessCode.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getMobileSize() {
        return mobileSize;
    }

    public void setMobileSize(Integer mobileSize) {
        this.mobileSize = mobileSize;
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

    public Integer getExtNumber() {
        return extNumber;
    }

    public void setExtNumber(Integer extNumber) {
        this.extNumber = extNumber;
    }

    public Integer getBornTerm() {
        return bornTerm;
    }

    public void setBornTerm(Integer bornTerm) {
        this.bornTerm = bornTerm;
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

    public List<MmsPassageParameter> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<MmsPassageParameter> parameterList) {
        this.parameterList = parameterList;
    }

    public List<MmsPassageProvince> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<MmsPassageProvince> provinceList) {
        this.provinceList = provinceList;
    }

    public Integer getHspaasTemplateId() {
        return hspaasTemplateId;
    }

    public void setHspaasTemplateId(Integer hspaasTemplateId) {
        this.hspaasTemplateId = hspaasTemplateId;
    }
}
