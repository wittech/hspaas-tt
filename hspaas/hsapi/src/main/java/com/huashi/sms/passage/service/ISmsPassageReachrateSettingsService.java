package com.huashi.sms.passage.service;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageReachrateSettings;

import java.util.List;

/**
 * Created by youngmeng on 2017/3/11.
 */
public interface ISmsPassageReachrateSettingsService {


    BossPaginationVo<SmsPassageReachrateSettings> findPage(Integer pageNum,String keyword);

    Integer create(SmsPassageReachrateSettings data);

    boolean update(SmsPassageReachrateSettings data);

    SmsPassageReachrateSettings findById(Long id);

    /**
     * 查询可以添加轮询设置的短信通道
     * 将已经在表中存在轮询设置的通道过滤掉
     * @return
     */
    List<SmsPassage> getPassageByUseable();

    List<SmsPassageReachrateSettings> getByUseable();
}
