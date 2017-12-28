package com.huashi.exchanger.resolver.http.custom.lanjinglz;

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
  * TODO 北京蓝鲸联众发送短信处理器
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2017年7月31日 下午8:29:03
 */
@Component
public class LanjinglianzhongPassageResolver extends AbstractPassageResolver{
	
	@Override
	public List<ProviderSendResponse> send(SmsPassageParameter parameter,String mobile, String content,
			String extNumber) {
		
		try {
			TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());
			
			// 转换参数，并调用网关接口，接收返回结果
			String result = HttpClientUtil.post(parameter.getUrl(), sendRequest(tparameter, mobile, content, extNumber), "GBK");
			
			List<ProviderSendResponse> response = sendResponse(result, parameter.getSuccessCode());
			logger.info("蓝鲸联众POST返回值：{}, 成功码：{}, 处理结果：{}", result, 
					parameter.getSuccessCode(), JSON.toJSONString(response));
			
			// 解析返回结果并返回
			return response;
//			return sendResponse(result, parameter.getSuccessCode());
		} catch (Exception e) {
			logger.error("蓝鲸联众解析失败", e);
			throw new RuntimeException("蓝鲸联众解析失败");
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
		Map<String, Object> params = new HashMap<>();
		params.put("user", tparameter.getString("account"));
		params.put("password", DigestUtils.md5Hex(tparameter.getString("password")));
		params.put("tele", mobile);
		params.put("msg", content);
		params.put("extno", extNumber == null ? "" : extNumber);
		
//		params.put("addseqno", "");
//		此参数可选，参数等于1时，如果短信内容超过限制的字数，同时该通道不支持长短信时，系统会自动将短信分成多条，并在每条的前面加上序号。例如：1/3)... 2/3)... 3/3)..
		
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
		
		// 判断接口返回是否为成功
		if(StringUtils.isEmpty(result)) {
            return null;
        }
		
		String[] report = result.split(":");
		String sid = null;
		List<ProviderSendResponse> list = new ArrayList<>();
		if(StringUtils.isNotEmpty(report[0]) && successCode.equalsIgnoreCase(report[0])) {
			// ok:mid:tele,tele（多个号码返回格式）
			sid = report[1];
			String[] mobiles = report[2].replaceAll("\n", "").split(",");
			
			ProviderSendResponse response = null;
			for (String mobile : mobiles) {
				response = new ProviderSendResponse();
				response.setMobile(mobile);
				response.setStatusCode(successCode);
				response.setSid(sid);
				response.setSuccess(true);
				response.setRemark(String.format("status:%s,msg_id:%s", successCode, sid));
				
				list.add(response);
			}
		} else {
			ProviderSendResponse response = new ProviderSendResponse();
			response.setStatusCode(report[0]);
			response.setSid(sid);
			response.setSuccess(false);
			response.setRemark(String.format("status:%s, error_msg:%s", report[0], report[1]));
			
			list.add(response);
		} 
		
		return list;
	}
	
	@Override
	public List<SmsMtMessageDeliver> mtDeliver(String report, String successCode) {
		try {
			logger.info("下行状态报告简码：{} =========={}", code(), report);
			
			if(StringUtils.isEmpty(report)) {
                return null;
            }
			
			JSONObject jsonobj = JSONObject.parseObject(report);
			String msgId = jsonobj.getString("mid");
			String mobile = jsonobj.getString("mobile");
			String status = jsonobj.getString("msg");
			Object port = jsonobj.get("port");
			
			if(StringUtils.isEmpty(msgId) || StringUtils.isEmpty(mobile) || StringUtils.isEmpty(status)) {
				logger.error("蓝鲸联众下行状态报告异常 : {}", report);
				return null;
			}
				
			String[] msgIds = msgId.split(",");
			String[] mobiles = mobile.split(",");
			String[] statuses = status.split(",");
			if(msgIds.length != mobiles.length || msgIds.length != statuses.length) {
				logger.error("蓝鲸联众下行状态报告数量不匹配 : {}", report);
				return null;
			}

			List<SmsMtMessageDeliver> list = new ArrayList<>();

			SmsMtMessageDeliver response = null;
			for(int i = 0; i< msgIds.length; i++) {
				response = new SmsMtMessageDeliver();
				response.setMsgId(msgIds[i]);
				response.setMobile(mobiles[i]);
				response.setCmcp(CMCP.local(mobiles[i]).getCode());
				response.setStatusCode(statuses[i]);
				response.setStatus((StringUtils.isNotEmpty(statuses[i]) && statuses[i].equalsIgnoreCase(successCode)
						? DeliverStatus.SUCCESS.getValue() : DeliverStatus.FAILED.getValue()));
				response.setDeliverTime(DateUtil.getNow());
				response.setCreateTime(new Date());
				response.setRemark(String.format("msg_id : %s, port : %s", msgIds[i], port == null ? "" : port.toString()));

				list.add(response);
			}

			// 解析返回结果并返回
			return list;
		} catch (Exception e) {
			logger.error("蓝鲸联众状态报告解析失败", e);
			throw new RuntimeException("蓝鲸联众状态报告解析失败");
		}
	}

	@Override
	public List<SmsMoMessageReceive> moReceive(String report, Integer passageId) {
		try {
			
			logger.info("上行报告简码：{} =========={}", code(), report);
			
			JSONObject jsonobj = JSONObject.parseObject(report);
			
			String msgId = jsonobj.getString("mid");
			String destId = jsonobj.getString("port");
			String mobile = jsonobj.getString("mobile");
			String content = jsonobj.getString("msg");
			

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
			logger.error("蓝鲸联众解析失败", e);
			throw new RuntimeException("蓝鲸联众解析失败");
		}
	}

	@Override
	public Object balance(Object param) {
		return 0;
	}

	@Override
	public String code() {
		return "lanjinglz";
	}
}
