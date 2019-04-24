/**************************************************************************
 * Copyright (c) 2015-2016 HangZhou Huashi, Inc. All rights reserved. 项目名称：华时短信平台
 * 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权 情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知 识产权保护的内容。
 ***************************************************************************/
package com.huashi.sms.settings.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant.MessageAction;
import com.huashi.sms.settings.constant.MobileBlacklistType;
import com.huashi.sms.settings.dao.SmsMobileBlackListMapper;
import com.huashi.sms.settings.domain.SmsMobileBlackList;

/**
 * TODO 黑名单服务接口类
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年2月20日 下午12:30:55
 */
@Service
public class SmsMobileBlackListService implements ISmsMobileBlackListService {

    private Logger                              logger                  = LoggerFactory.getLogger(getClass());

    @Reference
    private IUserService                        userService;
    @Resource
    private StringRedisTemplate                 stringRedisTemplate;
    @Autowired
    private SmsMobileBlackListMapper            smsMobileBlackListMapper;

    /**
     * 全局手机号码（与REDIS 同步采用广播模式）
     */
    public static volatile Map<String, Integer> GLOBAL_MOBILE_BLACKLIST = new ConcurrentHashMap<>();

    /**
     * TODO 消息拼接返回
     * 
     * @param code
     * @param msg
     * @return
     */
    private Map<String, Object> response(String code, String msg) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("result_code", code);
        resultMap.put("result_msg", msg);
        return resultMap;
    }

    @Override
    @Transactional
    public Map<String, Object> batchInsert(SmsMobileBlackList black) {
        if (StringUtils.isEmpty(black.getMobile())) {
            return response("-2", "参数不能为空！");
        }

        List<SmsMobileBlackList> list = new ArrayList<>();
        try {
            // 前台默认是多个手机号码换行添加
            String[] mobiles = black.getMobile().split("\n");
            SmsMobileBlackList mbl;
            for (String mobile : mobiles) {
                if (StringUtils.isBlank(mobile)) {
                    continue;
                }

                // 判断是否重复 重复则不保存
                if (isMobileBelongtoBlacklist(mobile.trim())) {
                    continue;
                }

                mbl = new SmsMobileBlackList();
                mbl.setMobile(mobile.trim());
                mbl.setType(black.getType());
                mbl.setRemark(black.getRemark());
                list.add(mbl);
            }

            // 批量添加黑名单
            if (CollectionUtils.isNotEmpty(list)) {
                smsMobileBlackListMapper.batchInsert(list);
                // 批量操作无误后添加至缓存REDIS
                for (SmsMobileBlackList ml : list) {
                    publishToRedis(MessageAction.ADD, ml.getMobile(), ml.getType());
                }
            }

            return response("success", "成功！");
        } catch (Exception e) {
            logger.info("添加手机号码黑名单失败", e);
            return response("exption", "操作失败");
        }
    }

    @Override
    public PaginationVo<SmsMobileBlackList> findPage(String mobile, String startDate, String endDate,
                                                     String currentPage) {

        int _currentPage = PaginationVo.parse(currentPage);

        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        int totalRecord = smsMobileBlackListMapper.getCount(params);
        if (totalRecord == 0) {
            return null;
        }

        params.put("startPage", PaginationVo.getStartPage(_currentPage));
        params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

        List<SmsMobileBlackList> list = smsMobileBlackListMapper.findPageList(params);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return new PaginationVo<>(list, _currentPage, totalRecord);
    }

    @Override
    public int deleteByPrimaryKey(int id) {
        try {
            SmsMobileBlackList smsMobileBlackList = smsMobileBlackListMapper.selectByPrimaryKey(id);
            publishToRedis(MessageAction.REMOVE, smsMobileBlackList.getMobile(), smsMobileBlackList.getType());

        } catch (Exception e) {
            logger.warn("Redis 删除黑名单数据信息失败, id : {}", id, e);
        }

        return smsMobileBlackListMapper.deleteByPrimaryKey(id);
    }

    @Override
    public boolean isMobileBelongtoBlacklist(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return false;
        }

        try {
            return GLOBAL_MOBILE_BLACKLIST.keySet().contains(mobile);
        } catch (Exception e) {
            logger.warn("Redis 手机号黑名单查询失败", e);
        }

        return smsMobileBlackListMapper.selectByMobile(mobile) > 0;
    }

    /**
     * TODO 根据黑名单类型判断是否属于忽略的黑名单（配合短信模板配置使用）
     * 
     * @param type
     * @return
     */
    private static boolean isBelongtoIgnored(Integer type) {
        return type == null || MobileBlacklistType.NORMAL.getCode() == type
               || MobileBlacklistType.UNSUBSCRIBE.getCode() == type;
    }

    @Override
    public List<String> filterBlacklistMobile(List<String> mobiles, boolean isIgnored) {
        try {
            List<String> blackList = new ArrayList<>(mobiles);

            // 与黑名单总集取交集
            blackList.retainAll(GLOBAL_MOBILE_BLACKLIST.keySet());

            // 需要排除忽略的黑名单类型号码 add by 2018-05-02
            if (isIgnored) {
                // 需要删除的集合信息
                List<String> needRemoveList = new ArrayList<>(blackList.size());
                for (String mobile : blackList) {
                    if (isBelongtoIgnored(GLOBAL_MOBILE_BLACKLIST.get(mobile))) {
                        needRemoveList.add(mobile);
                    }
                }

                blackList.removeAll(needRemoveList);
            }

            mobiles.removeAll(blackList);
            return blackList;

        } catch (Exception e) {
            logger.error("黑名单解析失败", e);
            return mobiles;
        }
    }

    @Override
    public BossPaginationVo<SmsMobileBlackList> findPage(int pageNum, String keyword) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keyword", keyword);
        BossPaginationVo<SmsMobileBlackList> page = new BossPaginationVo<SmsMobileBlackList>();
        page.setCurrentPage(pageNum);
        int total = smsMobileBlackListMapper.findCount(paramMap);
        if (total <= 0) {
            return page;
        }
        page.setTotalCount(total);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());

        List<SmsMobileBlackList> list = smsMobileBlackListMapper.findList(paramMap);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        for (SmsMobileBlackList sbl : list) {
            sbl.setTypeText(MobileBlacklistType.parse(sbl.getType()));
        }

        page.getList().addAll(list);
        return page;
    }

    /**
     * TODO REDIS队列数据操作(订阅发布模式)
     *
     * @param mobile
     * @param action
     * @return
     */
    private void publishToRedis(MessageAction action, String mobile, Integer type) {
        try {

            stringRedisTemplate.convertAndSend(SmsRedisConstant.BROADCAST_MOBILE_BLACKLIST_TOPIC,
                                               String.format("%d:%s:%d", action.getCode(), mobile, type));
        } catch (Exception e) {
            logger.error("加入黑名单数据错误", e);
        }
    }

    private boolean pushToRedis(final List<SmsMobileBlackList> list) {
        try {
            long size = stringRedisTemplate.opsForHash().size(SmsRedisConstant.RED_MOBILE_BLACKLIST);
            if (size == list.size()) {

                // 初始化JVM 全局数据
                for (SmsMobileBlackList mbl : list) {
                    GLOBAL_MOBILE_BLACKLIST.put(mbl.getMobile(), mbl.getType());
                }

                logger.info("手机黑名单数据与DB数据一致，无需重载");
                return true;
            }

            long start = System.currentTimeMillis();
            stringRedisTemplate.delete(SmsRedisConstant.RED_MOBILE_BLACKLIST);
            List<Object> result = stringRedisTemplate.execute((connection) -> {
                RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
                byte[] key = serializer.serialize(SmsRedisConstant.RED_MOBILE_BLACKLIST);
                connection.openPipeline();

                // Map<byte[], byte[]> map = new HashMap<>();
                for (SmsMobileBlackList mbl : list) {
                    // map.put(serializer.serialize(mbl.getMobile()), serializer.serialize(mbl.getType()+ ""));
                    GLOBAL_MOBILE_BLACKLIST.put(mbl.getMobile(), mbl.getType());
                    connection.hSet(key, serializer.serialize(mbl.getMobile()),
                                    serializer.serialize(mbl.getType() + ""));
                }
                // connection.hMSet(key, map);
                return connection.closePipeline();
            }, false, true);

            logger.info("--------初始化JVM黑名单" + (System.currentTimeMillis() - start) + "ms");

            return CollectionUtils.isNotEmpty(result);
        } catch (Exception e) {
            logger.error("REDIS数据LOAD手机号码黑名单失败", e);
            return false;
        }
    }

    @Override
    public boolean reloadToRedis() {
        List<SmsMobileBlackList> list = smsMobileBlackListMapper.selectAllMobiles();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("未找到手机号码黑名单数据");
            return false;
        }

        return pushToRedis(list);
    }

}
