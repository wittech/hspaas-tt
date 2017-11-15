/**************************************************************************
 * Copyright (c) 2015-2016 HangZhou Huashi, Inc.
 * All rights reserved.
 * 
 * 项目名称：华时短信平台
 * 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
package com.huashi.sms.settings.service;

import java.util.List;
import java.util.Set;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.settings.domain.ForbiddenWords;

/**
 * 
 * TODO 敏感词服务
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年9月28日 下午11:56:39
 */
public interface IForbiddenWordsService {
	
	/**
	 * 
	 * TODO 短信内容是否包含敏感字
	 * 
	 * @param content
	 * 
	 * @return
	 */
	boolean isContainsForbiddenWords(String content);

	/**
	 * 
	 * TODO 短信内容是否包含敏感字
	 * 
	 * @param content
	 * 		短信内容
	 * @param safeWords
	 * 		报备白名单词汇
	 * 
	 * @return
	 */
	boolean isContainsForbiddenWords(String content, Set<String> safeWords);

	/**
	 * 
	 * TODO 根据内容查找具体的敏感词汇
	 * 
	 * @param content
	 * @return
	 */
	Set<String> filterForbiddenWords(String content);
	
	/**
	 * 
	   * TODO 过滤敏感词（去除报备白名单词汇）
	   * 
	   * @param content
	   * @param safeWords
	   * @return
	 */
	Set<String> filterForbiddenWords(String content, Set<String> safeWords);

	/**
	 * 
	 * TODO 获取所有的敏感词库
	 * 
	 * @return
	 */
	List<String> findForbiddenWordsLibrary();
	
	/**
	 * 
	   * TODO 新增敏感词汇
	   * 
	   * @param words
	   * @return
	 */
	boolean saveForbiddenWords(ForbiddenWords words);
	
	/**
	 * 
	   * TODO 修改敏感词汇
	   * 
	   * @param words
	   * @return
	 */
	boolean update(ForbiddenWords words);
	
	/**
	 * 
	   * TODO 重新加载数据库中的敏感词至REDIS中
	   * 
	   * @return
	 */
	boolean reloadRedisForbiddenWords();

	/**
	 * 
	   * TODO 分页查询敏感词信息（支撑平台）
	   * 
	   * @param pageNum
	   * @param keyword
	   * @return
	 */
	BossPaginationVo<ForbiddenWords> findPage(int pageNum, String keyword);

	/**
	 * 
	   * TODO 根据ID删除敏感词信息
	   * @param id
	   * @return
	 */
	boolean deleteWord(int id);
	
	/**
	 * 
	   * TODO 短信敏感词是否允许通过（敏感词开关）
	   * @return
	 */
	boolean isForbiddenWordsAllowPassed();
	
	/**
	 * 
	   * TODO 获取所有的敏感词标签库
	   * @return
	 */
	String[] findWordsLabelLibrary();
	
	/**
	 * 
	   * TODO 根据敏感词查询归属的标签名称（多个词汇可能存在多种标签名称）
	   * 
	   * @param words
	   * @return
	 */
	List<ForbiddenWords> getLabelByWords(String words);
	
	/**
	 * 
	   * TODO 根据ID查询记录
	   * 
	   * @param id
	   * @return
	 */
	ForbiddenWords get(int id);
}
