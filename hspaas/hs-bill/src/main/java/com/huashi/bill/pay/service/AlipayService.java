package com.huashi.bill.pay.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.bill.order.service.ITradeOrderService;
import com.huashi.bill.pay.dao.AlipayBillMapper;
import com.huashi.bill.pay.domain.AlipayBill;
import com.huashi.bill.pay.exception.AlipayException;
import com.huashi.bill.pay.model.AlipayModel;

/**
 * 
 * TODO 支付宝服务实现
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月15日 上午12:36:27
 */
@Service
public class AlipayService implements IAlipayService {

	@Value("${aplipay.partner}")
	private String partner;
	@Value("${aplipay.key}")
	private String key;
	@Value("${aplipay.inputCharset:UTF-8}")
	private String inputCharset;
	@Value("${aplipay.signType:MD5}")
	private String signType;
	@Value("${aplipay.sellerEmail}")
	private String sellerEmail;
	@Value("${aplipay.alipayGatewayNew}")
	private String alipayGatewayNew;// 支付宝提供给商户的服务接入网关URL(新)
	@Value("${aplipay.verifyUrl}")
	private String verifyUrl;// 支付宝通知验证地址
	
	@Autowired
	private AlipayBillMapper alipayBillMapper;
	@Autowired
	private ITradeOrderService tradeOrderService;
	

	// 支付类型
	private static final String PAYMENT_TYPE = "1";
	private static final String ALIPAY_SERVICE_MODE = "create_direct_pay_by_user";
	private static final String PAGE_BUTTON_TEXT = "确认";
	private static final String ACTION_METHOD = "get";

	private Logger billger = LoggerFactory.getLogger(AlipayService.class);

	@Override
	public String buildOrder(AlipayModel model) throws AlipayException {
		Map<String, String> paraTemp = new HashMap<String, String>();
		try {
			paraTemp.put("payment_type", PAYMENT_TYPE);
			paraTemp.put("out_trade_no", model.getTradeNo());
			paraTemp.put("subject", model.getProduct());
			paraTemp.put("body", model.getBody());
			paraTemp.put("total_fee", model.getTotalFee());
			paraTemp.put("anti_phishing_key", model.getAntiPhishingKey());
			paraTemp.put("exter_invoke_ip", model.getIp());

			// 增加基本配置
			paraTemp.put("service", ALIPAY_SERVICE_MODE);
			paraTemp.put("partner", partner);
			paraTemp.put("return_url", model.getPageUrl());
			paraTemp.put("notify_url", model.getNotifyUrl());
			paraTemp.put("seller_email", sellerEmail);
			paraTemp.put("_input_charset", inputCharset);
			return buildForm(paraTemp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AlipayException(e);
		}
	}

	/**
	 * 
	 * TODO 构建表单FORM
	 * 
	 * @param sParaTemp
	 * @return
	 * @throws Exception
	 */
	public String buildForm(Map<String, String> sParaTemp) throws Exception {
		StringBuffer sbHtml = new StringBuffer();
		try {
			// 待请求参数数组
			Map<String, String> sPara = buildRequestPara(sParaTemp);
			List<String> keys = new ArrayList<String>(sPara.keySet());
			sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + alipayGatewayNew
					+ "_input_charset=" + inputCharset + "\" method=\"" + ACTION_METHOD + "\">");
			for (int i = 0; i < keys.size(); i++) {
				String name = (String) keys.get(i);
				String value = (String) sPara.get(name);
				sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
			}
			// submit按钮控件请不要含有name属性
			sbHtml.append("<input type=\"submit\" value=\"" + PAGE_BUTTON_TEXT + "\" style=\"display:none;\"></form>");
			sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbHtml.toString();
	}

	/**
	 * 生成要请求给支付宝的参数数组
	 * 
	 * @param sParaTemp
	 *            请求前的参数数组
	 * @return 要请求的参数数组
	 */
	private Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
		// 除去数组中的空值和签名参数
		Map<String, String> sPara = paraFilter(sParaTemp);
		try {
			// 生成签名结果
			String mysign = buildMysign(sPara);
			// 签名结果与签名方式加入请求提交参数组中
			sPara.put("sign", mysign);
			sPara.put("sign_type", signType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sPara;
	}

	/**
	 * 根据反馈回来的信息，生成签名结果
	 * 
	 * @param Params
	 *            通知返回来的参数数组
	 * @return 生成的签名结果
	 */
	private String getMysign(Map<String, String> Params) {
		Map<String, String> sParaNew = this.paraFilter(Params);// 过滤空值、sign与sign_type参数
		String mysign = this.buildMysign(sParaNew);// 获得签名结果
		return mysign;
	}

	/**
	 * 获取远程服务器ATN结果,验证返回URL
	 * 
	 * @param notify_id
	 *            通知校验ID
	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
	 *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
	private String verifyResponse(String notify_id) {
		// 获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
		StringBuilder url = new StringBuilder();
		url.append(verifyUrl).append("partner=").append(partner).append("&notify_id=").append(notify_id);
		return checkUrl(url.toString());
	}

	/**
	 * 获取远程服务器ATN结果
	 * 
	 * @param urlvalue
	 *            指定URL路径地址
	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
	 *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
	private static String checkUrl(String urlvalue) {
		String inputLine = "";
		try {
			URL url = new URL(urlvalue);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			inputLine = in.readLine().toString();
		} catch (Exception e) {
			e.printStackTrace();
			inputLine = "";
		}
		return inputLine;
	}

	@Override
	public boolean isPaySuccess(Map<String, String> params, boolean isTranscoding) throws AlipayException {
		String mysign = getMysign(params);
		String responseTxt = "true";
		if (params.get("notify_id") != null) {
			responseTxt = verifyResponse(params.get("notify_id"));
		}
		String sign = "";
		if (params.get("sign") != null) {
			sign = params.get("sign");
		}

		// 写日志记录（若要调试，请取消下面两行注释）
		// String sWord = "responseTxt=" + responseTxt +
		// "\n notify_url_bill:sign=" + sign + "&mysign="
		// + mysign + "\n 返回参数：" + AlipayCore.createLinkString(params);
		// AlipayCore.billResult(sWord);

		// 验证
		// responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
		// mysign与sign不等，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
		if (!mysign.equals(sign) || !responseTxt.equals("true")) {
            {
                return false;
            }
        }

		// 获取交易状态
		String tradeStatus = params.get("trade_status");
		if (tradeStatus.equals("TRADE_FINISHED") || tradeStatus.equals("TRADE_SUCCESS")) {
            {
                return true;
            }
        }

		if (!isTranscoding) {
			billger.warn("订单号交易失败：{} ", JSON.toJSONString(params));
			return true;
		}

		return false;
	}
	

	/**
	 * 除去数组中的空值和签名参数
	 * 
	 * @param sArray
	 *            签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	private Map<String, String> paraFilter(Map<String, String> sArray) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			if (sArray == null || sArray.size() <= 0) {
				return result;
			}
			for (String key : sArray.keySet()) {
				String value = sArray.get(key);
				if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
						|| key.equalsIgnoreCase("sign_type")) {
					continue;
				}
				result.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private String buildMysign(Map<String, String> array) {
		String mysign = "";
		try {
			String prestr = createLinkString(array); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
			prestr = prestr + key; // 把拼接后的字符串再与安全校验码直接连接起来
			mysign = this.md5(prestr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mysign;
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	private String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}

	private byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}

	public String md5(String text) throws Exception {
		return DigestUtils.md5Hex(getContentBytes(text, this.inputCharset));
	}

	@Override
	public boolean notifyUpdateOrder(Map<String, String> paramMap) throws AlipayException {
		String tradeNo = saveAlipayBill(paramMap);
		
		if (!isPaySuccess(paramMap, false)) {
            {
                return false;
            }
        }
		
		if(StringUtils.isEmpty(tradeNo)) {
            {
                return false;
            }
        }

		// 更新订单
		return tradeOrderService.updateOrderPayCompleted(tradeNo);
	}

	/**
	 * 
	   * TODO 插入支付宝订单对账记录
	   * 
	   * @param params
	 */
	private String saveAlipayBill(Map<String, String> params) {
		try {
			AlipayBill bill = new AlipayBill();
			bill.setOutTradeNo(params.get("out_trade_no"));
			bill.setSubject(params.get("subject"));
			bill.setPaymentType(params.get("payment_type"));
			bill.setTradeNo(params.get("trade_no"));
			bill.setTradeStatus(params.get("trade_status"));
			bill.setTotalFee(StringUtils.isEmpty(params.get("total_fee")) ? null : Double.parseDouble(params.get("total_fee")));
			bill.setNotifyId(params.get("notify_id"));
			bill.setNotifyTime(params.get("notify_time"));
			bill.setNotifyType(params.get("notify_type"));
			bill.setSellerEmail(params.get("seller_email"));
			bill.setBuyerEmail(params.get("buyer_email"));
			bill.setSellerId(params.get("seller_id"));
			bill.setBuyerId(params.get("buyer_id"));
			bill.setIsSuccess(Boolean.parseBoolean(params.get("is_success")));
			bill.setCreateTime(new Date());
			alipayBillMapper.insertSelective(bill);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params.get("out_trade_no");
	}

}
