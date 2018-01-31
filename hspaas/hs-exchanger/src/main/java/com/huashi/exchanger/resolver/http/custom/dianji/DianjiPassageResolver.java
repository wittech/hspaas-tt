package com.huashi.exchanger.resolver.http.custom.dianji;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.util.DateUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.http.HttpClientUtil;
import com.huashi.exchanger.resolver.http.custom.AbstractPassageResolver;
import com.huashi.exchanger.template.handler.RequestTemplateHandler;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

/**
 * 
  * TODO 上海点集发送短信处理器
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2016年11月13日 下午3:14:44
 */
@Component
public class DianjiPassageResolver extends AbstractPassageResolver{
	
	@Override
	public List<ProviderSendResponse> send(SmsPassageParameter parameter,String mobile, String content,
			String extNumber) {
		
		try {
			TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());
			
			// 转换参数，并调用网关接口，接收返回结果
			String result = HttpClientUtil.post(parameter.getUrl(), sendRequest(tparameter, mobile, content, extNumber));
			
			// 解析返回结果并返回
			return sendResponse(result, parameter.getSuccessCode());
		} catch (Exception e) {
			logger.error("解析失败", e);
			throw new RuntimeException("解析失败");
		}
	}
	
	/**
	 * 
	   * TODO 发送短信组装请求信息
	   * @param tparameter
	   * @param mobile
	   * @param content
	   * 		短信内容
	   * @param extNumber
	   * 		扩展号
	   * @return
	 */
	private static Map<String, Object> sendRequest(TParameter tparameter, String mobile, String content, String extNumber) {
		String timestamps = System.currentTimeMillis() + "";
		String password = tparameter.getString("password");
		
		Map<String, Object> params = new HashMap<>();
		params.put("account", tparameter.getString("account"));
		params.put("password", DigestUtils.md5Hex(password + mobile + timestamps));
		params.put("mobile", mobile);
		params.put("content", content);
		params.put("timestamps", timestamps);
		params.put("extNumber", extNumber == null ? "" : extNumber);
		
		return params;
	}
	
	/**
	 * 
	   * TODO 解析发送返回值
	   * 
	   * @param result
	   * @param successCode
	   * @return
	 */
	private static List<ProviderSendResponse> sendResponse(String result, String successCode) {
		if (StringUtils.isEmpty(result)) {
            return null;
        }
		
		successCode = StringUtils.isEmpty(successCode) ? COMMON_MT_STATUS_SUCCESS_CODE : successCode;
		
		Map<String, Object> m = JSON.parseObject(result, new TypeReference<Map<String, Object>>(){});
		Object o = m.get("Rets");
		if (o == null || StringUtils.isEmpty(o.toString())) {
            return null;
        }
		List<ProviderSendResponse> list = new ArrayList<>();
		List<String> l = JSON.parseArray(o.toString(), String.class);
		ProviderSendResponse response;
		for (String s : l) {
			Map<String, Object> map = JSON.parseObject(s, new TypeReference<Map<String, Object>>(){});
			response = new ProviderSendResponse();
			response.setMobile(map.get("Mobile").toString());
			response.setStatusCode(map.get("Rspcode").toString());
			response.setSid(map.get("Msg_Id").toString());
			response.setSuccess(StringUtils.isNotEmpty(response.getStatusCode()) && successCode.equals(response.getStatusCode()));
			response.setRemark(JSON.toJSONString(map));
			
			list.add(response);
		}
		return list;
	}

	@Override
	public List<SmsMtMessageDeliver> mtDeliver(String report, String successCode) {
		try {
			logger.info("下行状态报告简码：{} =========={}", code(), report);
			
			JSONObject jsonobj = JSONObject.parseObject(report);
			String msgId = jsonobj.getString("Msg_Id");
			String mobile = jsonobj.getString("Mobile");
			String status = jsonobj.getString("Status");

			// String destId = jsonobj.getString("Dest_Id");
			// String extInfo = null;
			// if (jsonobj.containsKey("ExtInfo")) {
			// extInfo = jsonobj.getString("ExtInfo");
			// }

			List<SmsMtMessageDeliver> list = new ArrayList<>();

			SmsMtMessageDeliver response = new SmsMtMessageDeliver();
			response.setMsgId(msgId);
			response.setMobile(mobile);
			response.setCmcp(CMCP.local(mobile).getCode());
			response.setStatusCode(status);
			response.setStatus((StringUtils.isNotEmpty(status) && status.equalsIgnoreCase(successCode)
					? DeliverStatus.SUCCESS.getValue() : DeliverStatus.FAILED.getValue()));
			response.setDeliverTime(DateUtil.getNow());
			response.setCreateTime(new Date());
			response.setRemark(report);

			list.add(response);

			// 解析返回结果并返回
			return list;
		} catch (Exception e) {
			logger.error("解析失败", e);
			throw new RuntimeException("解析失败");
		}
	}

	@Override
	public List<SmsMoMessageReceive> moReceive(String report, Integer passageId) {
		try {
			
			logger.info("上行报告简码：{} =========={}", code(), report);
			
			JSONObject jsonobj = JSONObject.parseObject(report);
			String msgId = jsonobj.getString("Msg_Id");
			String destId = jsonobj.getString("Dest_Id");
			String mobile = jsonobj.getString("Mobile");
			String content = jsonobj.getString("Content");

			List<SmsMoMessageReceive> list = new ArrayList<>();

			SmsMoMessageReceive response = new SmsMoMessageReceive();
			response.setPassageId(passageId);
			response.setMsgId(msgId);
			response.setMobile(mobile);
			response.setContent(content);
			response.setDestnationNo(destId);
			response.setReceiveTime(DateUtil.getNow());
			response.setCreateTime(new Date());
			response.setCreateUnixtime(response.getCreateTime().getTime());
			list.add(response);

			// 解析返回结果并返回
			return list;
		} catch (Exception e) {
			logger.error("解析失败", e);
			throw new RuntimeException("解析失败");
		}
	}

	@Override
	public Object balance(Object param) {
		return 0;
	}

	@Override
	public String code() {
		return "dianji";
	}
}
