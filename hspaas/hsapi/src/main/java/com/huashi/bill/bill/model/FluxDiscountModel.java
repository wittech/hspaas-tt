package com.huashi.bill.bill.model;

import java.io.Serializable;

public class FluxDiscountModel implements Serializable {

	private static final long serialVersionUID = 6689748131139108974L;

	// 产品ID
	private int prodcutId;
	// 产品名称
	private String productName;
	// 官方报价
	private double officialPrice;
	// 折扣价
	private double discountPrice;

	public int getProdcutId() {
		return prodcutId;
	}

	public void setProdcutId(int prodcutId) {
		this.prodcutId = prodcutId;
	}

	public double getOfficialPrice() {
		return officialPrice;
	}

	public void setOfficialPrice(double officialPrice) {
		this.officialPrice = officialPrice;
	}

	public double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public FluxDiscountModel() {
		super();
	}

	public FluxDiscountModel(int prodcutId, String productName, double officialPrice, double discountPrice) {
		super();
		this.prodcutId = prodcutId;
		this.productName = productName;
		this.officialPrice = officialPrice;
		this.discountPrice = discountPrice;
	}

}
