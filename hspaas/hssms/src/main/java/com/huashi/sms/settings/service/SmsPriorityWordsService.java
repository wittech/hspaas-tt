package com.huashi.sms.settings.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.settings.dao.SmsPriorityWordsMapper;
import com.huashi.sms.settings.domain.SmsPriorityWords;

/**
 * 优先级词库配置服务实现类
 * 
 * @author Administrator
 *
 */
@Service
public class SmsPriorityWordsService implements ISmsPriorityWordsService {

	@Autowired
	private SmsPriorityWordsMapper smsPriorityWordsMapper;
	@Reference
	private IUserService userService;

	@Override
	public BossPaginationVo<SmsPriorityWords> findPage(int pageNum, String userId, String content) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("content", content);
		BossPaginationVo<SmsPriorityWords> page = new BossPaginationVo<SmsPriorityWords>();
		page.setCurrentPage(pageNum);
		int total = smsPriorityWordsMapper.findCount(paramMap);
		if (total <= 0) {
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<SmsPriorityWords> dataList = smsPriorityWordsMapper.findList(paramMap);
		if(dataList !=null && dataList.size()>0){
			for (SmsPriorityWords words : dataList) {
				words.setUserModel(userService.getByUserId(words.getUserId()));
			}
		}
		page.getList().addAll(dataList);
		return page;
	}

	@Override
	public SmsPriorityWords get(int id) {
		return smsPriorityWordsMapper.selectByPrimaryKey(id);
	}

	@Override
	public boolean enableAndisable(SmsPriorityWords words) {
		return smsPriorityWordsMapper.updateByPrimaryKeySelective(words) > 0;
	}

	@Override
	public boolean save(SmsPriorityWords words) {
		words.setCreateTime(new Date());
		return smsPriorityWordsMapper.insertSelective(words) > 0;
	}

	@Override
	public boolean delete(int id) {
		return smsPriorityWordsMapper.deleteByPrimaryKey(id) > 0;
	}

}
