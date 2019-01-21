package com.huashi.mms.passage.service;

import java.util.List;
import java.util.Map;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.mms.passage.domain.MmsPassageAccess;

/**
 * TODO 彩信可用通道
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月14日 下午3:57:54
 */
public interface IMmsPassageAccessService {

    /**
     * TODO 查找全部的通道信息
     * 
     * @return
     */
    List<MmsPassageAccess> findPassageAccess();

    /**
     * TODO 根据用户ID获取可用通道集合信息
     * 
     * @param userId
     * @return
     */
    Map<String, MmsPassageAccess> getByUserId(int userId);

    /**
     * TODO 根据条件查询可用通道
     * 
     * @param userId 用户编号
     * @param routeType 路由类型
     * @param cmcp 运营商
     * @param provinceCode 省份代码
     * @return
     */
    MmsPassageAccess get(int userId, int routeType, int cmcp, int provinceCode);

    /**
     * 根据id获取对象信息
     * 
     * @param id
     * @return
     */
    MmsPassageAccess get(int id);

    /**
     * TODO 保存新的通道信息
     * 
     * @param access
     * @return
     */
    boolean save(MmsPassageAccess access);

    /**
     * TODO 重新加载数据
     * 
     * @return
     */
    boolean reload();

    /**
     * TODO 根据通道ID更新数据（通道出错）
     * 
     * @param passageId
     * @return
     */
    boolean updateWhenPassageBroken(int passageId);

    /**
     * TODO 更新用户访问通道
     * 
     * @param access
     * @return
     */
    boolean update(MmsPassageAccess access);

    /**
     * TODO 后台查询全部短信通道访问数据 分页
     * 
     * @param pageNum
     * @param keyword 通道
     * @param userId 用户编码
     * @return
     */
    BossPaginationVo<MmsPassageAccess> findPage(int pageNum, String keyword, int userId);

    /**
     * 当更新用户信息时，如果通道组发生变化，更新access（DB,REDIS及通道参数信息）
     * 
     * @param userId
     * @return
     */
    boolean updateByModifyUser(int userId);

    /**
     * 当更新通道组时，通道组信息发生变化，更新access
     * 
     * @param passageGroupId
     * @return
     */
    boolean updateByModifyPassageGroup(int passageGroupId);

    /**
     * 当更新通道时，通道信息发生变化，更新access Expect: 根据passageId更新Access 思路可改为 1.根据passageId 批量更新 passgaeAccess
     * 2.根据passageId查询所有passageAccess集合并压存到REDIS Current: 1.根据passageId找到通道组信息 2.根据通道组信息查询用户和通道组对应关系 3.
     * 
     * @param passageId
     * @return
     */
    boolean updateByModifyPassage(int passageId);

    /**
     * TODO 查询带轮训通道
     * 
     * @return
     */
    List<MmsPassageAccess> findWaitPulling(PassageCallType callType);

    /**
     * TODO 查询通道余额信息
     * 
     * @return
     */
    List<MmsPassageAccess> findPassageBalace();

    /**
     * TODO 根据通道代码获取参数详细信息（主要针对回执报告和上行信息）
     * 
     * @param passageCode 通道代码（当通道调用类型为 状态回执推送 或 上行推送时，passage_url 值为 通道代码[唯一]）
     * @param callType 通道调用类型，本例主要用于[状态回执推送,上行推送]
     * @return
     */
    MmsPassageAccess getByType(PassageCallType callType, String passageCode);

    /**
     * TODO 根据通道ID删除可用通道
     * 
     * @param passageId
     * @return
     */
    boolean deletePassageAccess(Integer passageId);

    /**
     * TODO 更新可用通道状态
     * 
     * @param passageId
     * @param status
     * @return
     */
    boolean updateAccessStatus(Integer passageId, Integer status);

    /**
     * TODO 获取REDIS通道组合KEY（用于REDIS用户通道组合使用）
     * 
     * @param routeType 路由类型
     * @param cmcp 运营商
     * @param provinceCode 省份代码
     * @return
     */
    String getAssistKey(Integer routeType, Integer cmcp, Integer provinceCode);

}
