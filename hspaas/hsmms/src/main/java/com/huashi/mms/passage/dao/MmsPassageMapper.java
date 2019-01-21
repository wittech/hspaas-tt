package com.huashi.mms.passage.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.mms.passage.domain.MmsPassage;

public interface MmsPassageMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(MmsPassage record);

    int insertSelective(MmsPassage record);

    MmsPassage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmsPassage record);

    int updateByPrimaryKey(MmsPassage record);

    List<MmsPassage> findList(Map<String, Object> params);

    int findCount(Map<String, Object> params);

    List<MmsPassage> findAll();

    List<MmsPassage> selectByGroupId(int groupId);

    List<MmsPassage> getByCmcp(int cmcp);

    /**
     * 根据运营商、路由类型、状态查询全部可用通道组下面的通道
     *
     * @param groupId 通道组id
     * @param cmcp 运营商
     * @param routeType 路由类型
     * @param status 状态
     * @return
     */
    List<MmsPassage> selectAvaiablePassages(@Param("groupId") int groupId, @Param("cmcp") int cmcp,
                                            @Param("routeType") int routeType, @Param("status") int status);

    /**
     * TODO 根据运营商代码查询通道（包含全网）
     * 
     * @param cmcp
     * @return
     */
    List<MmsPassage> findByCmcpOrAll(int cmcp);

    /**
     * TODO 根据省份代码和运营商查询通道信息
     * 
     * @param provinceCode
     * @param cmcp
     * @return
     */
    List<MmsPassage> getByProvinceAndCmcp(@Param("provinceCode") Integer provinceCode, @Param("cmcp") int cmcp);

    /**
     * TODO 根据通道代码查询通道信息
     * 
     * @param code
     * @return
     */
    MmsPassage getPassageByCode(@Param("code") String code);

    /**
     * TODO 查询所有可用通道代码（唯一）
     * 
     * @return
     */
    List<String> selectAvaiableCodes();
}
