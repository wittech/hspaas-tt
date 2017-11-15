package com.huashi.common.notice.dao;

import com.huashi.common.notice.domain.EmailSendRecord;

public interface EmailSendRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(EmailSendRecord record);

    int insertSelective(EmailSendRecord record);

    EmailSendRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(EmailSendRecord record);

    int updateByPrimaryKey(EmailSendRecord record);
}