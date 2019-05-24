/**
 * 
 */
package com.huashi.sms.passage.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.passage.context.PassageContext.PassageStatus;
import com.huashi.sms.passage.context.PassageContext.ResultCode;
import com.huashi.sms.passage.dao.SmsPassageControlMapper;
import com.huashi.sms.passage.dao.SmsPassageMapper;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageControl;

/**
 * @author Administrator
 *
 */
@Service
public class SmsPassageControlService implements ISmsPassageControlService {

	@Autowired
	private SmsPassageControlMapper smsPassageControlMapper;
	@Autowired
	private SmsPassageMapper smsPassageMapper;

	@Override
	public BossPaginationVo<SmsPassageControl> findPage(int pageNum, String keyword, String status) {
		BossPaginationVo<SmsPassageControl> page = new BossPaginationVo<SmsPassageControl>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("passageId", keyword);
		paramMap.put("status", status);
		int count = smsPassageControlMapper.count(paramMap);
		page.setCurrentPage(pageNum);
		page.setTotalCount(count);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<SmsPassageControl> list = smsPassageControlMapper.findList(paramMap);
		if (list != null && list.size() > 0) {
			for (SmsPassageControl c : list) {
				SmsPassage smsPassage = smsPassageMapper.selectByPrimaryKey(c.getPassageId());
				if (smsPassage != null) {
					c.setPassageIdText(smsPassage.getName());
				}
			}
		}
		page.setList(list);
		return page;
	}

	@Override
	public int save(SmsPassageControl control) {
		control.setCreateTime(new Date());
		//验证定时任务通道是否有重复
		SmsPassageControl c = smsPassageControlMapper.selectByPassageId(control.getPassageId());
		if(c !=null){
			//判断添加的通道是否是停用状态 如果为停用返回 提示不允许提交
			if(c.getStatus()==PassageStatus.HANGUP.getValue()){
				return ResultCode.THREE.getValue();
			}
			return ResultCode.TWO.getValue();
		}
		return smsPassageControlMapper.insertSelective(control);
	}

	@Override
	public int update(SmsPassageControl control) {
		//验证定时任务通道是否有重复 
		SmsPassageControl c = smsPassageControlMapper.selectByPassageId(control.getPassageId());
		if(c !=null){
			//判断不等于当前记录
			if(!control.getId().equals(c.getId())){
				//判断添加的通道是否是停用状态 如果为停用返回 提示不允许提交
				if(c.getStatus()==PassageStatus.HANGUP.getValue()){
					return ResultCode.THREE.getValue();
				}
				return ResultCode.TWO.getValue();
			}
		}
		return smsPassageControlMapper.updateByPrimaryKeySelective(control);
	}

	@Override
	public boolean deleteById(int id) {
		return smsPassageControlMapper.deleteByPrimaryKey(id) > 0;
	}

	@Override
	public SmsPassageControl get(int id) {
		return smsPassageControlMapper.selectByPrimaryKey(id);
	}

	@Override
	public boolean updateStatus(SmsPassageControl control) {
		return smsPassageControlMapper.updateByPrimaryKeySelective(control)>0;
	}
}
