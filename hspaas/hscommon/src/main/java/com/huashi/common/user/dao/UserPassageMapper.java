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
    
    /**
     * 
       * TODO 查询全部用户ID数据
       * 
       * @return
     */
    List<UserPassage> selectAll();

    /**
     * 
       * TODO 根据用户ID查询通道组信息
       * 
       * @param userId
       * @return
     */
    List<UserPassage> findByUserId(int userId);

    /**
     * 
       * TODO 根据用户ID删除
       * 
       * @param userId
       * @return
     */
    int deleteByUserId(int userId);

    /**
     * TODO 根据用户ID和平台类型获取用户对应的通道组信息
     * 
     * @param userId
     * @param
     * @return
     */
    UserPassage selectByUserIdAndType(@Param("userId") int userId, @Param("type") int type);

    /**
     * TODO 根据用户ID和类型更新通道组ID
     * 
     * @param passageGroupId
     * @param userId
     * @param type
     * @return
     */
    int updateByUserIdAndType(@Param("passageGroupId") int passageGroupId, @Param("userId") int userId,
                              @Param("type") int type);

    /**
     * TODO 根据用户通道组ID查询通道集合
     * 
     * @param passageGroupId
     * @return
     */
    public List<UserPassage> getPassageGroupListByGroupId(@Param("passageGroupId") int passageGroupId);
}
