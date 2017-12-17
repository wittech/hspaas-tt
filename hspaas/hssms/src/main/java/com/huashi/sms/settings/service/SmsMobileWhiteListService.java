/**************************************************************************
 * Copyright (c) 2015-2016 HangZhou Huashi, Inc.
 * All rights reserved.
 * 
 * 项目名称：华时短信平台
 * 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
package com.huashi.sms.settings.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.settings.dao.SmsMobileWhiteListMapper;
import com.huashi.sms.settings.domain.SmsMobileWhiteList;

/**
 * TODO 白名单服务接口实现类
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年2月20日 下午12:31:13
 */
@Service
public class SmsMobileWhiteListService implements ISmsMobileWhiteListService {

	@Reference
	private IUserService userService;
	@Autowired
	private SmsMobileWhiteListMapper smsMobileWhiteListMapper;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static Map<String, Object> response(String code, String msg) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result_code", code);
		resultMap.put("result_msg", msg);
		return resultMap;
	}
	
	/**
	 * 
	   * TODO 拼接重复错误报告信息
	   * @param lineNo
	   * @param mobile
	   * @return
	 */
	private static String appendRepeatReport(Integer lineNo, String mobile) {
		return String.format("重复数据，行号：%d， 手机号码：%s;", lineNo, mobile);
	}

	@Override
	public Map<String, Object> batchInsert(SmsMobileWhiteList white) {
		StringBuilder mobileRepeatReport = new StringBuilder();
		if (StringUtils.isEmpty(white.getMobile())) {
            return response("-2", "参数不能为空！");
        }
		
		int existsCount = 0;
		List<SmsMobileWhiteList> list = new ArrayList<>();
		try {
			// 前台默认是多个手机号码换行添加
			String str[] = white.getMobile().split("\n");
			SmsMobileWhiteList mwl = null;
			for (int i = 0; i < str.length; i++) {
				if(StringUtils.isEmpty(str[i]) || StringUtils.isEmpty(str[i].trim())) {
                    continue;
                }
				
				// 判断是否重复 重复则不保存
				int statCount = smsMobileWhiteListMapper.selectByUserIdAndMobile(white.getUserId(), str[i].trim());
				if (statCount > 0) {
					mobileRepeatReport.append(appendRepeatReport(i + 1, str[i]));
					existsCount ++;
					continue;
				}
				
				mwl = new SmsMobileWhiteList();
				mwl.setMobile(str[i]);
				mwl.setUserId(white.getUserId());
				
				list.add(mwl);
			}
			
			// 如果手机号码中包含已存在的数据
			if (existsCount > 0) {
                return response("fail", mobileRepeatReport.toString());
            }
			
			if(CollectionUtils.isNotEmpty(list)) {
				smsMobileWhiteListMapper.batchInsert(list);
				
				// 批量操作无误后添加至缓存REDIS
				for(SmsMobileWhiteList ml : list) {
                    pushToRedis(ml);
                }
			}
				
			
			return  response("success", "成功！");
		} catch (Exception e) {
			logger.info("添加白名单失败", e);
			return response("exption", "操作失败");
		}
	}

	@Override
	public List<SmsMobileWhiteList> selectByUserId(int userId) {
		return smsMobileWhiteListMapper.selectByUserId(userId);
	}

	@Override
	public PaginationVo<SmsMobileWhiteList> findPage(int userId,
			String phoneNumber, String startDate, String endDate,
			String currentPage) {
		int _currentPage = PaginationVo.parse(currentPage);

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		if (StringUtils.isNotEmpty(phoneNumber)) {
			params.put("phoneNumber", phoneNumber);
		}
		params.put("startDate", startDate);
		params.put("endDate", endDate);

		int totalRecord = smsMobileWhiteListMapper.getCountByUserId(params);
		if (totalRecord == 0) {
            return null;
        }

		params.put("startPage", PaginationVo.getStartPage(_currentPage));
		params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

		List<SmsMobileWhiteList> list = smsMobileWhiteListMapper
				.findPageListByUserId(params);
		if (list == null || list.isEmpty()) {
            return null;
        }
		return new PaginationVo<SmsMobileWhiteList>(list, _currentPage,
				totalRecord);
	}

	@Override
	public int deleteByPrimaryKey(int id) {
		return smsMobileWhiteListMapper.deleteByPrimaryKey(id);
	}

	@Override
	public BossPaginationVo<SmsMobileWhiteList> findPage(int pageNum, String keyword) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("keyword", keyword);
		BossPaginationVo<SmsMobileWhiteList> page = new BossPaginationVo<SmsMobileWhiteList>();
		page.setCurrentPage(pageNum);
		int total = smsMobileWhiteListMapper.findCount(paramMap);
		if (total <= 0) {
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<SmsMobileWhiteList> dataList = smsMobileWhiteListMapper.findList(paramMap);
		for(SmsMobileWhiteList m : dataList){
			m.setUserProfile(userService.getProfileByUserId(m.getUserId()));
		}
		page.getList().addAll(dataList);
		return page;
	}
	
	/**
	 * 
	   * TODO 获取白名单手机号码KEY名称
	   * 
	   * @param userId
	   * @return
	 */
	private String getKey(Integer userId) {
		return String.format("%s:%d", SmsRedisConstant.RED_MOBILE_WHITELIST, userId);
	}

	@Override
	public boolean reloadToRedis() {
		List<SmsMobileWhiteList> list = smsMobileWhiteListMapper.selectAll();
		if(CollectionUtils.isEmpty(list)) {
			logger.info("数据库未检索到手机白名单，放弃填充REDIS");
			return true;
		}
		try {
			stringRedisTemplate.delete(stringRedisTemplate.keys(SmsRedisConstant.RED_MOBILE_WHITELIST + "*"));
			for(SmsMobileWhiteList mwl : list) {
				pushToRedis(mwl);
			}
			return true;
		} catch (Exception e) {
			logger.warn("REDIS重载手机白名单数据失败", e);
			return false;
		} 
	}
	
	public boolean pushToRedis(SmsMobileWhiteList mwl) {
		try {
			stringRedisTemplate.opsForSet().add(getKey(mwl.getUserId()), mwl.getMobile());
			return true;
		} catch (Exception e) {
			logger.error("REDIS加载手机白名单信息", e);
			return false;
		}
	}

	@Override
	public boolean isMobileWhitelist(int userId, String mobile) {
		if(userId == 0 || StringUtils.isEmpty(mobile)) {
            return false;
        }
		
		try {
			return stringRedisTemplate.opsForSet().isMember(getKey(userId), mobile);
		} catch (Exception e) {
			logger.warn("redis 获取手机号码白名单失败，将从DB加载", e);
			return smsMobileWhiteListMapper.selectByUserIdAndMobile(userId, mobile) > 0;
		}
	}

	@Override
	public Set<String> getByUserId(int userId) {
		try {
			return stringRedisTemplate.opsForSet().members(getKey(userId));
		} catch (Exception e) {
			logger.warn("redis 获取手机号码白名单集合失败，将从DB加载", e);
			List<String> list = smsMobileWhiteListMapper.selectDistinctMobilesByUserId(userId);
			if(CollectionUtils.isEmpty(list)) {
                return null;
            }
			
			return new HashSet<String>(list);
		}
	}

}
