package com.huashi.sms.passage.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.passage.domain.SmsPassageAccess;

public interface SmsPassageAccessMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(SmsPassageAccess record);

	int insertSelective(SmsPassageAccess record);

	SmsPassageAccess selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(SmsPassageAccess record);

	int updateByPrimaryKey(SmsPassageAccess record);

	/**
	 * 
	 * TODO 查询全部可用
	 * 
	 * @return
	 */
	List<SmsPassageAccess> selectAll();

	/**
	 * 
	 * TODO 查询所有带平台轮训抓取数据通道信息
	 * 
	 * @return
	 */
	List<SmsPassageAccess> selectWaitPulling(@Param("callType") int callType);

	/**
	 * 
	 * TODO 根据类型获取可用通道信息
	 * 
	 * @param type
	 * @return
	 */
	List<SmsPassageAccess> selectByType(@Param("type") int type);

	/**
	 * 查询全部短信通道访问数据
	 * 
	 * @param params
	 * @return
	 */
	List<SmsPassageAccess> findList(Map<String, Object> params);

	/**
	 * 查询短信通道访问数据总数 （分页）
	 * 
	 * @param params
	 * @return
	 */
	int count(Map<String, Object> params);

	/**
	 * 
	 * TODO 根据用户ID和运营商查询
	 * 
	 * @param userId
	 * @param cmcp
	 * @return
	 */
	SmsPassageAccess selectByUserIdAndCmcp(@Param("userId") int userId, @Param("cmcp") int cmcp);

	/**
	 * 
	 * TODO 根据用户ID、路由类型和运营商查询
	 * 
	 * @param userId
	 * @param cmcp
	 * @return
	 */
	SmsPassageAccess selectByUserIdAndRouteCmcp(@Param("userId") int userId, @Param("route") int route, @Param("provinceCode") int provinceCode,
			@Param("cmcp") int cmcp);

	/**
	 * 
	   * TODO 根据用户ID删除
	   * @param userId
	   * @return
	 */
	int deleteByUserId(@Param("userId") int userId);

	/**
	 * 
	 * TODO 根据通道代码和调用类型获取通道参数信息
	 * 
	 * @param callType
	 * @param url
	 * @return
	 */
	SmsPassageAccess getByTypeAndUrl(@Param("callType") int callType, @Param("url") String url);
	
	/**
	 * 
	   * TODO 根据通道ID查询可用通道信息
	   * 
	   * @param passageId
	   * @return
	 */
	List<SmsPassageAccess> selectByPassageId(@Param("passageId") int passageId);
	
	/**
	 * 
	   * TODO 根据通道Id删除
	   * @param passageId
	   * @return
	 */
	int deleteByPasageId(@Param("passageId") int passageId);
	
	/**
	 * 
	   * TODO 根据通道ID修改状态
	   * @param passageId
	   * @param status 
	   * @return
	 */
	int updateStatusByPassageId(@Param("passageId") int passageId, @Param("status") int status);
	
}