/**
 * 
 */
package com.huashi.fs.product.service;

import java.util.List;
import java.util.Map;

import com.huashi.fs.product.domain.FluxProduct;

/**
 * 
 * TODO 流量产品服务
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月18日 下午11:06:01
 */
public interface IFluxProductService {

	/**
	 * 根据手机号码获取归属地，根据归属地获取当前全部套餐
	 * 
	 * @param mobile
	 * @return
	 */
	Map<String, Object> findListAll(String mobile);

	/**
	 * 
	 * TODO 根据用户手机号码获取套餐信息
	 * 
	 * @param mobile
	 * @return
	 */
	Map<String, Object> findListByMobile(String mobile);

	/**
	 * 
	 * TODO 根据面值和运营商获取流量产品信息
	 * 
	 * @param parValue
	 *            流量面值，如500(500M)，1024(1G)
	 * @param cmcp
	 *            运营商
	 * @return
	 */
	List<FluxProduct> findByPackage(String parValue, int cmcp);

}
