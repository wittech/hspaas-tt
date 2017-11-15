package com.huashi.fs.order.domain;

import java.io.Serializable;
import java.util.Date;

public class FluxOrder implements Serializable{
	private static final long serialVersionUID = 7568673074768649527L;

	private Long id;

    private Byte orderType;

    private Long userId;

    private String tradeNo;

    private String mobile;

    private String prodcutName;

    private Integer productPackagesSize;

    private Byte payStatus;

    private Byte tradeStatus;

    private Byte tradeRefund;

    private Double officialPrice;

    private Double priceOff;

    private Double dealPrice;

    private String local;

    private Integer passageGroupId;

    private Integer passageId;

    private Integer providerId;

    private Integer providerProdId;

    private String providerTradeno;

    private String providerResultCode;

    private Integer resultCode;

    private Date createTime;

    private Date finishTime;

    private Date submitTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Byte getOrderType() {
        return orderType;
    }

    public void setOrderType(Byte orderType) {
        this.orderType = orderType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getProdcutName() {
        return prodcutName;
    }

    public void setProdcutName(String prodcutName) {
        this.prodcutName = prodcutName == null ? null : prodcutName.trim();
    }

    public Integer getProductPackagesSize() {
        return productPackagesSize;
    }

    public void setProductPackagesSize(Integer productPackagesSize) {
        this.productPackagesSize = productPackagesSize;
    }

    public Byte getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Byte payStatus) {
        this.payStatus = payStatus;
    }

    public Byte getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(Byte tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public Byte getTradeRefund() {
        return tradeRefund;
    }

    public void setTradeRefund(Byte tradeRefund) {
        this.tradeRefund = tradeRefund;
    }

    public Double getOfficialPrice() {
        return officialPrice;
    }

    public void setOfficialPrice(Double officialPrice) {
        this.officialPrice = officialPrice;
    }

    public Double getPriceOff() {
        return priceOff;
    }

    public void setPriceOff(Double priceOff) {
        this.priceOff = priceOff;
    }

    public Double getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(Double dealPrice) {
        this.dealPrice = dealPrice;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local == null ? null : local.trim();
    }

    public Integer getPassageGroupId() {
        return passageGroupId;
    }

    public void setPassageGroupId(Integer passageGroupId) {
        this.passageGroupId = passageGroupId;
    }

    public Integer getPassageId() {
        return passageId;
    }

    public void setPassageId(Integer passageId) {
        this.passageId = passageId;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public Integer getProviderProdId() {
        return providerProdId;
    }

    public void setProviderProdId(Integer providerProdId) {
        this.providerProdId = providerProdId;
    }

    public String getProviderTradeno() {
        return providerTradeno;
    }

    public void setProviderTradeno(String providerTradeno) {
        this.providerTradeno = providerTradeno == null ? null : providerTradeno.trim();
    }

    public String getProviderResultCode() {
        return providerResultCode;
    }

    public void setProviderResultCode(String providerResultCode) {
        this.providerResultCode = providerResultCode == null ? null : providerResultCode.trim();
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }
}