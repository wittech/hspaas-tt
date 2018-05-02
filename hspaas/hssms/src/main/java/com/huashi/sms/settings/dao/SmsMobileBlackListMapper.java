package com.huashi.sms.settings.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.settings.domain.SmsMobileBlackList;

public interface SmsMobileBlackListMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(SmsMobileBlackList record);

	int insertSelective(SmsMobileBlackList record);

	SmsMobileBlackList selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(SmsMobileBlackList record);

	int updateByPrimaryKey(SmsMobileBlackList record);

	/**
	 * 
	 * TODO 获取手机号码是否存在
	 * 
	 * @param params
	 * @return
	 */
	int selectByMobile(@Param("mobile") String mobile);

	/**
	 * 
	 * TODO 总条数
	 * 
	 * @param params
	 * @return
	 */
	int getCount(Map<String, Object> params);

	/**
	 * 
	 * TODO 分页
	 * 
	 * @param params
	 * @return
	 */
	List<SmsMobileBlackList> findPageList(Map<String, Object> params);

	/**
	 * 
	 * TODO 查找全部的黑名单手机号码
	 * 
	 * @return
	 */
	List<SmsMobileBlackList> selectAllMobiles();

	/**
	 * 
	   * TODO 分页查询列表
	   * 
	   * @param params
	   * @return
	 */
	List<SmsMobileBlackList> findList(Map<String, Object> params);

	int findCount(Map<String, Object> params);

	/**
	 * 
	 * TODO 批量保存
	 * 
	 * @param record
	 * @return
	 */
	int batchInsert(List<SmsMobileBlackList> list);
}