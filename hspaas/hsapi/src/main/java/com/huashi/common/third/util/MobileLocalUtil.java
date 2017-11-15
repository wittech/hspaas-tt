package com.huashi.common.third.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.huashi.common.third.model.MobileLocation;

public class MobileLocalUtil {
	
	private static final String MOBILE_LOCAL_URL = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm";
	static final String MOBILE_LOCAL_URL_SP2 = "http://virtual.paipai.com/extinfo/GetMobileProductInfo?mobile=15868193450&amount=10000&callname=getPhoneNumInfoExtCallback";// 备用URL
	//地市+运营商
	public static final String CARRIER = "carrier";
	//中国+运营商 用于获取流量产品数据表进行中文转换
	public static final String CATNAME="catName";
	private static Logger logger = LoggerFactory.getLogger(MobileLocalUtil.class);
	
	public static MobileLocation local(String mobile){
		String respnoseResult = "";
		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(MOBILE_LOCAL_URL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.connect();

			StringBuilder sendParam = new StringBuilder();
			sendParam.append("tel=").append(mobile);

			// POST方法时使用
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			out.writeBytes(sendParam.toString());
			out.flush();
			out.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "GBK"));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			reader.close();
			respnoseResult = buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		if(logger.isDebugEnabled()) {
			logger.debug("mobile : {}, local : {}", mobile, respnoseResult);
		}
		return parse(respnoseResult);
	}
	
	/**
	 * 
	   * TODO 获取手机号码的归属详细，如 浙江移动，上海联通
	   * @param mobile
	   * @return
	 */
	public static String carrier(String mobile){
		MobileLocation location = local(mobile);
		if(location == null)
			return "";
		return location.getCarrier();
	}
	
	/**
	 * 根据手机号码获取 归属地 例如 中国移动、联通、电信
	 * @param mobile
	 * @return
	 */
	public static String catName(String mobile){
		MobileLocation location = local(mobile);
		if(location == null)
			return "";
		return location.getCatName();
	}
	
	public static MobileLocation parse(String respnoseResult){
		if(StringUtils.isEmpty(respnoseResult))
			return null;
		respnoseResult = respnoseResult.split("=")[1];
		if(StringUtils.isEmpty(respnoseResult))
			return null;
		
		return JSON.parseObject(respnoseResult, MobileLocation.class);
	}
	
	public static void main(String[] args) {
		System.out.println(carrier("18633513285"));
		//__GetZoneResult_ = {    mts:'1586819',    province:'浙江',    catName:'中国移动',    telString:'15868193450',	areaVid:'30510',	ispVid:'3236139',	carrier:'浙江移动'}
		
		System.out.println(local("18633513285"));
	}
}
