package com.huashi.fs.product.domain;

import java.io.Serializable;
import java.util.Date;

public class FluxProduct implements Serializable{
    
	private static final long serialVersionUID = 383929711604257473L;

	private Integer id;

    private String code;

    private String innerCode;

    private String name;

    private Integer status;

    private Integer operator;

    private Integer parValue;

    private Integer scope;

    private Double outPriceOff;

    private String provinceCode;

    private Double officialPrice;

    private String validatyDescription;

    private String description;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getInnerCode() {
        return innerCode;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode == null ? null : innerCode.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public Integer getParValue() {
        return parValue;
    }

    public void setParValue(Integer parValue) {
        this.parValue = parValue;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public Double getOutPriceOff() {
        return outPriceOff;
    }

    public void setOutPriceOff(Double outPriceOff) {
        this.outPriceOff = outPriceOff;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode == null ? null : provinceCode.trim();
    }

    public Double getOfficialPrice() {
        return officialPrice;
    }

    public void setOfficialPrice(Double officialPrice) {
        this.officialPrice = officialPrice;
    }

    public String getValidatyDescription() {
        return validatyDescription;
    }

    public void setValidatyDescription(String validatyDescription) {
        this.validatyDescription = validatyDescription == null ? null : validatyDescription.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}