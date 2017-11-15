package com.huashi.common.notice.dao;

import java.util.List;
import java.util.Map;

import com.huashi.common.notice.domain.Notification;

public interface NotificationMapper {
    int deleteByPrimaryKey(int id);

    int insert(Notification record);

    int insertSelective(Notification record);

    Notification selectByPrimaryKey(int id);

    int updateByPrimaryKeySelective(Notification record);

    int updateByPrimaryKeyWithBLOBs(Notification record);

    int updateByPrimaryKey(Notification record);
    
    /**
     * 
       * TODO 查询通告记录
       * @param count
       * @return
     */
    List<Notification> selectByCount(int count);
    
    List<Notification> findList(Map<String, Object> paramMap);
    
    int findCount(Map<String, Object> paramMap);
}