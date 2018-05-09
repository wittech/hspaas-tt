// *********************************************************************
// 系统名称：DBRDR
// Copyright(C)2000-2016 NARI Information and Communication Technology
// Branch. All rights reserved.
// 版本信息：DBRDR-V1.000
// #作者：杨猛 $权重：100%#
// 版本 日期 作者 变更记录
// DBRDR-V1.000 2016年8月27日 杨猛　 新建
// *********************************************************************
package com.huashi.common.user.service;

import java.util.List;

import com.huashi.common.user.domain.UserPassage;

/**
 * @author ym
 * @created_at 2016年8月27日下午9:15:30
 */
public interface IUserPassageService {

    /**
     * TODO 初始添加通道
     * 
     * @param userId
     * @param passageList
     * @return
     */
    boolean initUserPassage(int userId, List<UserPassage> passageList);

    /**
     * TODO 批量添加通道
     * 
     * @param userId
     * @param userPassage
     * @return
     */
    boolean save(int userId, UserPassage userPassage);

    /**
     * TODO 保存多个通道信息
     * 
     * @param userId
     * @param userPassages
     * @return
     */
    boolean save(int userId, List<UserPassage> userPassages);

    /**
     * TODO 批量更新用户通道信息
     * 
     * @param userId
     * @param passageList
     * @return
     */
    boolean update(int userId, List<UserPassage> passageList);

    /**
     * 根据用户ID获取用户相关业务类型的通道组ID
     * 
     * @param userId
     * @return
     */
    List<UserPassage> findByUserId(int userId);

    /**
     * TODO 根据用户ID和平台类型获取通道组信息
     * 
     * @param userId
     * @param type
     * @return
     */
    Integer getByUserIdAndType(int userId, int type);

    /**
     * TODO 根据通道组ID获取所有的用户通道信息
     * 
     * @param passageGroupId
     * @return
     */
    List<UserPassage> getPassageGroupListByGroupId(int passageGroupId);

    /**
     * TODO 更新通道组ID
     * 
     * @param userId 用户ID
     * @param type 平台类型
     * @param passageGroupId 通道组ID
     * @return
     */
    boolean update(int userId, int type, int passageGroupId);

    /**
     * TODO 重载用户关系数据到REDIS
     * 
     * @return
     */
    boolean reloadModelToRedis();
}
