package com.huashi.listener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huashi.listener.prervice.SmsPassagePrervice;

/**
 * 
 * TODO 短信发送回调处理器
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年10月11日 下午5:36:43
 */

@Controller
@RequestMapping("/sms")
public class SmsSendbackController extends BaseController {

	@Autowired
	private SmsPassagePrervice smsPassagePrervice;

	/**
	 * TODO 接收通道厂商回执状态报告
	 *
	 * @param filterCode
	 * 		过滤码
	 * @param providerCode
	 * 		通道简码
	 * @param encoding
	 * 		编码方式
	 * @return
	 */
	@RequestMapping("/status/{filter_code}/{provider_code}/{encoding}")
	@ResponseBody
	public String status(@PathVariable("filter_code") Integer filterCode, 
			@PathVariable("provider_code") String providerCode,
			@PathVariable("encoding") String encoding) {
		try {
			
			smsPassagePrervice.doPassageStatusReport(providerCode, doTranslateParameter(filterCode, encoding));
		} catch (Exception e) {
			logger.error("解析回执报告数据失败", e);
		}
		return SUCCESS;
	}
	
	/**
	 * 
	   * TODO 接收通道厂商上行数据
	 *
	   * @param filterCode
	   * @param providerCode
	   * @param encoding
	   * @return
	 */
	@RequestMapping("/mo/{filter_code}/{provider_code}/{encoding}")
	@ResponseBody
	public String mo(@PathVariable("filter_code") Integer filterCode, @PathVariable("provider_code") String providerCode,
			@PathVariable("encoding") String encoding) {
		try {
			smsPassagePrervice.doPassageMoReport(providerCode, doTranslateParameter(filterCode, encoding));
		} catch (Exception e) {
			logger.error("解析上行报告数据失败", e);
		}
		return SUCCESS;
	}
	

}
