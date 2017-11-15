package com.huashi.common.notice.dao;

import com.huashi.common.notice.domain.EmailVerify;

public interface EmailVerifyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(EmailVerify record);

    int insertSelective(EmailVerify record);

    EmailVerify selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(EmailVerify record);

    int updateByPrimaryKey(EmailVerify record);
    
    /**
	 * 
	 * TODO 根据唯一符查询邮箱验证信息
	 * 
	 * @param uid
	 * @return
	 */
	EmailVerify selectByUid(String uid);

	/**
	 * 
	 * TODO 更新激活信息
	 * 
	 * @param uid
	 * @return
	 */
	int updateByUid(String uid);
}