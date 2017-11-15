package com.huashi.common.user.service;

import com.huashi.common.user.domain.UserFluxDiscount;

/**
 * 流量设置
 * @author ym
 * @created_at 2016年7月18日下午5:32:56
 */
public interface IUserFluxDiscountService {

	UserFluxDiscount getByUserId(int userId);
}
