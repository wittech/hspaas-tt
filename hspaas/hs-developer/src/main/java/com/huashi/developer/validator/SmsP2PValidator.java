package com.huashi.developer.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huashi.bill.bill.model.SmsP2PBillModel;
import com.huashi.bill.bill.service.IBillService;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode.CommonApiCode;
import com.huashi.developer.exception.ValidateException;
import com.huashi.developer.model.PassportModel;
import com.huashi.developer.model.SmsP2PModel;
import com.huashi.developer.prervice.PassportPervice;

@Component
public class SmsP2PValidator extends Validator {

//	private Logger logger = LoggerFactory.getLogger(SmsValidator.class);

	@Reference
	private IBillService billService;
	@Autowired
	private PassportPervice passportPervice;
	@Reference
	private IUserBalanceService userBalanceService;

	/**
	 * 
	   * TODO 用户参数完整性校验
	   * 
	   * @param paramMap
	   * @param ip
	   * @return
	   * @throws ValidateException
	 */
	public SmsP2PModel validate(Map<String, String[]> paramMap, String ip) throws ValidateException {

		SmsP2PModel model = new SmsP2PModel();
		super.validteAndParseFields(model, paramMap);
		
		// 获取解析后的通行证	
		PassportModel passportModel = passportPervice.getPassport(paramMap, ip);
		
		model.setUserId(passportModel.getUserId());
		
		// 点对点短信如果内容为空，则返回错误码
		String body = model.getBody();
		if(StringUtils.isEmpty(body))
			throw new ValidateException(CommonApiCode.COMMON_P2P_BODY_IS_WRONG);

		List<JSONObject> p2pBodies = JSON.parseObject(body, new TypeReference<List<JSONObject>>(){});
		
		// 移除节点为空数据
		removeElementWhenNodeIsEmpty(p2pBodies);
		
		if(CollectionUtils.isEmpty(p2pBodies))
			throw new ValidateException(CommonApiCode.COMMON_P2P_BODY_IS_WRONG);
		
		SmsP2PBillModel smsP2PBillModel = null;
		try {
			// 获取本次短信内容计费条数
			smsP2PBillModel = billService.getSmsFeeInP2P(passportModel.getUserId(), p2pBodies);
			if (smsP2PBillModel == null || smsP2PBillModel.getTotalFee() == 0)
				throw new ValidateException(CommonApiCode.COMMON_BALANCE_EXCEPTION);
		} catch (Exception e) {
			logger.error("开发者中心计费错误", e);
			throw new ValidateException(CommonApiCode.COMMON_BALANCE_EXCEPTION);
		}
		
		// f.用户余额不足（通过计费微服务判断，结合4.1.6中的用户计费规则）
		boolean balanceEnough = userBalanceService.isBalanceEnough(passportModel.getUserId(),
				PlatformType.SEND_MESSAGE_SERVICE, (double) smsP2PBillModel.getTotalFee());
		if (!balanceEnough)
			throw new ValidateException(CommonApiCode.COMMON_BALANCE_NOT_ENOUGH);
		
		model.setFee(smsP2PBillModel.getTotalFee());
		model.setTotalFee(smsP2PBillModel.getTotalFee());
		model.setIp(ip);
		model.setUserId(passportModel.getUserId());
		model.setP2pBodies(smsP2PBillModel.getP2pBodies());

		return model;
	}
	
	/**
	 * 
	   * TODO 移除数据为空数据
	   * 
	   * @param p2pBodies
	 */
	private void removeElementWhenNodeIsEmpty(List<JSONObject> p2pBodies) throws ValidateException {
		if(CollectionUtils.isEmpty(p2pBodies))
			throw new ValidateException(CommonApiCode.COMMON_P2P_BODY_IS_WRONG);
		
		List<JSONObject> removeBodies = new ArrayList<>();
		for(JSONObject obj : p2pBodies) {
			if(StringUtils.isEmpty(obj.getString("mobile")) || StringUtils.isEmpty(obj.getString("content"))) {
				logger.error("点对点短信内容或者手机号码为空，移除，JSON内容：{}", obj.toJSONString());
				removeBodies.add(obj);
			}
		}
		
		if(CollectionUtils.isNotEmpty(removeBodies))
			p2pBodies.removeAll(removeBodies);
	}
	
}
