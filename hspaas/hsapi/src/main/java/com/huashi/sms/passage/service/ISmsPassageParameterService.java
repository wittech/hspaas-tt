/**
 * 
 */
package com.huashi.sms.passage.service;

import java.util.List;

import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.sms.passage.domain.SmsPassageParameter;

/**
 * 通道参数服务
 * 
 * @author Administrator
 *
 */
public interface ISmsPassageParameterService {
	/**
	 * 根据通道id获取通道参数
	 * 
	 * @param passageId
	 * @return
	 */
	List<SmsPassageParameter> findByPassageId(int passageId);

	/**
	 * 
	 * TODO 根据通道代码获取参数详细信息（主要针对回执报告和上行信息）
	 * 
	 * @param passageCode
	 *            通道代码（当通道调用类型为 状态回执推送 或 上行推送时，passage_url 值为 通道代码[唯一]）
	 * @param callType
	 *            通道调用类型，本例主要用于[状态回执推送,上行推送]
	 * @return
	 */
	SmsPassageParameter getByType(PassageCallType callType, String passageCode);
}
