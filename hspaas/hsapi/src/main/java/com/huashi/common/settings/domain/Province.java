package com.huashi.common.settings.domain;

import com.huashi.sms.passage.domain.SmsPassageGroupDetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Province {
	private Integer id;

	private Integer code;

	private String name;

	private String fullName;

	private String localRegex;

	private Byte status;

	private Date createTime;

	private Integer select;

	private List<SmsPassageGroupDetail> detailList = new ArrayList<SmsPassageGroupDetail>();

	public Province(Integer code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

	public Province() {
		super();
	}

	// 全国通用的省份代码
	public static final Integer PROVINCE_CODE_ALLOVER_COUNTRY = 0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName == null ? null : fullName.trim();
	}

	public String getLocalRegex() {
		return localRegex;
	}

	public void setLocalRegex(String localRegex) {
		this.localRegex = localRegex == null ? null : localRegex.trim();
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getSelect() {
		return select;
	}

	public void setSelect(Integer select) {
		this.select = select;
	}

	public List<SmsPassageGroupDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<SmsPassageGroupDetail> detailList) {
		this.detailList = detailList;
	}

}