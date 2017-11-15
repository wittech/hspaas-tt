package com.huashi.common.settings.service;

import java.util.List;
import java.util.Map;

import com.huashi.common.settings.domain.Province;

/**
 * 
 * TODO 省份服务接口
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2017年1月5日 下午9:48:14
 */
public interface IProvinceService {

	/**
	 * 
	 * TODO 查找所有省份信息
	 * 
	 * @return
	 */
	List<Province> findAvaiable();

	/**
	 * 根据省份代码获取省份信息
	 * 
	 * @param provinceCode
	 *            省份代码
	 * @return
	 */
	Province get(Integer provinceCode);

	/**
	 * 
	 * TODO 加载到Redis
	 * 
	 * @return
	 */
	boolean reloadToRedis();
	
	/**
	 * 
	   * TODO 获取所有省份名称（Map模式）
	   * @return
	 */
	Map<Integer, String> findNamesInMap();
}
