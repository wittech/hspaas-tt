package com.huashi.exchanger.resolver.http.custom.cmccopen;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.common.util.DateUtil;
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
 * TODO 移动能力平台短信处理器
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年8月23日 下午2:56:32
 * @url http://aep.cmccopen.cn/sms/sendTemplateSms/v1
 */
@Component
public class CmccopenPassageResolver extends AbstractPassageResolver {
	
	@Override
	public List<ProviderSendResponse> send(SmsPassageParameter parameter, String mobile, 
			String content, String extNumber) {
		try {
			TParameter tparameter = RequestTemplateHandler.parse(parameter
					.getParams());

			// 转换参数，并调用网关接口，接收返回结果
			String result = HttpClientUtil.postJson(parameter.getUrl(), sendRequestHeader(tparameter),
					sendRequestParameter(tparameter, mobile, content, extNumber,
					parameter.getSmsTemplateId(), 
					buildVariableParamsReport(parameter.getVariableParamNames(), parameter.getVariableParamValues())));
			
			// 解析返回结果并返回
			return sendResponse(result, parameter.getSuccessCode());
		} catch (Exception e) {
			logger.error("移动能力发送失败", e);
			throw new RuntimeException("移动能力发送");
		}
	}
	
	/**
	 * 
	   * TODO 组装JSON变量参数报文信息
	   * @param paramNames
	   * 		参数名称
	   * @param paramValues
	   * 		参数值
	   * @return
	 */
	private String buildVariableParamsReport(String[] paramNames, String[] paramValues) {
		try {
			JSONObject paramReport = new JSONObject();
			for(int i=0; i < paramNames.length; i++) {
				paramReport.put(paramNames[i], paramValues[i]);
			}
			return paramReport.toJSONString();
		} catch (Exception e) {
			logger.error("移动能力组装发送参数信息", e);
			return null;
		}
	} 

	/**
	 * 
	   * TODO 生成随机串
	   * 
	   * @return
	 */
	private static String getNonce() {
		Random random = new Random();
		char CHAR_ARRAY[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
				'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
				'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
				'W', 'X', 'Y', 'Z' };
		
		StringBuffer sb = new StringBuffer();
		int nextPos = CHAR_ARRAY.length;
		int tmp = 0;
		for (int i = 0; i < 25; i++) {
			tmp = random.nextInt(nextPos);
			sb.append(CHAR_ARRAY[tmp]);
		}
		return sb.toString();
	}

	/**
	 * 
	 * TODO 获取UTC时间
	 * 
	 * @return
	 */
	private static String getUTCtime() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		System.out.println(sdf.format(calendar.getTime()));
		return sdf.format(calendar.getTime());
	}

	/**
	 * 
	 * TODO 获取密码摘要信息，生成规则：Base64 (SHA256 (Nonce + Created + Password))
	 * 
	 * @param nonce
	 * @param created
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static String getPasswordDigest(String nonce, String created,
			String password) throws NoSuchAlgorithmException {
		String signature = nonce + created + password;

		MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
		msgDigest.update(signature.getBytes());

		return Base64.encodeBase64String(msgDigest.digest());
	}

	/**
	 * 
	 * TODO 发送短信组装请求信息
	 * 
	 * @param tparameter
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static Map<String, Object> sendRequestHeader(TParameter tparameter)
			throws NoSuchAlgorithmException {
		String username = tparameter.getString("appkey");
	
		String nonce = getNonce();
		String created = getUTCtime();
		String password = tparameter.getString("password");

		Map<String, Object> headers = new HashMap<>();
		headers.put("Authorization",
				"WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"AppKey\"");
		headers.put("X-WSSE", MessageFormat.format("UsernameToken Username=\"{0}\",PasswordDigest=\"{1}\" ,Nonce=\"{2}\",Created=\"{3}\"",
								username, getPasswordDigest(nonce, created, password), nonce, created));
		headers.put("Content-Type", "application/json;charset=UTF-8");

		return headers;
	}

	/**
	 * 
	 * TODO 发送短信组装请求信息
	 * 
	 * @param tparameter
	 * @param mobile
	 * @param content
	 *            短信内容
	 * @param extNumber
	 *            扩展号	 *            
	 * @param passageTemplateId
	 * 			  通道方短信模板ID	
	 * @param varParams
	 * 			通道变量信息
	 * 
	 * @return
	 */
	private static String sendRequestParameter(TParameter tparameter, String mobile, String content,
			String extNumber, String passageTemplateId, String varParams) {

		JSONObject params = new JSONObject();
		params.put("from", tparameter.get("terminal_no") + (StringUtils.isNotBlank(extNumber) ? extNumber : ""));
		params.put("smsTemplateId", passageTemplateId);
		params.put("paramValue", JSON.parse(varParams));
		params.put("to", "+86" + mobile);

		// 状态URL，暂绑定唯一（网站绑定）
		// params.put("notifyURL", tparameter.get("callback"));
		// params.put("profileId", tparameter.get("profileId"));

		return params.toJSONString();
	}

	/**
	 * 
	 * TODO 解析发送返回值
	 * 
	 * @param result
	 * @param successCode
	 * @return
	 */
	private List<ProviderSendResponse> sendResponse(String result,
			String successCode) {
		if (StringUtils.isEmpty(result))
			return null;

		successCode = StringUtils.isEmpty(successCode) ? COMMON_MT_STATUS_SUCCESS_CODE
				: successCode;

		JSONObject report = JSON.parseObject(result);

		JSONObject resultElement = report.getJSONObject("result");
		if(resultElement == null) {
			logger.error("移动能力平台调用失败：{}", result);
			return null;
		}
		
		if(report.get("code") == null)
			return null;
		
		List<ProviderSendResponse> list = new ArrayList<>();
		ProviderSendResponse response = new ProviderSendResponse();
		response.setMobile(resultElement.getString("to").replace("+86", ""));
		response.setStatusCode(report.getString("code"));
		response.setSid(resultElement.getString("smsMsgId"));
		response.setSuccess(StringUtils.isNotEmpty(response.getStatusCode())
				&& successCode.equals(response.getStatusCode()));
		response.setRemark(result);

		list.add(response);
		
		// 如果提交通道方成功，则设置REDIS对应关系
		if(response.isSuccess())
			setReportMsgIdWithMobile(response.getSid(), response.getMobile());
		
		return list;
	}
	
	@Override
	public List<SmsMtMessageDeliver> mtDeliver(String report, String successCode) {
		try {
			logger.info("下行状态报告简码：{} =========={}", code(), report);

			JSONObject jsonobj = JSONObject.parseObject(report);
			String msgId = jsonobj.getString("smsMsgId");
			String status = jsonobj.getString("status");

			List<SmsMtMessageDeliver> list = new ArrayList<>();

			SmsMtMessageDeliver response = new SmsMtMessageDeliver();
			response.setMsgId(msgId);
			response.setStatusCode(status);
			response.setStatus((StringUtils.isNotEmpty(status)
					&& status.equalsIgnoreCase(successCode) ? DeliverStatus.SUCCESS
					.getValue() : DeliverStatus.FAILED.getValue()));
			response.setDeliverTime(DateUtil.getNow());
			response.setCreateTime(new Date());
			response.setRemark(report);
			
			// 获取发送时设置的提交MSG_ID和MOBILE对应关系数据
			String mobile = getReportMsgIdWithMobile(msgId);
			if(StringUtils.isNotEmpty(mobile)) {
				response.setMobile(mobile);
				removeReportMsgIdWithMobile(msgId);
			}

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

//EG.			{	"SmsMsgId": "000002031104201709021009289451",	"From": "8618368031231",	"To": "10657526110809012",	"Body": "噢噢噢噢噢噢噢噢哦哦哦"}

			JSONObject jsonobj = JSONObject.parseObject(report);
			String msgId = jsonobj.getString("SmsMsgId");
			String destId = jsonobj.getString("To");
			
			// 手机号码前两位为86 ，需要截取去掉
			String mobile = StringUtils.isEmpty(jsonobj.getString("From")) ? "" : jsonobj.getString("From").substring(2);
			String content = jsonobj.getString("Body");
			
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
		return "cmccopen";
	}

	@Override
	public List<SmsMtMessageDeliver> mtPullDeliver(TParameter tparameter,
			String url, String successCode) {
		throw new RuntimeException("暂不提供");
	}

	@Override
	public List<SmsMoMessageReceive> moPullReceive(TParameter tparameter,
			String url, Integer passageId) {
		throw new RuntimeException("暂不提供");
	}
}
