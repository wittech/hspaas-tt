package com.huashi.developer.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huashi.constants.OpenApiCode;
import com.huashi.developer.model.SmsModel;
import com.huashi.developer.response.sms.SmsSendResponse;

@RestController
@RequestMapping(value = "/voice")
public class VoiceApi extends BasicApiSupport{

	/**
	 * 
	 * TODO 发送短信
	 * 
	 * @return
	 */
	@RequestMapping(value = "/send")
	public SmsSendResponse send(@RequestParam("model") SmsModel model) {
		// 验证参数有效性
		
		// 调用
		SmsSendResponse response = new SmsSendResponse();
		response.setCode(OpenApiCode.SUCCESS);
		response.setMessage("发送成功");
		response.setFee("1");
		response.setSid("152586894564448521");
		
		return response;
	}

	/**
	 * 
	 * TODO 批量发送短信
	 * 
	 * @return
	 */
	public String batchSendMessage() {
		return "0";
	}

	/**
	 * 
	 * TODO 获取余额
	 * 
	 * @return
	 */
	public String getBalance() {
		return "0";
	}
}
