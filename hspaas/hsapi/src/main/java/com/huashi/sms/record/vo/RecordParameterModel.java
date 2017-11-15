package com.huashi.sms.record.vo;

import java.io.Serializable;

public class RecordParameterModel implements Serializable {

	private static final long serialVersionUID = -6313094398425420761L;
	private String userId;
	private String phoneNumber;
	private String startDate;
	private String endDate;
	private String currentPage;
	
	private Long startDateLong;
	private Long endDateLong;
	
	private int startPage;
	private int pageRecord;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	public Long getStartDateLong() {
		return startDateLong;
	}

	public void setStartDateLong(Long startDateLong) {
		this.startDateLong = startDateLong;
	}

	public Long getEndDateLong() {
		return endDateLong;
	}

	public void setEndDateLong(Long endDateLong) {
		this.endDateLong = endDateLong;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getPageRecord() {
		return pageRecord;
	}

	public void setPageRecord(int pageRecord) {
		this.pageRecord = pageRecord;
	}

}