/**
 * 
 */
package com.huashi.sms.passage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.sms.passage.dao.SmsPassageParameterMapper;
import com.huashi.sms.passage.domain.SmsPassageParameter;

/**
 * 通道参数配置服务
 * 
 * @author Administrator
 *
 */
@Service
public class SmsPassageParameterService implements ISmsPassageParameterService {

	@Autowired
	private SmsPassageParameterMapper smsPassageParameterMapper;

	@Override
	public List<SmsPassageParameter> findByPassageId(int passageId) {
		return smsPassageParameterMapper.findByPassageId(passageId);
	}

	@Override
	public SmsPassageParameter getByType(PassageCallType callType, String passageCode) {
		return smsPassageParameterMapper.getByTypeAndUrl(callType.getCode(), passageCode);
	}

}
