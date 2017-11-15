package com.huashi.common.settings.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.common.settings.domain.HostWhiteList;

public interface HostWhiteListMapper {
    int deleteByPrimaryKey(int id);

    int insert(HostWhiteList record);

    int insertSelective(HostWhiteList record);

    HostWhiteList selectByPrimaryKey(int id);

    int updateByPrimaryKeySelective(HostWhiteList record);

    int updateByPrimaryKey(HostWhiteList record);
    
    /**
	 * 
	 * TODO 保存
	 * 
	 * @param record
	 * @return
	 */
    int batchInsert(HostWhiteList record);

	/**
	 * 
	   * TODO 根据用户ID和IP获取白名单记录
	   * @param userId
	   * @param ip
	   * @return
	 */
	int selectByUserIdAndIp(@Param("userId") int userId,@Param("ip") String ip);

	/**
	 * 
	 * TODO查询用户记录 返回对象
	 * 
	 * @param id
	 * @return
	 */
	HostWhiteList selectActiveById(int id);

	/**
	 * 
	 * TODO 总条数
	 * 
	 * @param params
	 * @return
	 */
	int selectCount(Map<String, Object> params);

	/**
	 * 
	 * TODO 分页
	 * 
	 * @param params
	 * @return
	 */
	List<HostWhiteList> findPageList(Map<String, Object> params);
	
	/**
	 * 
	 * TODO 分页 后台
	 * 
	 * @param params
	 * @return
	 */
	List<HostWhiteList> findPageListBoss(Map<String, Object> params);
	
	List<HostWhiteList> selectAll();
}