package com.huashi.mms.template.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.mms.template.domain.MmsMessageTemplate;

public interface MmsMessageTemplateMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MmsMessageTemplate record);

    int insertSelective(MmsMessageTemplate record);

    MmsMessageTemplate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MmsMessageTemplate record);

    int updateByPrimaryKey(MmsMessageTemplate record);

    int getCountByUserId(Map<String, Object> params);

    List<MmsMessageTemplate> findPageListByUserId(Map<String, Object> params);

    List<MmsMessageTemplate> findList(Map<String, Object> params);

    int findCount(Map<String, Object> params);

    /**
     * TODO 根据用户ID和模板类型（路由）获取可用模板
     * 
     * @param userId
     * @return
     */
    List<MmsMessageTemplate> findAvaiableByUserId(@Param("userId") int userId);

    /**
     * TODO 根据用户ID和模板类型（路由）获取可用模板
     * 
     * @param userId
     * @return
     */
    List<MmsMessageTemplate> findAvaiableByUserIdAndType(@Param("userId") int userId, @Param("type") int type);

    /**
     * TODO 查询全部
     * 
     * @return
     */
    List<MmsMessageTemplate> findAll();

    /**
     * TODO 根据模板ID查询模板详情
     * 
     * @param modelId
     * @return
     */
    MmsMessageTemplate selectByModelId(@Param("modelId") String modelId);
}
