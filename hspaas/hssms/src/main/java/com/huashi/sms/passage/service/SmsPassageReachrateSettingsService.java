package com.huashi.sms.passage.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.passage.dao.SmsPassageReachrateSettingsMapper;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageReachrateSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by youngmeng on 2017/3/11.
 */
@Service
public class SmsPassageReachrateSettingsService implements ISmsPassageReachrateSettingsService {

    private static final Logger LOG = LoggerFactory.getLogger(SmsPassageReachrateSettingsService.class);

    @Autowired
    private SmsPassageReachrateSettingsMapper smsPassageReachrateSettingsMapper;

    @Override
    public BossPaginationVo<SmsPassageReachrateSettings> findPage(Integer pageNum,String keyword) {
        BossPaginationVo<SmsPassageReachrateSettings> page = new BossPaginationVo<SmsPassageReachrateSettings>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("keyword", keyword);
        int count = smsPassageReachrateSettingsMapper.findCount(paramMap);
        page.setCurrentPage(pageNum);
        page.setTotalCount(count);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());
        List<SmsPassageReachrateSettings> list = smsPassageReachrateSettingsMapper.findList(paramMap);
        page.setList(list);
        return page;
    }

    @Override
	public Integer create(SmsPassageReachrateSettings data) {
        try {
            int result = smsPassageReachrateSettingsMapper.insert(data);
            if(result > 0)
            	return data.getId();
        } catch (Exception e){
            LOG.error("添加通道监控轮询异常！",e);
        }
        return -1;
    }

    @Override
    public boolean update(SmsPassageReachrateSettings data) {
        try {
            smsPassageReachrateSettingsMapper.updateByPrimaryKeySelective(data);
            return true;
        } catch (Exception e){
            LOG.error("添加通道监控轮询异常！",e);
        }
        return false;
    }

    @Override
    public SmsPassageReachrateSettings findById(Long id) {
        return smsPassageReachrateSettingsMapper.selectByPrimaryKey(id.intValue());
    }

    @Override
    public List<SmsPassage> getPassageByUseable() {
        return smsPassageReachrateSettingsMapper.getPassageByUseable();
    }

    @Override
    public List<SmsPassageReachrateSettings> getByUseable() {
        return smsPassageReachrateSettingsMapper.getByUseable();
    }
}
