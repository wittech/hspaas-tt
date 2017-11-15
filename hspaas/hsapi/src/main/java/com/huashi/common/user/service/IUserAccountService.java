package com.huashi.common.user.service;

import com.huashi.common.user.domain.UserAccount;

/**
 * 
 * TODO 用户账户金额
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年9月18日 下午4:16:31
 */
public interface IUserAccountService {

	/**
	 * 
	 * TODO 根据平台类型获取用户相关余额量
	 * 
	 * @param userId
	 * @param type
	 * @return
	 */
	UserAccount getByUserId(int userId);

	/**
	 * 
	 * TODO 根据用户ID获取站内余额(金额)
	 * 
	 * @param userId
	 * @return
	 */
	double getAccountByUserId(int userId);

	/**
	 * 
	 * TODO 更新账户余额
	 * 
	 * @param userId
	 * @param money
	 * @param paySource
	 * @param payType
	 * @return
	 */
	boolean updateAccount(int userId, Double money, int paySource, int payType);
	
	/**
	 * 
	   * TODO 保存账户信息
	   * 
	   * @param userId
	   * @return
	 */
	boolean save(int userId);

	/**
	 * 
	 * TODO 转账
	 * 
	 * @param userId
	 * @param fromUserId
	 * @param money
	 * @return
	 */
	boolean exchange(int userId, int fromUserId, Double money);

}
