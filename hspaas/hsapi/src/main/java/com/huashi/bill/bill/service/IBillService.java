package com.huashi.bill.bill.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.huashi.bill.bill.model.FluxDiscountModel;
import com.huashi.bill.bill.model.SmsP2PBillModel;

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
	 * TODO 计算用户传递的短信内容条数
	 * 
	 * @param userId
	 * @param content
	 * @return
	 */
	int getSmsFeeByWords(int userId, String content) throws IllegalArgumentException;

	/**
	 * 
	 * TODO 计算用户点对点短信计费条数
	 * 
	 * @param userId
	 * @param p2pBody
	 *            点对点短信报文
	 * @return
	 */
	SmsP2PBillModel getSmsFeeInP2P(int userId, List<JSONObject> p2pBody);
	
	/**
	 * 
	 * TODO 计算用户模板点对点短信计费条数
	 * 
	 * @param userId
	 * @param content	
	 * 				模板内容
	 * @param p2pBody
	 *            点对点短信报文
	 * @return
	 */
	SmsP2PBillModel getSmsFeeInP2PTemplate(int userId, String content, List<JSONObject> p2pBody);
	
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
