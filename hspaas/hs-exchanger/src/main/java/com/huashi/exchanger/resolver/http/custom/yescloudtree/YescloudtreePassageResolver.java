package com.huashi.exchanger.resolver.http.custom.yescloudtree;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.huashi.common.util.DateUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.http.HttpClientManager;
import com.huashi.exchanger.resolver.http.custom.AbstractPassageResolver;
import com.huashi.exchanger.template.handler.RequestTemplateHandler;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

/**
 * 
 * TODO 云之树短信平台接口实现
 * 
 * @http://www.yescloudtree.cn/
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2017年8月13日 下午4:04:39
 */
@Component
public class YescloudtreePassageResolver extends AbstractPassageResolver {
	
	/**
	 * 最大连接数
	 */
	private Integer maxPerRoute = 20;

	/**
	 * 返回报告中是否含有消息体标识字段，如果含有如下字段，则标识有消息体，需要处理
	 */
	private static final String REPORT_HAS_BODY_FLAG = "Mobile";
	
	@Override
	public List<ProviderSendResponse> send(SmsPassageParameter parameter, String mobile, String content,
			String extNumber) {

		try {
			TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());

			// 转换参数，并调用网关接口，接收返回结果
			String result = HttpClientManager.post(parameter.getUrl(), request(tparameter, mobile, content, extNumber), maxPerRoute);

			// 解析返回结果并返回
			return sendResponse(result, parameter.getSuccessCode());
		} catch (Exception e) {
			logger.error("解析失败", e);
			throw new RuntimeException("解析失败");
		}
	}

	/**
	 * 
	 * TODO 下行发送短信组装请求信息
	 * 
	 * @param tparameter
	 * @param mobile
	 *            手机号码
	 * @param content
	 *            短信内容
	 * @param extNumber
	 *            扩展号码
	 * @return
	 */
	private static Map<String, Object> request(TParameter tparameter, String mobile, String content, String extNumber) {
		Map<String, Object> params = new HashMap<>();
		params.put("Action", "SendSms");
		params.put("UserName", tparameter.getString("account"));
		params.put("Password", signature(tparameter.getString("password")));

		// 目前对方接口多个号码分割采用 半角分号，需要转义
		params.put("Mobile", mobile.replaceAll(",", ";"));
		params.put("Message", content);
		// 扩展号，目前支持0-4位
		params.put("ExtCode", extNumber == null ? "" : extNumber);

		// 群发配置
		String isBatchSend = tparameter.getString("is_p2p");

		// 判断群发选项是否为空，不为空则设置
		if (StringUtils.isNotEmpty(isBatchSend)) {
            params.put("IsP2p", tparameter.getString("is_p2p"));
        }

		return params;
	}

	/**
	 * 
	 * TODO 下行回执/上行短信组装请求信息
	 * 
	 * @param tparameter
	 * @param actionName
	 * @return
	 */
	private static Map<String, Object> request(TParameter tparameter, String actionName) {
		Map<String, Object> params = new HashMap<>();
		params.put("UserName", tparameter.getString("account"));
		params.put("Password", signature(tparameter.getString("password")));
		params.put("Action", actionName);

		return params;
	}

	/**
	 *
	 * 状态报告分割符号
	 * eg.
	 * 0:<MOPacks><MOPack><DestId></DestId><Mobile></Mobile><Message></Message><MOTime></MOTime></MOPack></MOPacks>
	 */
	private static final String REPORT_SPLIT_CHARACTER = ":";

	/**
	 * 
	 * TODO 解析发送返回值
	 * 
	 * @param result
	 * @param successCode
	 * @return
	 */
	private List<ProviderSendResponse> sendResponse(String result, String successCode) {
		if (StringUtils.isEmpty(result)) {
            return null;
        }
		
		successCode = StringUtils.isEmpty(successCode) ? COMMON_MT_STATUS_SUCCESS_CODE : successCode;

		YescloudtreeResponse yescloudtreeResponse = parseReport(result);
		if(yescloudtreeResponse == null) {
			logger.error("云之树发送状态报告异常, {}", result);
			return null;
		}

		List<ProviderSendResponse> list = new ArrayList<>();

		StringReader read = new StringReader(yescloudtreeResponse.getReport());
		InputSource source = new InputSource(read);
		SAXBuilder sb = new SAXBuilder();
		try {
			Document doc = sb.build(source);
			Element root = doc.getRootElement();

			List<Element> data = root.getChildren();
			Namespace ns = root.getNamespace();
			ProviderSendResponse response;
			for (Element et : data) {
				if (et.getChild("Id", ns) == null) {
                    continue;
                }

				response = new ProviderSendResponse();
				response.setMobile(et.getChild("Mobile", ns).getText());
				response.setStatusCode(yescloudtreeResponse.getStatusCode());
				response.setSid(et.getChild("Id", ns).getText());
				response.setSendTime(DateUtil.getNow());
				response.setSuccess(StringUtils.isNotEmpty(response.getStatusCode())
						&& successCode.equals(response.getStatusCode()));
				response.setRemark(String.format("id : %s, mobile : %s , status : %s", response.getSid(),
						response.getMobile(), response.getStatusCode()));
				list.add(response);
			}

		} catch (JDOMException | IOException e) {
			logger.error("云之树发送报告解析失败, {}", result, e);
		}
		return list;
	}
	
	/**
	 * 
	 * TODO 解析发送返回值
	 * 
	 * @param result
	 * @param successCode
	 * @return
	 */
	private List<SmsMtMessageDeliver> deliverResponse(String result, String successCode) {
		// 保证有数据才解析
		if (StringUtils.isEmpty(result) && !result.contains(REPORT_HAS_BODY_FLAG)) {
            return null;
        }

		logger.info("下行状态报告简码：{} =========={}", code(), result);

		successCode = StringUtils.isEmpty(successCode) ? COMMON_MT_STATUS_SUCCESS_CODE : successCode;
		
		YescloudtreeResponse yescloudtreeResponse = parseReport(result);
		if(yescloudtreeResponse == null) {
			logger.error("云之树下行状态报告异常, {}", result);
			return null;
		}

		List<SmsMtMessageDeliver> list = new ArrayList<>();
		try {
			StringReader read = new StringReader(yescloudtreeResponse.getReport());
			InputSource source = new InputSource(read);
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(source);
			Element root = doc.getRootElement();

			List<Element> data = root.getChildren();
			Namespace ns = root.getNamespace();
			SmsMtMessageDeliver response;
			for (Element et : data) {
				if (et.getChild("Id", ns) == null) {
                    continue;
                }
				
				response = new SmsMtMessageDeliver();
				response.setMsgId(et.getChild("Id", ns).getText());
				response.setMobile(et.getChild("Mobile", ns).getText());
				response.setCmcp(CMCP.local(response.getMobile()).getCode());
				if (successCode.equalsIgnoreCase(et.getChild("Status", ns).getText())) {
					response.setStatusCode(COMMON_MT_STATUS_SUCCESS_CODE);
					response.setStatus(DeliverStatus.SUCCESS.getValue());
				} else {
					response.setStatusCode(et.getChild("ErrCode", ns).getText());
					response.setStatus(DeliverStatus.FAILED.getValue());
				}
				
				response.setDeliverTime(DateUtil.getNow());
				response.setRemark(String.format("msg_id : %s, mobile : %s , status_code : %s", response.getMsgId(),
						response.getMobile(), response.getStatusCode()));
				response.setCreateTime(new Date());

				list.add(response);
			}

		} catch (Exception e) {
			logger.error("云之树解析状态回执信息失败, {}", result, e);
		}
		return list;

	}

	/**
	 * 
	 * TODO 短信签名
	 * 
	 * @param password
	 * @return
	 */
	private static String signature(String password) {
		return DigestUtils.md5Hex(password);
	}

	/**
	 * 
	 * TODO 解析上行返回值
	 * 
	 * @param result
	 * @param passageId
	 * @return
	 */
	private List<SmsMoMessageReceive> moResponse(String result, Integer passageId) {
		// 保证有数据才解析
		if (StringUtils.isEmpty(result) && !result.contains(REPORT_HAS_BODY_FLAG)) {
            return null;
        }

		logger.info("上行报告简码：{} =========={}", code(), result);
		
		YescloudtreeResponse yescloudtreeResponse = parseReport(result);
		if(yescloudtreeResponse == null) {
			logger.error("云之树上行报告异常, {}", result);
			return null;
		}

		List<SmsMoMessageReceive> list = new ArrayList<>();
		
		StringReader read = new StringReader(yescloudtreeResponse.getReport());
		InputSource source = new InputSource(read);
		SAXBuilder sb = new SAXBuilder();
		try {
			Document doc = sb.build(source);
			Element root = doc.getRootElement();

			List<Element> data = root.getChildren();
			Namespace ns = root.getNamespace();
			SmsMoMessageReceive response;
			for (Element et : data) {
				if (et.getChild("Mobile", ns) == null) {
                    continue;
                }

				response = new SmsMoMessageReceive();
				response.setPassageId(passageId);
				response.setMobile(et.getChild("Mobile", ns).getText());
				response.setContent(java.net.URLDecoder.decode(et.getChild("Message", ns).getText(), "UTF-8"));
				response.setDestnationNo(et.getChild("DestId", ns).getText());
				response.setReceiveTime(unixtimeStamp2Date(et.getChild("MOTime", ns).getText()));
				response.setCreateTime(new Date());
				response.setCreateUnixtime(response.getCreateTime().getTime());
				list.add(response);
			}
		} catch (Exception e) {
			logger.error("云之树解析状态回执信息失败, {}", result, e);
		}
		return list;
	}

	@Override
    public Double balance(TParameter tparameter, String url, Integer passageId) {
        return 0d;
    }

	@Override
	public String code() {
		return "yescloudtree";
	}

	@Override
	public List<SmsMtMessageDeliver> mtDeliver(TParameter tparameter, String url, String successCode) {
		try {
			String result = HttpClientManager.post(url, request(tparameter, "ReportSms"), maxPerRoute);
			
			// 解析返回结果并返回
			return deliverResponse(result, successCode);
		} catch (Exception e) {
			logger.error("解析失败", e);
			throw new RuntimeException("解析失败");
		}
	}

	@Override
	public List<SmsMoMessageReceive> moReceive(TParameter tparameter, String url, Integer passageId) {
		try {
			String result = HttpClientManager.post(url, request(tparameter, "MoSms"), maxPerRoute);

			// 解析返回结果并返回
			return moResponse(result, passageId);
		} catch (Exception e) {
			logger.error("解析失败", e);
			throw new RuntimeException("解析失败");
		}
	}
	
	/**
	 * 
	   * TODO 解析状态报告
	   * @param report
	   * @return
	 */
	private static YescloudtreeResponse parseReport(String report) {
		if (StringUtils.isEmpty(report)) {
            return null;
        }

		if (!report.contains(REPORT_SPLIT_CHARACTER)) {
			return null;
		}

		return new YescloudtreeResponse(report.substring(0, report.indexOf(REPORT_SPLIT_CHARACTER)), 
				report.substring(report.indexOf(REPORT_SPLIT_CHARACTER) + 1, report.length()));
	}

	/**
	 * 返回值类定义
	 */
	static class YescloudtreeResponse implements Serializable{
		private static final long serialVersionUID = -4227037708935753641L;
		private String statusCode;
		private String report;

		public String getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(String statusCode) {
			this.statusCode = statusCode;
		}

		public String getReport() {
			return report;
		}

		public void setReport(String report) {
			this.report = report;
		}

		YescloudtreeResponse(String statusCode, String report) {
			super();
			this.statusCode = statusCode;
			this.report = report;
		}
		
	}

}
