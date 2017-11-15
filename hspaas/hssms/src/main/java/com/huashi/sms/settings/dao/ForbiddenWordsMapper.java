package com.huashi.sms.settings.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.settings.domain.ForbiddenWords;

public interface ForbiddenWordsMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(ForbiddenWords record);

	int insertSelective(ForbiddenWords record);

	ForbiddenWords selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(ForbiddenWords record);

	int updateByPrimaryKey(ForbiddenWords record);

	/**
	 * 
	 * TODO 查询全部的敏感词
	 * 
	 * @return
	 */
	List<String> selectAllWords();

	List<ForbiddenWords> findList(Map<String, Object> params);

	int findCount(Map<String, Object> params);

	/**
	 * 
	 * TODO 根据敏感词汇查询敏感词记录（单个敏感词，走索引）
	 * 
	 * @param words
	 * @return
	 */
	ForbiddenWords selectByWord(@Param("word") String word);

	/**
	 * 
	 * TODO 根据多个敏感词查询敏感词记录（IN不走索引）
	 * 
	 * @param words
	 * @return
	 */
	List<ForbiddenWords> selectByMultiWord(@Param("words") String[] words);
}