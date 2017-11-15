package com.huashi.bill.order.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * TODO 交易订单发票信息
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月16日 上午1:13:38
 */
public class TradeOrderInvoice implements Serializable {

	private static final long serialVersionUID = 7174530562480828412L;

	private Long id;

	private Long orderId;

	private String title;

	private String contactName;

	private String contactPhone;

	private String address;

	private Date createTime;
	
	private String createTimeText;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title == null ? null : title.trim();
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName == null ? null : contactName.trim();
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone == null ? null : contactPhone.trim();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address == null ? null : address.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateTimeText() {
		return createTimeText;
	}

	public void setCreateTimeText(String createTimeText) {
		this.createTimeText = createTimeText;
	}
}