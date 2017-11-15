package com.huashi.web.controller;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.huashi.common.notice.service.IMessageSendService;
import com.huashi.common.util.RandomUtil;
import com.huashi.common.util.VerifyCodeUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.web.filter.PermissionClear;

@Controller
@PermissionClear
@RequestMapping("/verify_code")
public class VerifyCodeController extends BaseController {

	@Reference
	private IMessageSendService messageSendService;

	/**
	 * 
	   * TODO 获取验证码
	   * @param response
	   * @param httpSession
	 */
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public @ResponseBody void getVerifyCodeImage() {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		try {
			String verifyCode = VerifyCodeUtil.generateVerifyCode(4);
			session.setAttribute(DEFAULT_CAPTCHA_MD5_CODE_KEY, verifyCode);
			ImageIO.write(VerifyCodeUtil.drawGraphic(verifyCode), "JPEG", response.getOutputStream());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	   * TODO 对比SESSION验证码
	   * @param httpSession
	   * @param code
	   * 		页面传递值
	   * @return
	 */
	@RequestMapping(value = "/validate", method = RequestMethod.POST)
	public @ResponseBody boolean validate(String code) {
		Object o = session.getAttribute(DEFAULT_CAPTCHA_MD5_CODE_KEY);
		if (o == null)
			return false;
		if(StringUtils.isEmpty(code))
			return false;
		return o.toString().equalsIgnoreCase(code.trim());
	}
	
	/**
	 * 
	   * TODO 发送短信验证码
	   * @param httpSession
	   * @param mobile
	   * @return
	 */
	@RequestMapping(value = "/mcode", method = RequestMethod.POST)
	public @ResponseBody int getMessageCode(String mobile, String code) {
		if(!CMCP.isAvaiableMobile(mobile))
			return MobileCodeValidate.MOBILE_INVALID.getValue();
		
		if(!validate(code))
			return MobileCodeValidate.IMAGE_CODE_ERROR.getValue();
		
		if(!isAllowedSms())
			return MobileCodeValidate.SMS_FREQUENCY_LIMIT.getValue();
		
		// 调用发送短信记录前校验 图形验证码是否正确
		String smsCode = RandomUtil.getRandomNum();
		// 调用发送短信验证码信息（根据系统短信模板内容拼接相关短信内容）
		boolean isSuccess = messageSendService.sendVerifyCode(smsCode, mobile);
		if(isSuccess) {
			setSessionMobileSmsCode(mobile, smsCode);
			return MobileCodeValidate.SUCCESS.getValue();
		}
		return MobileCodeValidate.SMS_FAILED.getValue();
	}
	
	/**
	 * 
	   * TODO 验证短信验证码是否正确
	   * @param httpSession
	   * @param smsCode
	   * @return
	 */
	@RequestMapping(value = "/mcode_validate", method = RequestMethod.POST)
	public @ResponseBody boolean isMessageCodeRight(String smsCode) {
		JSONObject sessionCode = getSessionMobileSmsCode();
		if (sessionCode == null) {
			logger.info("验证码数据为空");
			return false;
		}
		
		// 验证码是否过期
		if(isExpired(sessionCode.getLong("time"), 180L)){
			logger.info("验证码已过期，生成时间：{}， 当前时间：{}", sessionCode.getLong("time"), System.currentTimeMillis());
			return false;
		}
		
		return sessionCode.getString("code").toString().equals(smsCode.trim());
	}
	
	/**
	 * 
	 * TODO 手机动态码验证信息
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年3月11日 下午11:18:09
	 */
	public enum MobileCodeValidate {
		
		SUCCESS(0, "成功"), MOBILE_INVALID(1, "手机号码无效"), IMAGE_CODE_ERROR(2, "图形验证码错误"),
			SMS_FREQUENCY_LIMIT(3, "发送过于频繁，请稍后"), SMS_FAILED(-1, "短信发送失败");

		private int value;
		private String title;

		private MobileCodeValidate(int value, String title) {
			this.value = value;
			this.title = title;
		}

		public int getValue() {
			return value;
		}

		public String getTitle() {
			return title;
		}

	}

}
