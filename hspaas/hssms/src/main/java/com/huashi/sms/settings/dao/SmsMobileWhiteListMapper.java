package com.huashi.sms.settings.dao;

import java.util.List;
import java.util.Map;

import com.huashi.sms.settings.domain.SmsMobileWhiteList;
import org.apache.ibatis.annotations.Param;

public interface SmsMobileWhiteListMapper {
	int deleteByPrimaryKey(int id);

	int insert(SmsMobileWhiteList record);

	int insertSelective(SmsMobileWhiteList record);

	SmsMobileWhiteList selectByPrimaryKey(int id);

	int updateByPrimaryKeySelective(SmsMobileWhiteList record);

	int updateByPrimaryKey(SmsMobileWhiteList record);

	/**
	 * 
	 * TODO 批量保存
	 * 
	 * @param record
	 * @return
	 */
	int batchInsert(List<SmsMobileWhiteList> list);

	/**
	 * 
	 * TODO 获取手机号码是否存在
	 * 
	 * @param userId
	 * @return
	 */
	int selectByUserIdAndMobile(@Param("userId") int userId, @Param("mobile") String mobile);

	/**
	 * 
	 * TODO查询用户记录 返回对象
	 * 
	 * @param userId
	 * @return
	 */
	List<SmsMobileWhiteList> selectByUserId(int userId);

	/**
	 * 
	 * TODO 总条数
	 * 
	 * @param params
	 * @return
	 */
	int getCountByUserId(Map<String, Object> params);

	/**
	 * 
	 * TODO 分页
	 * 
	 * @param params
	 * @return
	 */
	List<SmsMobileWhiteList> findPageListByUserId(Map<String, Object> params);

	List<SmsMobileWhiteList> findList(Map<String,Object> params);

	int findCount(Map<String,Object> params);
	
	/**
	 * 
	   * TODO 查询全部手机白名单数据
	   * @return
	 */
	List<SmsMobileWhiteList> selectAll();
}