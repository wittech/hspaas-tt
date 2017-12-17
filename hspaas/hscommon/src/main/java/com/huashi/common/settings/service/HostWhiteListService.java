/**************************************************************************
 * Copyright (c) 2015-2016 HangZhou Huashi, Inc.
 * All rights reserved.
 * 
 * 项目名称：华时短信平台
 * 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
package com.huashi.common.settings.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.config.redis.CommonRedisConstant;
import com.huashi.common.settings.dao.HostWhiteListMapper;
import com.huashi.common.settings.domain.HostWhiteList;
import com.huashi.common.user.dao.UserMapper;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;

/**
 * TODO ip白名单
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年2月23日 下午10:17:59
 */
@Service
public class HostWhiteListService implements IHostWhiteListService {

	@Autowired
	private HostWhiteListMapper hostWhiteListMapper;
	@Autowired
	private UserMapper userMapper;
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public int updateByPrimaryKey(HostWhiteList record) {
		int result = hostWhiteListMapper.selectByUserIdAndIp(record.getUserId(), record.getIp());
		if (result == 0) {
			pushToRedis(record.getUserId(), record.getIp());
			
			return hostWhiteListMapper.updateByPrimaryKeySelective(record);
		} else {
			return 2;
		}
	}

	@Override
	public int deleteByPrimaryKey(int id) {
		return hostWhiteListMapper.deleteByPrimaryKey(id);
	}

	@Override
	public Map<String, Object> batchInsert(HostWhiteList record) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		int count = 0;
		boolean is_successs = true;
		//重复标记
		boolean is_flag=false;
		String context = "";
		if (StringUtils.isEmpty(record.getIp())) {
			resultMap.put("result_code", "-2");
			resultMap.put("result_msg", "参数不能为空！");
			return resultMap;
		}
		try {
			String str[] = record.getIp().split("\n");
			for (int i = 0; i < str.length; i++) {
				count++;
				record.setIp(str[i]);
				record.setStatus(0);
				// 标记重复提示
				boolean codeFlag = false;
				// 去空格验证是否为空
				if (!StringUtils.isEmpty(str[i].trim())) {
					// 判断是否重复 重复则不保存
					int exisCount = hostWhiteListMapper.selectByUserIdAndIp(record.getUserId(), record.getIp().trim());
					if (exisCount == 0) {
//						flag = hostWhiteListMapper.batchInsert(record) > 0;
					} else {
						is_flag=true;
						codeFlag = true;
						is_successs=false;
					}
				}
				if (codeFlag) {
					context += "重复行：" + count + ";ip地址：" + str[i] + ";";
				}
			}
			//保存
			if(!is_flag){
				for (int i = 0; i < str.length; i++) {
					count++;
					record.setIp(str[i]);
					record.setStatus(0);
					// 标记重复提示
					// 去空格验证是否为空
					if (!StringUtils.isEmpty(str[i].trim())) {
						pushToRedis(record.getUserId(), record.getIp());
						hostWhiteListMapper.batchInsert(record);
					}
				}
			}
			if (is_successs) {
				resultMap.put("result_code", "success");
				resultMap.put("result_msg", "成功！");
			} else {
				resultMap.put("result_code", "fail");
				resultMap.put("result_msg", context);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result_code", "exption");
			resultMap.put("result_msg", "异常" + e.getMessage());
		}
		return resultMap;
	}

	@Override
	public HostWhiteList getById(int id) {
		return hostWhiteListMapper.selectActiveById(id);
	}

	@Override
	public PaginationVo<HostWhiteList> findPage(int userId, String ip,
			String startDate, String endDate, String currentPage) {
		if (userId <= 0) {
            {
                return null;
            }
        }

		int _currentPage = PaginationVo.parse(currentPage);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		if (StringUtils.isNotEmpty(ip)) {
			params.put("ip", ip);
		}
		params.put("startDate", startDate);
		params.put("endDate", endDate);

		int totalRecord = hostWhiteListMapper.selectCount(params);
		if (totalRecord == 0) {
            {
                return null;
            }
        }

		params.put("startPage", PaginationVo.getStartPage(_currentPage));
		params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

		List<HostWhiteList> list = hostWhiteListMapper.findPageList(params);
		if (list == null || list.isEmpty()) {
            {
                return null;
            }
        }
		return new PaginationVo<HostWhiteList>(list, _currentPage, totalRecord);
	}
	
	@Override
	public BossPaginationVo<HostWhiteList> findPageBoss(int pageNum, String ip, String status, String userId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("ip", ip);
		paramMap.put("status", status);
		paramMap.put("userId", userId);
		BossPaginationVo<HostWhiteList> page = new BossPaginationVo<HostWhiteList>();
		page.setCurrentPage(pageNum);
		int total = hostWhiteListMapper.selectCount(paramMap);
		if (total <= 0) {
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<HostWhiteList> dataList = hostWhiteListMapper.findPageListBoss(paramMap);
		for(HostWhiteList m : dataList){
			m.setUserModel(userMapper.selectMappingByUserId(m.getUserId()));
		}
		page.getList().addAll(dataList);
		return page;
	}


	@Override
	public boolean ipAllowedPass(int userId, String ip) {
		try {
			Object ips = stringRedisTemplate.opsForHash().get(CommonRedisConstant.RED_USER_WHITE_HOST, 
					String.valueOf(userId));
			if(ips != null) {
                {
                    return ips.toString().contains(ip);
                }
            }
			
		} catch (Exception e) {
			logger.warn("REDIS 操作用户服务器IP配置失败", e);
		}
		
//		int result = hostWhiteListMapper.selectByUserIdAndIp(userId, ip);
//		if(result == 0) {
//			logger.warn("用户IP 未报备，及时备案");
//		}
		
		return true;
	}
	
	/**
	 * 
	   * TODO 添加至REDIS
	   * 
	   * @param userSmsConfig
	 */
	private void pushToRedis(int userId, String ip) {
		try {
			Object obj = stringRedisTemplate.opsForHash().get(CommonRedisConstant.RED_USER_WHITE_HOST, String.valueOf(userId));
			if(obj != null) {
				ip = String.format("%s,%s", ip, obj.toString());
			}
			
			stringRedisTemplate.opsForHash().put(CommonRedisConstant.RED_USER_WHITE_HOST, 
					String.valueOf(userId), ip);
			
		} catch (Exception e) {
			logger.warn("REDIS 操作用户服务器IP配置失败", e);
		}
	}

	@Override
	public boolean reloadToRedis() {
		List<HostWhiteList> list = hostWhiteListMapper.selectAll();
		if(CollectionUtils.isEmpty(list)) {
			logger.warn("服务器IP报备为空");
			return true;
		}
			
		stringRedisTemplate.delete(CommonRedisConstant.RED_USER_WHITE_HOST);
		for(HostWhiteList hwl : list) {
            {
                pushToRedis(hwl.getUserId(), hwl.getIp());
            }
        }
		
		return true;
	}

}
