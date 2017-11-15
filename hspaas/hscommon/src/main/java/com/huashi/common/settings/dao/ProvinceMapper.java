package com.huashi.common.settings.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huashi.common.settings.domain.Province;

public interface ProvinceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Province record);

    int insertSelective(Province record);

    Province selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Province record);

    int updateByPrimaryKey(Province record);
    
    /**
     * 
       * TODO 查询全部有效省份
       * @return
     */
    List<Province> selectAllAvaiable();
    
    /**
     * 根据省份代码查询省份信息
     * 
     * @param code
     * @return
     */
    Province selectByCode(@Param("code") Integer code);
}