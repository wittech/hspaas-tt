package com.huashi.mms.passage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.mms.passage.domain.MmsPassageGroupDetail;

public interface MmsPassageGroupDetailMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(MmsPassageGroupDetail record);

    int insertSelective(MmsPassageGroupDetail record);

    MmsPassageGroupDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmsPassageGroupDetail record);

    int updateByPrimaryKey(MmsPassageGroupDetail record);

    /**
     * TODO 根据通道组ID删除通道组及通道关联信息
     * 
     * @param groupId
     * @return
     */
    int deleteByGroupId(int groupId);

    /**
     * TODO 根据通道组ID查询所有通道详细信息
     * 
     * @param groupId
     * @return
     */
    List<MmsPassageGroupDetail> findPassageByGroupId(int groupId);

    /**
     * TODO 根据passageId查询所有通道组Id包含该通道的集合
     * 
     * @param passageId
     * @return
     */
    List<Integer> getGroupIdByPassageId(@Param("passageId") int passageId);

    /**
     * 根据通道组ID更新 该通道组下的通道ID
     * 
     * @param groupId
     * @param passageId
     * @return
     */
    int updateGroupPassageId(@Param("groupId") int groupId, @Param("passageId") int passageId);
}
