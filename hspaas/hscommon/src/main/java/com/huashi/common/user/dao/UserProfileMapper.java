package com.huashi.common.user.dao;

import java.util.List;
import java.util.Map;

import com.huashi.common.user.domain.UserProfile;
import org.apache.ibatis.annotations.Param;

public interface UserProfileMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserProfile record);

    int insertSelective(UserProfile record);

    UserProfile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserProfile record);

    int updateByPrimaryKey(UserProfile record);

	List<UserProfile> findAll();
    
    
    /**
	 * 
	 * TODO 根据用户获取用户基础信息
	 * 
	 * @param userId
	 * @return
	 */
	UserProfile selectProfileByUserId(int userId);
	
	List<UserProfile> findList(Map<String, Object> paramMap);
	
	int getCount(Map<String, Object> paramMap);
	
	int updateUserProfileState(@Param("userId") int userId, @Param("flag") String flag);
		
	int updateUserProfileInfo(UserProfile userProfile);
}