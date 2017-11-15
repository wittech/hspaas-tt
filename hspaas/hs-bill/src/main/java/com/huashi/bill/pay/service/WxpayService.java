package com.huashi.bill.pay.service;


/**
 * 
 * TODO 支付宝服务实现
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月15日 上午12:36:27
 */
//@Service
public abstract class WxpayService implements IWxpayService {

//	@Value("${wxpay.key}")
//	private String key = "";
//
//	// 微信分配的公众号ID（开通公众号之后可以获取到）
//	@Value("${wxpay.appId}")
//	private String appId = "";
//
//	// 微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
//	private String mchId = "";
//
//	// 受理模式下给子商户分配的子商户号
//	private static String subMchID = "";
//
//	// HTTPS证书的本地路径
//	private static String certLocalPath = "";
//
//	// HTTPS证书密码，默认密码等于商户号MCHID
//	private static String certPassword = "";
//
//	// 是否使用异步线程的方式来上报API测速，默认为异步模式
//	private static boolean useThreadToDoReport = true;
//
//	
//
//	/**
//	 * 微信支付统一下单接口
//	 * 
//	 * @param out_trade_no
//	 * @return
//	 * @throws Exception
//	 */
//	public String weixin_pay(String out_trade_no) throws Exception {
//		// 账号信息
//		String appid = appId; // appid
//		// String appsecret = PayConfigUtil.APP_SECRET; // appsecret
//		// 商业号
//		String mch_id = mchId;
//
//		String currTime = PayCommonUtil.getCurrTime();
//		String strTime = currTime.substring(8, currTime.length());
//		String strRandom = PayCommonUtil.buildRandom(4) + "";
//		// 随机字符串
//		String nonce_str = strTime + strRandom;
//		// 价格 注意：价格的单位是分
//		// String order_price = "1";
//		// 商品名称
//		// String body = "企嘉科技商品";
//
//		// 查询订单数据表获取订单信息
//		PayOrder payOrder = payOrderDao.get(PayOrder.class, out_trade_no);
//		// 获取发起电脑 ip
//		String spbill_create_ip = PayConfigUtil.getIP();
//		// 回调接口
//		String notify_url = PayConfigUtil.NOTIFY_URL;
//		String trade_type = "NATIVE";
//		String time_start = new SimpleDateFormat("yyyyMMddHHmmss")
//				.format(new Date());
//		Calendar ca = Calendar.getInstance();
//		ca.setTime(new Date());
//		ca.add(Calendar.DATE, 1);
//		String time_expire = new SimpleDateFormat("yyyyMMddHHmmss").format(ca
//				.getTime());
//		SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
//		packageParams.put("appid", appid);
//		packageParams.put("mch_id", mch_id);
//		packageParams.put("nonce_str", nonce_str);
//		packageParams.put("body", payOrder.getBody());
//		packageParams.put("out_trade_no", out_trade_no);
//		// packageParams.put("total_fee", "1");
//		packageParams.put("total_fee", payOrder.getTotalFee());
//		packageParams.put("spbill_create_ip", spbill_create_ip);
//		packageParams.put("notify_url", notify_url);
//		packageParams.put("trade_type", trade_type);
//		packageParams.put("time_start", time_start);
//		packageParams.put("time_expire", time_expire);
//		String sign = PayCommonUtil.createSign("UTF-8", packageParams, key);
//		packageParams.put("sign", sign);
//
//		String requestXML = PayCommonUtil.getRequestXml(packageParams);
//		System.out.println("请求xml：：：：" + requestXML);
//
//		String resXml = HttpUtil.postData(PayConfigUtil.PAY_API, requestXML);
//		System.out.println("返回xml：：：：" + resXml);
//
//		Map map = XMLUtil.doXMLParse(resXml);
//		// String return_code = (String) map.get("return_code");
//		// String prepay_id = (String) map.get("prepay_id");
//		String urlCode = (String) map.get("code_url");
//		System.out.println("打印调用统一下单接口生成二维码url:::::" + urlCode);
//		return urlCode;
//	}
//
//	private Logger billger = LoggerFactory.getLogger(WxpayService.class);
//
//	@Override
//	public String buildOrder(WxpayModel model) throws WxpayException {
//		Map<String, String> paraTemp = new HashMap<String, String>();
//		try {
//			paraTemp.put("payment_type", PAYMENT_TYPE);
//			paraTemp.put("out_trade_no", model.getTradeNo());
//			paraTemp.put("subject", model.getProduct());
//			paraTemp.put("body", model.getBody());
//			paraTemp.put("total_fee", model.getTotalFee());
//			paraTemp.put("anti_phishing_key", model.getAntiPhishingKey());
//			paraTemp.put("exter_invoke_ip", model.getIp());
//
//			// 增加基本配置
//			paraTemp.put("service", ALIPAY_SERVICE_MODE);
//			paraTemp.put("partner", partner);
//			paraTemp.put("return_url", model.getPageUrl());
//			paraTemp.put("notify_url", model.getNotifyUrl());
//			paraTemp.put("seller_email", sellerEmail);
//			paraTemp.put("_input_charset", inputCharset);
//			return buildForm(paraTemp);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new AlipayException(e);
//		}
//	}
//
//	/**
//	 * 
//	 * TODO 构建表单FORM
//	 * 
//	 * @param sParaTemp
//	 * @return
//	 * @throws Exception
//	 */
//	public String buildForm(Map<String, String> sParaTemp) throws Exception {
//		StringBuffer sbHtml = new StringBuffer();
//		try {
//			// 待请求参数数组
//			Map<String, String> sPara = buildRequestPara(sParaTemp);
//			List<String> keys = new ArrayList<String>(sPara.keySet());
//			sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\""
//					+ alipayGatewayNew
//					+ "_input_charset="
//					+ inputCharset
//					+ "\" method=\"" + ACTION_METHOD + "\">");
//			for (int i = 0; i < keys.size(); i++) {
//				String name = (String) keys.get(i);
//				String value = (String) sPara.get(name);
//				sbHtml.append("<input type=\"hidden\" name=\"" + name
//						+ "\" value=\"" + value + "\"/>");
//			}
//			// submit按钮控件请不要含有name属性
//			sbHtml.append("<input type=\"submit\" value=\"" + PAGE_BUTTON_TEXT
//					+ "\" style=\"display:none;\"></form>");
//			sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return sbHtml.toString();
//	}
//
//	/**
//	 * 生成要请求给支付宝的参数数组
//	 * 
//	 * @param sParaTemp
//	 *            请求前的参数数组
//	 * @return 要请求的参数数组
//	 */
//	private Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
//		// 除去数组中的空值和签名参数
//		Map<String, String> sPara = paraFilter(sParaTemp);
//		try {
//			// 生成签名结果
//			String mysign = buildMysign(sPara);
//			// 签名结果与签名方式加入请求提交参数组中
//			sPara.put("sign", mysign);
//			sPara.put("sign_type", signType);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return sPara;
//	}
//
//	/**
//	 * 根据反馈回来的信息，生成签名结果
//	 * 
//	 * @param Params
//	 *            通知返回来的参数数组
//	 * @return 生成的签名结果
//	 */
//	private String getMysign(Map<String, String> Params) {
//		Map<String, String> sParaNew = this.paraFilter(Params);// 过滤空值、sign与sign_type参数
//		String mysign = this.buildMysign(sParaNew);// 获得签名结果
//		return mysign;
//	}
//
//	/**
//	 * 获取远程服务器ATN结果,验证返回URL
//	 * 
//	 * @param notify_id
//	 *            通知校验ID
//	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
//	 *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
//	 */
//	private String verifyResponse(String notify_id) {
//		// 获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
//		StringBuilder url = new StringBuilder();
//		url.append(verifyUrl).append("partner=").append(partner)
//				.append("&notify_id=").append(notify_id);
//		return checkUrl(url.toString());
//	}
//
//	/**
//	 * 获取远程服务器ATN结果
//	 * 
//	 * @param urlvalue
//	 *            指定URL路径地址
//	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
//	 *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
//	 */
//	private static String checkUrl(String urlvalue) {
//		String inputLine = "";
//		try {
//			URL url = new URL(urlvalue);
//			HttpURLConnection urlConnection = (HttpURLConnection) url
//					.openConnection();
//			BufferedReader in = new BufferedReader(new InputStreamReader(
//					urlConnection.getInputStream()));
//			inputLine = in.readLine().toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//			inputLine = "";
//		}
//		return inputLine;
//	}
//
//	@Override
//	public boolean isPaySuccess(Map<String, String[]> paramMap,
//			boolean isTranscoding) throws AlipayException {
//		Map<String, String> params = ParameterParseUtil.parse(paramMap,
//				isTranscoding);
//		String mysign = getMysign(params);
//		String responseTxt = "true";
//		if (params.get("notify_id") != null) {
//			responseTxt = verifyResponse(params.get("notify_id"));
//		}
//		String sign = "";
//		if (params.get("sign") != null) {
//			sign = params.get("sign");
//		}
//
//		// 写日志记录（若要调试，请取消下面两行注释）
//		// String sWord = "responseTxt=" + responseTxt +
//		// "\n notify_url_bill:sign=" + sign + "&mysign="
//		// + mysign + "\n 返回参数：" + AlipayCore.createLinkString(params);
//		// AlipayCore.billResult(sWord);
//
//		// 验证
//		// responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
//		// mysign与sign不等，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
//		if (!mysign.equals(sign) || !responseTxt.equals("true"))
//			return false;
//
//		// 获取交易状态
//		String tradeStatus = params.get("trade_status");
//		if (tradeStatus.equals("TRADE_FINISHED")
//				|| tradeStatus.equals("TRADE_SUCCESS"))
//			return true;
//
//		if (!isTranscoding) {
//			billger.warn("订单号交易失败：{} ", JSON.toJSONString(params));
//			return true;
//		}
//
//		return false;
//	}
//
//	/**
//	 * 除去数组中的空值和签名参数
//	 * 
//	 * @param sArray
//	 *            签名参数组
//	 * @return 去掉空值与签名参数后的新签名参数组
//	 */
//	private Map<String, String> paraFilter(Map<String, String> sArray) {
//		Map<String, String> result = new HashMap<String, String>();
//		try {
//			if (sArray == null || sArray.size() <= 0) {
//				return result;
//			}
//			for (String key : sArray.keySet()) {
//				String value = sArray.get(key);
//				if (value == null || value.equals("")
//						|| key.equalsIgnoreCase("sign")
//						|| key.equalsIgnoreCase("sign_type")) {
//					continue;
//				}
//				result.put(key, value);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
//
//	private String buildMysign(Map<String, String> array) {
//		String mysign = "";
//		try {
//			String prestr = createLinkString(array); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
//			prestr = prestr + key; // 把拼接后的字符串再与安全校验码直接连接起来
//			mysign = this.md5(prestr);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return mysign;
//	}
//
//	/**
//	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
//	 * 
//	 * @param params
//	 *            需要排序并参与字符拼接的参数组
//	 * @return 拼接后字符串
//	 */
//	private String createLinkString(Map<String, String> params) {
//		List<String> keys = new ArrayList<String>(params.keySet());
//		Collections.sort(keys);
//		String prestr = "";
//		for (int i = 0; i < keys.size(); i++) {
//			String key = keys.get(i);
//			String value = params.get(key);
//
//			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
//				prestr = prestr + key + "=" + value;
//			} else {
//				prestr = prestr + key + "=" + value + "&";
//			}
//		}
//		return prestr;
//	}
//
//	private byte[] getContentBytes(String content, String charset) {
//		if (charset == null || "".equals(charset)) {
//			return content.getBytes();
//		}
//		try {
//			return content.getBytes(charset);
//		} catch (UnsupportedEncodingException e) {
//			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:"
//					+ charset);
//		}
//	}
//
//	public String md5(String text) throws Exception {
//		return DigestUtils.md5Hex(getContentBytes(text, this.inputCharset));
//	}
//
//	/**
//	 * 微信支付回调方法
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void weixin_notify(HttpServletRequest request,
//			HttpServletResponse response) throws Exception {
//
//		// 读取参数
//		InputStream inputStream;
//		StringBuffer sb = new StringBuffer();
//		inputStream = request.getInputStream();
//		String s;
//		BufferedReader in = new BufferedReader(new InputStreamReader(
//				inputStream, "UTF-8"));
//		while ((s = in.readLine()) != null) {
//			sb.append(s);
//		}
//		in.close();
//		inputStream.close();
//
//		// 解析xml成map
//		Map<String, String> m = new HashMap<String, String>();
//		m = XMLUtil.doXMLParse(sb.toString());
//
//		// 过滤空 设置 TreeMap
//		SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
//		Iterator it = m.keySet().iterator();
//		while (it.hasNext()) {
//			String parameter = (String) it.next();
//			String parameterValue = m.get(parameter);
//
//			String v = "";
//			if (null != parameterValue) {
//				v = parameterValue.trim();
//			}
//			packageParams.put(parameter, v);
//		}
//
//		// 账号信息
//		String key = PayConfigUtil.getKey(); // key
//		String out_trade_no = (String) packageParams.get("out_trade_no");
//		// logger.info(packageParams);
//		// 判断签名是否正确
//		if (PayCommonUtil.isTenpaySign("UTF-8", packageParams, key)) {
//			// ------------------------------
//			// 处理业务开始
//			// ------------------------------
//			String resXml = "";
//			if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
//				// 这里是支付成功
//				// ////////执行自己的业务逻辑////////////////
//				String mch_id = (String) packageParams.get("mch_id");
//				String openid = (String) packageParams.get("openid");
//				String is_subscribe = (String) packageParams
//						.get("is_subscribe");
//
//				String bank_type = (String) packageParams.get("bank_type");
//				String total_fee = (String) packageParams.get("total_fee");
//				String transaction_id = (String) packageParams
//						.get("transaction_id");
//
//				System.out.println("mch_id:" + mch_id);
//				System.out.println("openid:" + openid);
//				System.out.println("is_subscribe:" + is_subscribe);
//				System.out.println("out_trade_no:" + out_trade_no);
//				System.out.println("total_fee:" + total_fee);
//				System.out.println("bank_type:" + bank_type);
//				System.out.println("transaction_id:" + transaction_id);
//
//				// 成功回调后需要处理预生成订单的状态和一些支付信息
//				
//				 //查询数据库中订单，首先判定订单中金额与返回的金额是否相等，不等金额被纂改
//				
//				//判定订单是否已经被支付，不可重复支付
//				
//				//正常处理相关业务逻辑
//				
//				
//			} else {
//				System.out.println("支付失败,错误信息："
//						+ packageParams.get("err_code")
//						+ "-----订单号：：："
//						+ out_trade_no
//						+ "*******支付失败时间：：：："
//						+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//								.format(new Date()));
//
//				String err_code = (String) packageParams.get("err_code");
//
//				resXml = "<xml>"
//						+ "<return_code><![CDATA[FAIL]]></return_code>"
//						+ "<return_msg><![CDATA[报文为空]]></return_msg>"
//						+ "</xml> ";
//
//			}
//			// ------------------------------
//			// 处理业务完毕
//			// ------------------------------
//			BufferedOutputStream out = new BufferedOutputStream(
//					response.getOutputStream());
//			out.write(resXml.getBytes());
//			out.flush();
//			out.close();
//		} else {
//			System.out.println("通知签名验证失败---时间::::"
//					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//							.format(new Date()));
//		}
//
//	}
//
//	@Override
//	public boolean notifyUpdateOrder(Map<String, String[]> paramMap)
//			throws AlipayException {
//
//		String tradeNo = saveAlipayBill(ParameterParseUtil.parse(paramMap,
//				false));
//
//		if (!isPaySuccess(paramMap, false))
//			return false;
//
//		if (StringUtils.isEmpty(tradeNo))
//			return false;
//
//		// 更新订单
//		return tradeOrderService.updateOrderPayCompleted(tradeNo);
//	}
//
//	/**
//	 * 
//	 * TODO 插入支付宝订单对账记录
//	 * 
//	 * @param params
//	 */
//	private String saveAlipayBill(Map<String, String> params) {
//		try {
//			AlipayBill bill = new AlipayBill();
//			bill.setOutTradeNo(params.get("out_trade_no"));
//			bill.setSubject(params.get("subject"));
//			bill.setPaymentType(params.get("payment_type"));
//			bill.setTradeNo(params.get("trade_no"));
//			bill.setTradeStatus(params.get("trade_status"));
//			bill.setTotalFee(StringUtils.isEmpty(params.get("total_fee")) ? null
//					: Double.parseDouble(params.get("total_fee")));
//			bill.setNotifyId(params.get("notify_id"));
//			bill.setNotifyTime(params.get("notify_time"));
//			bill.setNotifyType(params.get("notify_type"));
//			bill.setSellerEmail(params.get("seller_email"));
//			bill.setBuyerEmail(params.get("buyer_email"));
//			bill.setSellerId(params.get("seller_id"));
//			bill.setBuyerId(params.get("buyer_id"));
//			bill.setIsSuccess(Boolean.parseBoolean(params.get("is_success")));
//			alipayBillMapper.insertSelective(bill);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return params.get("out_trade_no");
//	}

}
