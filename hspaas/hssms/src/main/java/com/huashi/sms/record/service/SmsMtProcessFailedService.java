package com.huashi.sms.record.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.record.dao.SmsMtProcessFailedMapper;
import com.huashi.sms.record.domain.SmsMtProcessFailed;

@Service
public class SmsMtProcessFailedService implements ISmsMtProcessFailedService {

	@Autowired
	private SmsMtProcessFailedMapper smsMtProcessFailedMapper;
	@Reference
	private IUserService iUserService;

	@Override
	public boolean save(SmsMtProcessFailed smsMtProcessFailed) {
		smsMtProcessFailed.setCreateTime(new Date());

		return smsMtProcessFailedMapper.insertSelective(smsMtProcessFailed) > 0;
	}

	@Override
	public BossPaginationVo<SmsMtProcessFailed> findPage(int pageNum, String keyword,Long sid) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("keyword", keyword);
		paramMap.put("sid",sid);
		BossPaginationVo<SmsMtProcessFailed> page = new BossPaginationVo<SmsMtProcessFailed>();
		page.setCurrentPage(pageNum);
		int total = smsMtProcessFailedMapper.findCount(paramMap);
		if (total <= 0) {
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<SmsMtProcessFailed> dataList = smsMtProcessFailedMapper.findList(paramMap);
		for(SmsMtProcessFailed record : dataList){
			if(record.getUserId() != null){
				record.setUserModel(iUserService.getByUserId(record.getUserId()));
			}
		}
		page.getList().addAll(dataList);
		return page;
	}

}
