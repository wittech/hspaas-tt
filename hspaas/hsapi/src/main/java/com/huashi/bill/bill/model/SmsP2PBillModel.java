package com.huashi.bill.bill.model;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

public class SmsP2PBillModel implements Serializable {
	
	private static final long serialVersionUID = -7649376186514466434L;
	private int totalFee;
	private List<JSONObject> p2pBodies;

	public int getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(int totalFee) {
		this.totalFee = totalFee;
	}

	public List<JSONObject> getP2pBodies() {
		return p2pBodies;
	}

	public void setP2pBodies(List<JSONObject> p2pBodies) {
		this.p2pBodies = p2pBodies;
	}

	public SmsP2PBillModel(int totalFee, List<JSONObject> p2pBodies) {
		super();
		this.totalFee = totalFee;
		this.p2pBodies = p2pBodies;
	}

	public SmsP2PBillModel() {
		super();
	}

}
