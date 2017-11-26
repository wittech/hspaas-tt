package com.huashi.common.third.service;

import java.util.List;
import java.util.Map;

import com.huashi.common.settings.domain.ProvinceLocal;
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
	 * TODO 获取手机号码归属地相关信息
	 * 
	 * @param mobile
	 * @return
	 */
	ProvinceLocal getByMobile(String mobile);

	/**
	 * 
	 * TODO 获取手机号码集合归属地相关信息
	 * 
	 * @param mobiles
	 * @return
	 */
	Map<String, ProvinceLocal> getByMobiles(String[] mobiles);

	/**
	 * 
	 * TODO 加载
	 * 
	 * @return
	 */
	boolean reload();

}
