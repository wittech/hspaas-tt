package com.huashi.common.service.dao;

import com.huashi.common.service.domain.OpinionFeedBack;

public interface OpinionFeedBackMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OpinionFeedBack record);

    int insertSelective(OpinionFeedBack record);

    OpinionFeedBack selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OpinionFeedBack record);

    int updateByPrimaryKeyWithBLOBs(OpinionFeedBack record);

    int updateByPrimaryKey(OpinionFeedBack record);
}