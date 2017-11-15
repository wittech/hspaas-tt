package com.huashi.common.third.model;

import java.io.Serializable;

/**
 * 
 * TODO 手机号码归属地
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年2月9日 上午11:26:24
 */
public class MobileLocation implements Serializable {

	// eg : //__GetZoneResult_ = { mts:'1586819', province:'浙江', catName:'中国移动',
	// telString:'15868193450', areaVid:'30510', ispVid:'3236139',
	// carrier:'浙江移动'}

	private static final long serialVersionUID = -3370629022066979742L;
	// 浙江移动/浙江联通等
	private String carrier;
	private String catName;
	private String province;
	private String mts;

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getMts() {
		return mts;
	}

	public void setMts(String mts) {
		this.mts = mts;
	}

}
