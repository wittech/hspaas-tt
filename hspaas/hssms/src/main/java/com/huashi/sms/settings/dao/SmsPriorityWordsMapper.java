package com.huashi.sms.settings.dao;

import java.util.List;
import java.util.Map;

import com.huashi.sms.settings.domain.SmsPriorityWords;

public interface SmsPriorityWordsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SmsPriorityWords record);

    int insertSelective(SmsPriorityWords record);

    SmsPriorityWords selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SmsPriorityWords record);

    int updateByPrimaryKey(SmsPriorityWords record);
    
    /**
	 * 
	 * TODO 总条数
	 * 
	 * @param params
	 * @return
	 */
	int findCount(Map<String, Object> params);

	/**
	 * 
	 * TODO 分页
	 * 
	 * @param params
	 * @return
	 */
	List<SmsPriorityWords> findList(Map<String, Object> params);
}