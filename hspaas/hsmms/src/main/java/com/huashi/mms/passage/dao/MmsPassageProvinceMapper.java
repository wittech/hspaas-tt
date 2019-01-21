package com.huashi.mms.passage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.mms.passage.domain.MmsPassageProvince;

public interface MmsPassageProvinceMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MmsPassageProvince record);

    int insertSelective(MmsPassageProvince record);

    MmsPassageProvince selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MmsPassageProvince record);

    int updateByPrimaryKey(MmsPassageProvince record);

    int deleteByPassageId(@Param("passageId") Integer passageId);

    /**
     * TODO 根据通道ID获取省份通道信息
     * 
     * @param passageId
     * @return
     */
    List<MmsPassageProvince> getListByPassageId(@Param("passageId") Integer passageId);

    /**
     * TODO 批量插入信息
     * 
     * @param list
     * @return
     */
    int batchInsert(List<MmsPassageProvince> list);
}
