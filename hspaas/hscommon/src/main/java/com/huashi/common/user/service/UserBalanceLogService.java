/**
 * 
 */
package com.huashi.common.user.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.user.dao.UserBalanceLogMapper;
import com.huashi.common.user.dao.UserMapper;
import com.huashi.common.user.domain.UserBalanceLog;
import com.huashi.common.vo.BossPaginationVo;

/**
 * 用户余额日志服务记录
 * 
 * @author Administrator
 *
 */
@Service
public class UserBalanceLogService implements IUserBalanceLogService {

	@Autowired
	private UserBalanceLogMapper userBalanceLogMapper;
	@Autowired
	private UserMapper userMapper;

	@Override
	public BossPaginationVo<UserBalanceLog> findPageBoss(int pageNum, String userId,String platformType) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("platformType", platformType);
		BossPaginationVo<UserBalanceLog> page = new BossPaginationVo<UserBalanceLog>();
		page.setCurrentPage(pageNum);
		int total = userBalanceLogMapper.findCount(paramMap);
		if (total <= 0) {
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<UserBalanceLog> list = userBalanceLogMapper.findListBoss(paramMap);
		for (UserBalanceLog b : list) {
			b.setUserModel(userMapper.selectMappingByUserId(b.getUserId()));
		}
		page.getList().addAll(list);
		return page;
	}

}
