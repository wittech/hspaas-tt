package com.huashi.sms.template.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.template.domain.MessageTemplate;

public interface MessageTemplateMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MessageTemplate record);

    int insertSelective(MessageTemplate record);

    MessageTemplate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MessageTemplate record);

    int updateByPrimaryKey(MessageTemplate record);

    int getCountByUserId(Map<String, Object> params);

    List<MessageTemplate> findPageListByUserId(Map<String, Object> params);

    List<MessageTemplate> findList(Map<String, Object> params);

    int findCount(Map<String, Object> params);

    /**
     * 根据用户ID和模板类型（路由）获取可用模板
     * 
     * @param userId
     * @return
     */
    List<MessageTemplate> findAvaiableByUserId(@Param("userId") int userId);

    /**
     * 根据用户ID和模板类型（路由）获取可用模板
     * 
     * @param userId
     * @return
     */
    List<MessageTemplate> findAvaiableByUserIdAndType(@Param("userId") int userId, @Param("type") int type);

    /**
     * 查询全部
     * 
     * @return
     */
    List<MessageTemplate> findAll();

    /**
     * 批量插入信息
     *
     * @param list
     * @return
     */
    int batchInsert(List<MessageTemplate> list);
}
