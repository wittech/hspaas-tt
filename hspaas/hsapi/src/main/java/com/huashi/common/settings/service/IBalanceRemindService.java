/**
 * 
 */
package com.huashi.common.settings.service;

import java.util.List;

import com.huashi.common.settings.domain.BalanceRemind;

/**
 * TODO 余额提醒
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年1月27日 下午10:14:40
 */
public interface IBalanceRemindService {
	/**
	 * 
	 * TODO 保存余额提醒
	 * 
	 * @param balanceRemind
	 * @return
	 */
	boolean update(BalanceRemind balanceRemind);
	
	/**
	 * 保存
	 * @param balanceRemind
	 * @return
	 */
	Integer insert(BalanceRemind balanceRemind);

	/**
	 * 根据用户id
	 * 
	 * @param userId
	 * @return
	 */
	List<BalanceRemind> selectByUserId(int userId);
	
	/**
	 * 根据用户id获取对象信息
	 * @param userId
	 * @return
	 */
	BalanceRemind getByUserId(int userId);
}