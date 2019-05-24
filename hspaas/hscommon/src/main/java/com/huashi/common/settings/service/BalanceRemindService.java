/**
 * 
 */
package com.huashi.common.settings.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.settings.dao.BalanceRemindMapper;
import com.huashi.common.settings.domain.BalanceRemind;

/**
 * 余额提醒
 * @author Administrator
 *
 */
@Service
public class BalanceRemindService implements IBalanceRemindService{
	@Autowired
	private BalanceRemindMapper balanceRemindMapper;

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public boolean update(BalanceRemind balanceRemind) {
		balanceRemind.setModifyTime(new Date());
		return balanceRemindMapper.updateByPrimaryKey(balanceRemind) > 0;
	}

	@Override
	public List<BalanceRemind> selectByUserId(int userId) {
		return balanceRemindMapper.selectByUserId(userId);
	}
	
	/**
	 * 根据用户id获取对象信息
	 * @param userId
	 * @return
	 */
	@Override
	public BalanceRemind getByUserId(int userId){
		return balanceRemindMapper.getByUserId(userId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Integer insert(BalanceRemind balanceRemind) {
		balanceRemind.setCreateTime(new Date());
		return balanceRemindMapper.insert(balanceRemind);
	}
}
