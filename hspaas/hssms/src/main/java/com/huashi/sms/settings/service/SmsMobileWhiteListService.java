/**************************************************************************
 * Copyright (c) 2015-2016 HangZhou Huashi, Inc. All rights reserved. 项目名称：华时短信平台
 * 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权 情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知 识产权保护的内容。
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
import org.springframework.data.redis.serializer.RedisSerializer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.settings.dao.SmsMobileWhiteListMapper;
import com.huashi.sms.settings.domain.SmsMobileWhiteList;

/**
 * 白名单服务接口实现类
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年2月20日 下午12:31:13
 */
@Service
public class SmsMobileWhiteListService implements ISmsMobileWhiteListService {

    @Reference
    private IUserService             userService;
    @Autowired
    private SmsMobileWhiteListMapper smsMobileWhiteListMapper;
    @Resource
    private StringRedisTemplate      stringRedisTemplate;

    private final Logger             logger = LoggerFactory.getLogger(getClass());

    private static Map<String, Object> response(String code, String msg) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result_code", code);
        resultMap.put("result_msg", msg);
        return resultMap;
    }

    @Override
    public Map<String, Object> batchInsert(SmsMobileWhiteList white) {
        if (StringUtils.isEmpty(white.getMobile())) {
            return response("-2", "参数不能为空！");
        }

        List<SmsMobileWhiteList> list = new ArrayList<>();
        try {
            // 前台默认是多个手机号码换行添加
            String[] mobiles = white.getMobile().split("\n");
            SmsMobileWhiteList mwl;
            for (String mobile : mobiles) {
                if (StringUtils.isBlank(mobile)) {
                    continue;
                }

                // 判断是否重复 重复则不保存
                int statCount = smsMobileWhiteListMapper.selectByUserIdAndMobile(white.getUserId(), mobile.trim());
                if (statCount > 0) {
                    continue;
                }

                mwl = new SmsMobileWhiteList();
                mwl.setMobile(mobile.trim());
                mwl.setUserId(white.getUserId());

                list.add(mwl);
            }

            if (CollectionUtils.isNotEmpty(list)) {
                smsMobileWhiteListMapper.batchInsert(list);

                // 批量操作无误后添加至缓存REDIS
                for (SmsMobileWhiteList ml : list) {
                    pushToRedis(ml);
                }
            }

            return response("success", "成功！");
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
    public PaginationVo<SmsMobileWhiteList> findPage(int userId, String phoneNumber, String startDate, String endDate,
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

        List<SmsMobileWhiteList> list = smsMobileWhiteListMapper.findPageListByUserId(params);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return new PaginationVo<>(list, _currentPage, totalRecord);
    }

    @Override
    public int deleteByPrimaryKey(int id) {
        return smsMobileWhiteListMapper.deleteByPrimaryKey(id);
    }

    @Override
    public BossPaginationVo<SmsMobileWhiteList> findPage(int pageNum, String keyword) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keyword", keyword);
        BossPaginationVo<SmsMobileWhiteList> page = new BossPaginationVo<>();
        page.setCurrentPage(pageNum);
        int total = smsMobileWhiteListMapper.findCount(paramMap);
        if (total <= 0) {
            return page;
        }
        page.setTotalCount(total);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());
        List<SmsMobileWhiteList> dataList = smsMobileWhiteListMapper.findList(paramMap);
        for (SmsMobileWhiteList m : dataList) {
            m.setUserProfile(userService.getProfileByUserId(m.getUserId()));
        }
        page.getList().addAll(dataList);
        return page;
    }

    /**
     * 获取白名单手机号码KEY名称
     *
     * @param userId 用户ID
     * @return key
     */
    private String getKey(Integer userId) {
        return String.format("%s:%d", SmsRedisConstant.RED_MOBILE_WHITELIST, userId);
    }

    @Override
    public boolean reloadToRedis() {
        List<SmsMobileWhiteList> list = smsMobileWhiteListMapper.selectAll();
        if (CollectionUtils.isEmpty(list)) {
            logger.info("数据库未检索到手机白名单，放弃填充REDIS");
            return true;
        }
        try {
            stringRedisTemplate.delete(stringRedisTemplate.keys(SmsRedisConstant.RED_MOBILE_WHITELIST + "*"));

            List<Object> con = stringRedisTemplate.execute((connection) -> {

                RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
                connection.openPipeline();
                for (SmsMobileWhiteList mwl : list) {
                    byte[] key = serializer.serialize(getKey(mwl.getUserId()));
                    connection.sAdd(key, serializer.serialize(JSON.toJSONString(mwl)));
                }

                return connection.closePipeline();

            }, false, true);

            return CollectionUtils.isNotEmpty(con);
        } catch (Exception e) {
            logger.warn("REDIS重载手机白名单数据失败", e);
            return false;
        }
    }

    /**
     * 添加到REDIS 数据中
     * 
     * @param mwl 手机白名单数据
     */
    private void pushToRedis(SmsMobileWhiteList mwl) {
        try {
            stringRedisTemplate.opsForSet().add(getKey(mwl.getUserId()), mwl.getMobile());
        } catch (Exception e) {
            logger.error("REDIS加载手机白名单信息", e);
        }
    }

    @Override
    public boolean isMobileWhitelist(int userId, String mobile) {
        if (userId == 0 || StringUtils.isEmpty(mobile)) {
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
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }

            return new HashSet<>(list);
        }
    }

}
