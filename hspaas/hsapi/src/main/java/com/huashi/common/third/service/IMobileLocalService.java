package com.huashi.common.third.service;

import java.util.List;

import com.huashi.common.third.model.MobileCatagory;

public interface IMobileLocalService {

	/**
	 * 
	 * TODO 单个或以逗号分隔的多个手机号码分类
	 * 
	 * @param mobile
	 * @return
	 */
	MobileCatagory doCatagory(String mobile);

	/**
	 * 
	 * TODO 批量手机号码分类
	 * 
	 * @param mobiles
	 * @return
	 */
	MobileCatagory doCatagory(List<String> mobiles);
	
	/**
	 * 
	   * TODO 加载
	   * @return
	 */
	boolean reload();
	
}
