package com.huashi.bill.bill.service;

import java.util.Map;

import com.huashi.bill.bill.model.FluxDiscountModel;

public interface IBillService {

	/**
	 * 
	 * TODO 获取流量计费
	 * 
	 * @param userId
	 * 
	 * @param packages
	 * @param mobile
	 * @return
	 */
	FluxDiscountModel getFluxDiscountPrice(int userId, String packages, String mobile) 
			throws IllegalArgumentException;

	// double getFluxDiscountPrice(String appkey, String packages, String
	// mobile);
	
	/**
	 * 
	 * TODO 获取用户消费报表数据（短信、语音、流量等数据）
	 * 
	 * @param userId
	 *            用户ID
	 * @param platformType
	 *            平台类型
	 * @param limitSize
	 *            显示条数
	 * @return
	 */
	Map<String, Object> getConsumptionReport(int userId, int platformType, int limitSize);

	/**
	 * 
	 * TODO 根据平台类型更新所有人的消费记录信息
	 * 
	 * @param platformType
	 */
	void updateConsumptionReport(int platformType);
}
