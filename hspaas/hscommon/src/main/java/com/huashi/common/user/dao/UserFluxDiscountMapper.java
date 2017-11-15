package com.huashi.common.user.dao;

import com.huashi.common.user.domain.UserFluxDiscount;

public interface UserFluxDiscountMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserFluxDiscount record);

    int insertSelective(UserFluxDiscount record);

    UserFluxDiscount selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserFluxDiscount record);

    int updateByPrimaryKey(UserFluxDiscount record);
    
    UserFluxDiscount selectByUserId(long userId);
    
    int updateByUserId(UserFluxDiscount discount);
}