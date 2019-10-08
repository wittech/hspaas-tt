package com.huashi.common.settings.service;

import java.util.List;
import java.util.Map;

import com.huashi.common.settings.context.SettingsContext.SystemConfigType;
import com.huashi.common.settings.domain.SystemConfig;

/**
 * 
  * TODO 请在此处添加注释
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2017年2月17日 下午11:27:13
 */
public interface ISystemConfigService {

	/**
	 * 
	 * TODO 根据类型查询系统配置信息
	 * 
	 * @param type
	 * @return
	 */
	List<SystemConfig> findByType(String type);

	/**
	 * 
	 * TODO 根据类型和子类型查询系统配置信息
	 * 
	 * @param type
	 * @return
	 */
	SystemConfig findByTypeAndKey(String type, String key);

	/**
	 * 
	   * TODO 根据系统参数ID查询具体信息
	   * @param id
	   * @return
	 */
	SystemConfig findById(int id);

	/**
	 * 
	   * TODO 修改系统配置信息
	   * 
	   * @param systemConfig
	   * @return
	 */
	Map<String,Object> update(SystemConfig systemConfig);

	/**
	 * 
	   * TODO 根据ID删除系统配置信息
	   * 
	   * @param id
	   * @return
	 */
    boolean deleteById(int id);

    /**
     * 
       * TODO 根据类型名称获取相关用户ID（如测试通道用户ID，告警通道用户ID）
       * @param type
       * @return
     */
    Integer getUserIdByTypeName(SystemConfigType type);
    
    /**
     * 
       * TODO 获取加入黑名单词库信息
       * @return
     */
    String[] getBlacklistWords();
    
    /**
     * 
       * TODO 获取运营商正则表达式
       * @param cmcp
       * @return
     */
    String getCmcpRegex(Integer cmcp);

	/**
	 * 根据来源标识查询通道ID
	 * @param isSource 是否为源通道ID  源通道/目标通道
	 * @return 通道ID
	 */
	Integer getPassageIdInBlackMobile(boolean isSource);
    
    /**
     * 
       * TODO 将相关参数信息缓存到REDIS（三大运营商正则表达式，黑名单词库）
       * @return
     */
    boolean reloadSettingsToRedis();
}
