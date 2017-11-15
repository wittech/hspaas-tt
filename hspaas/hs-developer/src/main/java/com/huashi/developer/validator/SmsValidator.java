package com.huashi.developer.validator;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huashi.developer.exception.ValidateException;
import com.huashi.developer.model.PassportModel;
import com.huashi.developer.model.SmsModel;
import com.huashi.developer.prervice.PassportPervice;

@Component
public class SmsValidator extends Validator {

//	private Logger logger = LoggerFactory.getLogger(SmsValidator.class);

	@Autowired
	private PassportPervice passportPervice;

	/**
	 * 
	   * TODO 用户参数完整性校验
	   * 
	   * @param paramMap
	   * @param ip
	   * @return
	   * @throws ValidateException
	 */
	public SmsModel validate(Map<String, String[]> paramMap, String ip) throws ValidateException {

		SmsModel model = new SmsModel();
		super.validteAndParseFields(model, paramMap);
		
		// 获取解析后的通行证
		PassportModel passportModel = passportPervice.getPassportWithMobile(paramMap, ip);
		
		model.setUserId(passportModel.getUserId());
		model.setFee(passportModel.getFee());
		model.setTotalFee(passportModel.getTotalFee());
		model.setIp(ip);

		return model;
	}

}
