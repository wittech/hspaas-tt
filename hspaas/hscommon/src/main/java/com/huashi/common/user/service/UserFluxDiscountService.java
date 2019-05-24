package com.huashi.common.user.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.user.dao.UserFluxDiscountMapper;
import com.huashi.common.user.domain.UserFluxDiscount;

@Service
public class UserFluxDiscountService implements IUserFluxDiscountService{

	
	@Autowired
	private UserFluxDiscountMapper userFluxDiscountMapper;
	
	@Override
	public UserFluxDiscount getByUserId(int userId) {
		return userFluxDiscountMapper.selectByUserId(userId);
	}

}
