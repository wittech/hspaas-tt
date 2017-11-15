/**
 * 
 */
package com.huashi.common.settings.service;

import java.util.List;

import com.huashi.common.settings.domain.PushConfig;

/**
 * 推送设置服务接口类
 * 
 * @author Administrator
 *
 */
public interface IPushConfigService {

	/**
	 * 推送设置修改
	 * 
	 * @param pushConfig
	 * @return
	 */
	boolean update(PushConfig record);
	
	/**
	 * 保存
	 * @param record
	 * @return
	 */
	boolean save(PushConfig record);
	
	/**
	 * boss 修改推送用户设置
	 * @param pushConfig
	 * @return
	 */
	int updateByUserId(PushConfig pushConfig);

	/**
	 * 根据用户id 获取推送设置记录
	 * 
	 * @param userId
	 * @return
	 */
	List<PushConfig> findByUserId(int userId);
	
	/**
	 * 
	   * TODO 根据用ID和平台获取推送信息
	   * 
	   * @param userId
	   * @param platformType
	   * @return
	 */
	PushConfig getByUserId(int userId, int callbackUrlType);
	
	/**
	 * 
	   * TODO 根据推送数据的状态和是否用户自定义URL决定最终的推送URL
	   * @param userId
	   * 	用户编号
	   * @param platformType
	   * 	平台类型
	   * @param customUrl
	   * 	用户接口自定义参数传递的URL
	   * @return
	 */
	PushConfig getPushUrl(int userId, int callbackUrlType, String customUrl);
	
	/**
	 * 
	   * TODO 加载推送配置信息到REDIS
	   * 
	   * @return
	 */
	boolean reloadToRedis();
}
