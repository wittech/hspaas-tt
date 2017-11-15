package com.huashi.common.user.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.huashi.common.user.domain.UserPassage;

public interface UserPassageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserPassage record);

    int insertSelective(UserPassage record);

    UserPassage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserPassage record);

    int updateByPrimaryKey(UserPassage record);
    
    List<UserPassage> findByUserId(int userId);
    
    int deleteByUserId(int userId);
    
    /**
     * 
       * TODO 根据用户ID和平台类型获取用户对应的通道组信息
       * 
       * @param userId
       * @param
       * @return
     */
    UserPassage selectByUserIdAndType(@Param("userId") int userId,@Param("type") int type);

    int updateByUserIdAndType(UserPassage passage);

    public List<UserPassage> getPassageGroupListByGroupId(@Param("passageGroupId") int passageGroupId);
}