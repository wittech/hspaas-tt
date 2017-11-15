package com.huashi.common.settings.dao;

import java.util.List;
import java.util.Map;

import com.huashi.common.settings.domain.AddressBook;

public interface AddressBookMapper {
	int deleteByPrimaryKey(int id);

	int insert(AddressBook record);

	int insertSelective(AddressBook record);

	AddressBook selectByPrimaryKey(int id);

	int updateByPrimaryKeySelective(AddressBook record);

	int updateByPrimaryKey(AddressBook record);

	/**
	 * 
	 * TODO 更新默认地址
	 * 
	 * @return
	 */
	int updateStatus(int id);

	/**
	 * 
	 * TODO 根据用户id获取全部地址
	 * 
	 * @param params
	 * @return
	 */
	List<AddressBook> findListByUserId(int userId);

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
	List<AddressBook> findPageListByUserId(Map<String, Object> params);

	/**
	 * 
	 * TODO 获取用户默认地址
	 * 
	 * @param userId
	 * @return
	 */
	AddressBook selectByDefaultFlag(int userId);
}