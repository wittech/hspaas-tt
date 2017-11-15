package com.huashi.bill.pay.service;

import java.util.Map;

import com.huashi.bill.pay.exception.AlipayException;
import com.huashi.bill.pay.model.AlipayModel;

public interface IAlipayService {

	/**
	 * 
	 * TODO 支付接口
	 * 
	 * @param model
	 * @return 订单信息
	 * @throws AlipayException
	 */
	String buildOrder(AlipayModel model) throws AlipayException;

	/**
	 * 
	 * TODO 校验支付宝返回的结果是否完成
	 * 
	 * @param paramMap
	 * @return
	 * @throws AlipayException
	 */
	boolean isPaySuccess(Map<String, String> paramMap, boolean isTranscoding) throws AlipayException;

	/**
	 * 
	 * TODO 异步后台订单逻辑
	 * 
	 * @param paramMap
	 * @throws AlipayException
	 */
	boolean notifyUpdateOrder(Map<String, String> paramMap) throws AlipayException;
}
