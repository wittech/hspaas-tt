package com.huashi.sms.passage.dao;

import com.huashi.sms.passage.domain.SmsPassage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SmsPassageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SmsPassage record);

    SmsPassage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SmsPassage record);

    int updateByPrimaryKey(SmsPassage record);
    
    List<SmsPassage> findList(Map<String, Object> params);

	int findCount(Map<String, Object> params);

	List<SmsPassage> findAll();

	List<SmsPassage> selectByGroupId(int groupId);

	List<SmsPassage> getByCmcp(int cmcp);

	/**
	 * 根据运营商、路由类型、状态查询全部可用通道组下面的通道
	 *
	 * @param groupId
	 *            通道组id
	 * @param cmcp
	 *            运营商
	 * @param routeType
	 *            路由类型
	 * @param status
	 *            状态
	 * @return
	 */
	List<SmsPassage> selectAvaiablePassages(@Param("groupId") int groupId, @Param("cmcp") int cmcp,
			@Param("routeType") int routeType, @Param("status") int status);

	/**
	 * 
	   * TODO 根据运营商代码查询通道（包含全网）
	   * @param cmcp
	   * @return
	 */
	List<SmsPassage> findByCmcpOrAll(int cmcp);

	/**
	 * 
	   * TODO 根据省份代码和运营商查询通道信息
	   * 
	   * @param provinceCode
	   * @param cmcp
	   * @return
	 */
	List<SmsPassage> getByProvinceAndCmcp(@Param("provinceCode") Integer provinceCode, @Param("cmcp") int cmcp);
	
	/**
	 * 
	   * TODO 根据通道代码查询通道信息
	   * 
	   * @param code
	   * @return
	 */
	SmsPassage getPassageByCode(@Param("code") String code);
	
	/**
	 * 
	   * TODO 查询所有可用通道代码（唯一）
	   * @return
	 */
	List<String> selectAvaiableCodes();
	
}