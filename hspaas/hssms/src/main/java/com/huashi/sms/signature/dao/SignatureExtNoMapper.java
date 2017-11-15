package com.huashi.sms.signature.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.signature.domain.SignatureExtNo;

public interface SignatureExtNoMapper {
	int deleteByPrimaryKey(Long id);

	int insert(SignatureExtNo record);

	int insertSelective(SignatureExtNo record);

	SignatureExtNo selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(SignatureExtNo record);

	int updateByPrimaryKey(SignatureExtNo record);
	
	List<SignatureExtNo> findList(Map<String, Object> params);

	int findCount(Map<String, Object> params);
	
	/**
	 * 
	   * TODO 查询全部
	   * @return
	 */
	List<SignatureExtNo> findAll();
	
	/**
	 * 
	   * TODO 根据用户ID查询
	   * @param userId
	   * @return
	 */
	List<SignatureExtNo> selectByUserId(@Param("userId") Integer userId);
}