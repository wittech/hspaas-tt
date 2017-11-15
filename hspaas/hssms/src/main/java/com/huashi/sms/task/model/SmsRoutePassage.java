package com.huashi.sms.task.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.alibaba.druid.util.StringUtils;
import com.huashi.common.third.model.MobileCatagory;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.sms.passage.domain.SmsPassageAccess;

/**
 * 
 * TODO 用户路由通道信息
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年10月11日 上午11:57:29
 */
public class SmsRoutePassage implements Serializable {

	private static final long serialVersionUID = 7468557377818340963L;
	private Integer userId;

	// 移动通道信息
	private Map<Integer, SmsPassageAccess> cmPassage = new HashMap<>();
	// 联通通道信息
	private Map<Integer, SmsPassageAccess> cuPassage = new HashMap<>();
	// 电信通道信息
	private Map<Integer, SmsPassageAccess> ctPassage = new HashMap<>();
	// 通道手机号码信息（手机号码以逗号分割）
	private Map<Integer, String> passageMobiles = new HashMap<>();

	private String cmErrorMessage;
	private String cuErrorMessage;
	private String ctErrorMessage;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Map<Integer, SmsPassageAccess> getCmPassage() {
		return cmPassage;
	}

	public void setCmPassage(Map<Integer, SmsPassageAccess> cmPassage) {
		this.cmPassage = cmPassage;
	}

	public Map<Integer, SmsPassageAccess> getCuPassage() {
		return cuPassage;
	}

	public void setCuPassage(Map<Integer, SmsPassageAccess> cuPassage) {
		this.cuPassage = cuPassage;
	}

	public Map<Integer, SmsPassageAccess> getCtPassage() {
		return ctPassage;
	}

	public void setCtPassage(Map<Integer, SmsPassageAccess> ctPassage) {
		this.ctPassage = ctPassage;
	}

	public String getCmErrorMessage() {
		return cmErrorMessage;
	}

	public void setCmErrorMessage(String cmErrorMessage) {
		this.cmErrorMessage = cmErrorMessage;
	}

	public String getCuErrorMessage() {
		return cuErrorMessage;
	}

	public void setCuErrorMessage(String cuErrorMessage) {
		this.cuErrorMessage = cuErrorMessage;
	}

	public String getCtErrorMessage() {
		return ctErrorMessage;
	}

	public void setCtErrorMessage(String ctErrorMessage) {
		this.ctErrorMessage = ctErrorMessage;
	}

	public Map<Integer, String> getPassageMobiles() {
		return passageMobiles;
	}

	public void setPassageMobiles(Map<Integer, String> passageMobiles) {
		this.passageMobiles = passageMobiles;
	}

	/**
	 * 
	   * TODO 设置错误信息
	   * @param cmcp
	   * @param message
	 */
	public void setProvincePassage(CMCP cmcp, Integer provinceCode, SmsPassageAccess passage) {
		if(provinceCode == null || passage == null)
			return;
		
		if(CMCP.CHINA_MOBILE == cmcp)
			getCmPassage().put(provinceCode, passage);
		
		else if(CMCP.CHINA_TELECOM == cmcp)
			getCtPassage().put(provinceCode, passage);
		
		if(CMCP.CHINA_UNICOM == cmcp)
			getCuPassage().put(provinceCode, passage);
	}
	
	/**
	 * 
	   * TODO 获取省份通道信息
	   * 
	   * @param cmcp
	   * @return
	 */
	public Map<Integer, SmsPassageAccess> getProvincePassage(CMCP cmcp) {
		if(CMCP.CHINA_MOBILE == cmcp)
			return getCmPassage();
		
		else if(CMCP.CHINA_TELECOM == cmcp)
			return getCtPassage();
		
		if(CMCP.CHINA_UNICOM == cmcp)
			return getCuPassage();
		
		return null;
	}
	
	/**
	 * 
	   * TODO 设置错误信息
	   * 
	   * @param cmcp
	   * @param message
	 */
	public void setErrorMessage(CMCP cmcp, String message) {
		if(StringUtils.isEmpty(message))
			return;
		
		if(CMCP.CHINA_MOBILE == cmcp)
			setCmErrorMessage(message);
		
		else if(CMCP.CHINA_TELECOM == cmcp)
			setCtErrorMessage(message);
		
		if(CMCP.CHINA_UNICOM == cmcp)
			setCuErrorMessage(message);
	}
	
	/**
	 * 
	   * TODO 获取错误信息
	   * 
	   * @param cmcp
	   * @return
	 */
	public String getErrorMessage(CMCP cmcp) {
		if(CMCP.CHINA_MOBILE == cmcp)
			return getCmErrorMessage();
		
		else if(CMCP.CHINA_TELECOM == cmcp)
			return getCtErrorMessage();
		
		if(CMCP.CHINA_UNICOM == cmcp)
			return getCuErrorMessage();
		
		return null;
	}
	
	/**
	 * 
	   * TODO 加入通道ID和手机号码对应关系(即同一个通道下面拥有手机号码集合信息)
	   * 
	   * @param cmcp
	   * 		运营商
	   * @param passageId
	   * @param mobiles
	 */
	public void addPassageMobilesMapping(Integer passageId, String mobiles) {
		if(MapUtils.isEmpty(passageMobiles) || !passageMobiles.containsKey(passageId)) {
			passageMobiles.put(passageId, mobiles);
			return;
		}
		
		// 拼接手机号码
		passageMobiles.put(passageId, passageMobiles.get(passageId) + MobileCatagory.MOBILE_SPLIT_CHARCATOR + mobiles);
	}
}
