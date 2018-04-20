package com.huashi.sms.passage.service;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.passage.context.TemplateEnum.PassageTemplateType;
import com.huashi.common.settings.domain.Province;
import com.huashi.common.settings.service.IProvinceService;
import com.huashi.common.user.domain.UserPassage;
import com.huashi.common.user.model.UserModel;
import com.huashi.common.user.service.IUserPassageService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.monitor.passage.service.IPassageMonitorService;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.passage.context.PassageContext.PassageStatus;
import com.huashi.sms.passage.context.PassageContext.RouteType;
import com.huashi.sms.passage.dao.SmsPassageAccessMapper;
import com.huashi.sms.passage.dao.SmsPassageGroupDetailMapper;
import com.huashi.sms.passage.dao.SmsPassageMapper;
import com.huashi.sms.passage.dao.SmsPassageParameterMapper;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.passage.domain.SmsPassageGroupDetail;
import com.huashi.sms.passage.domain.SmsPassageParameter;

@Service
public class SmsPassageAccessService implements ISmsPassageAccessService {

    @Reference
    private IUserService userService;
    @Reference
    private IUserPassageService userPassageService;
    @Autowired
    private SmsPassageMapper smsPassageMapper;
    @Autowired
    private SmsPassageAccessMapper smsPassageAccessMapper;
    @Autowired
    private SmsPassageGroupDetailMapper smsPassageGroupDetailMapper;
    @Autowired
    private SmsPassageParameterMapper smsPassageParameterMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Reference
    private IPassageMonitorService passageMonitorService;
    @Reference
    private IProvinceService provinceService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<SmsPassageAccess> findPassageAccess() {
        return smsPassageAccessMapper.selectAll();
    }

    @Override
    public boolean save(SmsPassageAccess access) {
        access.setCreateTime(new Date());
        try {
            stringRedisTemplate.opsForHash().put(getMainKey(access.getUserId(), access.getCallType()), getAssistKey(access.getRouteType(), access.getCmcp(), access.getProvinceCode()), JSON.toJSONString(access));

            // 如果通道调用类型为 自取，需要同步到 监管中心
            if (PassageCallType.STATUS_RECEIPT_WITH_SELF_GET.getCode() == access.getCallType() || PassageCallType.SMS_MO_REPORT_WITH_SELF_GET.getCode() == access.getCallType()) {
                passageMonitorService.addPassagePull(access);
            }

        } catch (Exception e) {
            logger.warn("Redis 用户可用通道信息保存失败", e);
        }
        return smsPassageAccessMapper.insertSelective(access) > 0;
    }

    @Override
    public boolean reload() {
        // 加载所有待发送可用通道信息
        List<SmsPassageAccess> list = smsPassageAccessMapper.selectByType(PassageCallType.DATA_SEND.getCode());
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("缓冲可用通道失败，通道可用数据为空，请排查");
            return false;
        }

        Set<String> keys = stringRedisTemplate.keys(SmsRedisConstant.RED_USER_PASSAGE_ACCESS + "*");

        stringRedisTemplate.delete(keys);

        for (SmsPassageAccess access : list) {
            addAccessPassageToRedis(access);
        }
        return true;
    }

    /**
     * TODO 添加可用通道至REDIS系统
     *
     * @param access
     * @return
     */
    private boolean addAccessPassageToRedis(SmsPassageAccess access) {
        try {
            stringRedisTemplate.opsForHash().put(getMainKey(access.getUserId(), access.getCallType()), getAssistKey(access.getRouteType(), access.getCmcp(), access.getProvinceCode()), JSON.toJSONString(access));
            return true;
        } catch (Exception e) {
            logger.warn("Redis 用户可用通道信息缓冲失败", e);
            return false;
        }
    }

    @Override
    public boolean updateWhenPassageBroken(int passageId) {
        return false;
    }

    /**
     * TODO 获取REDIS通道组合KEY（用于REDIS用户通道组合使用）
     *
     * @param routeType
     * @param cmcp
     * @return
     */
    @Override
    public String getAssistKey(Integer routeType, Integer cmcp, Integer provinceCode) {
        return String.format("%d:%d:%d", routeType, cmcp, provinceCode);
    }

    /**
     * TODO 获取REDIS主KEY
     *
     * @param userId
     * @return
     */
    private static String getMainKey(Integer userId, Integer callType) {
        return String.format("%s:%d:%d", SmsRedisConstant.RED_USER_PASSAGE_ACCESS, userId, callType);
    }

    /**
     * TODO 获取用户模糊查询REDIS KEY
     *
     * @param userId
     * @return
     */
    private static String getMainLikeKey(Integer userId) {
        return String.format("%s:%d*", SmsRedisConstant.RED_USER_PASSAGE_ACCESS, userId);
    }

    @Override
    public boolean update(SmsPassageAccess access) {
        try {
            stringRedisTemplate.opsForHash().put(getMainKey(access.getUserId(), access.getCallType()), getAssistKey(access.getRouteType(), access.getCmcp(), access.getProvinceCode()), JSON.toJSONString(access));

            // 如果通道调用类型为 自取，需要同步到 监管中心
            if (PassageCallType.STATUS_RECEIPT_WITH_SELF_GET.getCode() == access.getCallType() || PassageCallType.SMS_MO_REPORT_WITH_SELF_GET.getCode() == access.getCallType()) {
                // 修改暂时不同步 监控中心，需要判断通道参数是否发生变动

            }

        } catch (Exception e) {
            logger.warn("Redis 用户可用通道信息更新失败, userId : {}, cmcp : {}, routeType :{}", access.getUserId(), access.getCmcp(), access.getRouteType(), e);
        }

        return smsPassageAccessMapper.updateByPrimaryKeySelective(access) > 0;
    }

    @Override
    public BossPaginationVo<SmsPassageAccess> findPage(int pageNum, String keyword, int userId) {
        BossPaginationVo<SmsPassageAccess> page = new BossPaginationVo<SmsPassageAccess>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("passageId", keyword);
        paramMap.put("userId", userId);
        int count = smsPassageAccessMapper.count(paramMap);
        page.setCurrentPage(pageNum);
        page.setTotalCount(count);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());
        List<SmsPassageAccess> list = smsPassageAccessMapper.findList(paramMap);

        Province province = null;
        Map<Integer, String> passageMap = new HashMap<>();
        Map<Integer, String> provinceMap = new HashMap<>();
        Map<Integer, UserModel> userMap = new HashMap<>();
        if (list != null && list.size() > 0) {
            for (SmsPassageAccess p : list) {
                if (passageMap.containsKey(p.getPassageId())) {
                    p.setPassageIdText(passageMap.get(p.getPassageId()));
                } else {
                    SmsPassage smsPassage = smsPassageMapper.selectByPrimaryKey(p.getPassageId());
                    if (smsPassage != null) {
                        p.setPassageIdText(smsPassage.getName());
                        passageMap.put(p.getPassageId(), smsPassage.getName());
                    }
                }

                if (userMap.containsKey(p.getUserId())) {
                    p.setUserModel(userMap.get(p.getUserId()));
                } else {
                    p.setUserModel(userService.getByUserId(p.getUserId()));
                    userMap.put(p.getUserId(), p.getUserModel());
                }

                // 设置省份名称
                if (provinceMap.containsKey(p.getProvinceCode())) {
                    p.setProvinceName(provinceMap.get(p.getProvinceCode()));
                } else {
                    // 根据省份代码查询省份名称
                    province = provinceService.get(p.getProvinceCode());
                    p.setProvinceName(province == null ? "未知" : province.getName());
                    provinceMap.put(p.getProvinceCode(), p.getProvinceName());
                }

                if (p.getCmcp() == null || p.getCmcp() == 0) {
                    p.setCmcpName(CMCP.GLOBAL.getTitle());
                } else {
                    p.setCmcpName(CMCP.getByCode(p.getCmcp()).getTitle());
                }

            }
        }
        page.setList(list);

        province = null;
        passageMap = null;
        provinceMap = null;
        userMap = null;
        return page;
    }

    @Override
    @Transactional
    public boolean updateByModifyUser(int userId) {
        try {
            // 根据userId 获取用户短信通道组关系信息
            UserPassage userPassage = userPassageService.getByUserIdAndType(userId, PassageTemplateType.SMS.getValue());
            if (userPassage == null) {
                logger.error("根据用户：{} 查不到相关短信通道对应关系", userId);
                return false;
            }

            // 根据通道组ID查询通道组详细信息
            List<SmsPassageGroupDetail> detailList = smsPassageGroupDetailMapper.findPassageByGroupId(userPassage.getPassageGroupId());
            if (CollectionUtils.isEmpty(detailList)) {
                logger.warn("通道组ID：{} 查不到相关短信通道集合数据", userPassage.getPassageGroupId());
            }

            // 根据用户ID删除所有的可用通道信息
            smsPassageAccessMapper.deleteByUserId(userId);
            // 删除该用户的ACCESS Redis中信息
            stringRedisTemplate.delete(stringRedisTemplate.keys(getMainLikeKey(userId)));

            Set<String> distinctAccessKeys = new HashSet<>();
            for (SmsPassageGroupDetail detail : detailList) {

                // 如果通道状态为不可用，直接忽略
                if (detail.getSmsPassage() == null || detail.getSmsPassage().getStatus() == null || PassageStatus.ACTIVE.getValue() != detail.getSmsPassage().getStatus()) {
                    continue;
                }

                if (isDone(distinctAccessKeys, detail)) {
                    continue;
                }

                replacePassageValue2Access(detail, userId, userPassage.getPassageGroupId());
            }

            return true;
        } catch (Exception e) {
            logger.error("userId: ["+ userId+"] 更改可用通道失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

    }

    /**
     * TODO 去重KEY（方便数据判断，去掉重复操作）
     *
     * @param routeType
     * @param cmcp
     * @param provinceCode
     * @return
     */
    private static String getAccessCutRepeatKey(Integer routeType, Integer cmcp, Integer provinceCode) {
        return String.format("%d:%d:%d", routeType, cmcp, provinceCode);
    }

    /**
     * TODO 判断是否已经处理完成 根据用户ID确定的情况下， 运营商：路由类型：省份代码 确定唯一性处理，如已处理，则本次忽略
     *
     * @param distinctAccessKeys
     * @param detail
     * @return
     */
    private boolean isDone(Set<String> distinctAccessKeys, SmsPassageGroupDetail detail) {
        if (CollectionUtils.isEmpty(distinctAccessKeys)) {
            return false;
        }

        if (detail == null) {
            logger.warn("通道关系数据为空，暂不处理");
            return true;
        }

        return !distinctAccessKeys.contains(getAccessCutRepeatKey(detail.getRouteType(), detail.getCmcp(), detail.getProvinceCode()));
    }

    /**
     * TODO 替换本次新的通道信息至可用通道中（access）
     *
     * @param detail
     * @param userId         用户ID
     * @param passageGroupId 通道组ID
     */
    private void replacePassageValue2Access(SmsPassageGroupDetail detail, Integer userId, Integer passageGroupId) {
        SmsPassage smsPassage = detail.getSmsPassage();

        try {
            List<SmsPassageParameter> parameterList = smsPassageParameterMapper.findByPassageId(detail.getPassageId());
            SmsPassageAccess access = null;
            for (SmsPassageParameter parameter : parameterList) {
                access = new SmsPassageAccess();
                access.setGroupId(passageGroupId);
                access.setCmcp(detail.getCmcp());
                access.setUserId(userId);

                // add by zhengying 2017-05-20
                // 扩展号码需要根据用户系统扩展号码和参数传递扩展号码累加计算，总长度不超过扩展号码长度
                access.setExtNumber(smsPassage.getExtNumber());
                access.setAccessCode(smsPassage.getAccessCode());
                access.setMobileSize(smsPassage.getMobileSize());
                access.setPacketsSize(smsPassage.getPacketsSize());
                access.setPassageId(detail.getPassageId());
                access.setPassageCode(smsPassage.getCode());
                access.setStatus(smsPassage.getStatus());
                access.setProvinceCode(detail.getProvinceCode());
                access.setRouteType(detail.getRouteType());
                access.setSignMode(smsPassage.getSignMode());
                access.setCallType(parameter.getCallType());
                access.setParams(parameter.getParams());
                access.setParamsDefinition(parameter.getParamsDefinition());
                access.setResultFormat(parameter.getResultFormat());
                access.setProtocol(parameter.getProtocol());
                access.setSuccessCode(parameter.getSuccessCode());
                access.setPosition(parameter.getPosition());
                access.setUrl(parameter.getUrl());
                access.setCreateTime(new Date());

                // add by zhengying 2017-08-16 加入最大连接数限制（如HTTP 最大20个， CMPP
                // 1个连接等）集群环境需要考虑
                access.setConnectionSize(smsPassage.getConnectionSize());

                // add by zhengying 2017-08-31 加入请求超时时间（毫秒）
                access.setReadTimeout(smsPassage.getReadTimeout());

                // add by zhengying 2017-09-18 加入网关提交参数中是否需要带入网关指定参数
                access.setSmsTemplateParam(smsPassage.getSmsTemplateParam());

                // 保存可用通道信息至REDIS 和DB
                this.save(access);
            }
        } catch (Exception e) {
            logger.error("替换可用通道新数据出错", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public boolean updateByModifyPassageGroup(int passageGroupId) {
        try {
            List<UserPassage> userPassageList = userPassageService.getPassageGroupListByGroupId(passageGroupId);
            for (UserPassage userPassage : userPassageList) {
                this.updateByModifyUser(userPassage.getUserId());
            }
            return true;
        } catch (Exception e) {
            logger.error("更新通道组失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

    }

    @Override
    @Transactional
    public boolean updateByModifyPassage(int passageId) {
        try {
            List<Integer> groupIdList = smsPassageGroupDetailMapper.getGroupIdByPassageId(passageId);
            for (Integer groupId : groupIdList) {
                this.updateByModifyPassageGroup(groupId);
            }

            return true;
        } catch (Exception e) {
            logger.error("更新通道失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public SmsPassageAccess get(int id) {
        SmsPassageAccess access = smsPassageAccessMapper.selectByPrimaryKey(id);
        if (access != null) {
            // 根据省份代码查询省份名称
            Province province = provinceService.get(access.getProvinceCode());
            access.setProvinceName(province == null ? "未知" : province.getName());
            // 设置路由类型名称
            access.setRouteTypeText(RouteType.parse(access.getRouteType()).getName());

            if (access.getCmcp() == null || access.getCmcp() == 0) {
                access.setCmcpName(CMCP.GLOBAL.getTitle());
            } else {
                access.setCmcpName(CMCP.getByCode(access.getCmcp()).getTitle());
            }
        }

        return access;
    }

    @Override
    public List<SmsPassageAccess> findWaitPulling(PassageCallType callType) {
        return smsPassageAccessMapper.selectWaitPulling(callType.getCode());
    }

    @Override
    public List<SmsPassageAccess> findPassageBalace() {
        return smsPassageAccessMapper.selectByType(PassageCallType.PASSAGE_BALANCE_GET.getCode());
    }

    @Override
    public SmsPassageAccess getByType(PassageCallType callType, String passageCode) {
        return smsPassageAccessMapper.getByTypeAndUrl(callType.getCode(), passageCode);
    }

    @Override
    public boolean deletePassageAccess(Integer passageId) {
        try {
            List<SmsPassageAccess> list = smsPassageAccessMapper.selectByPassageId(passageId);
            if (CollectionUtils.isEmpty(list)) {
                return true;
            }

            int result = smsPassageAccessMapper.deleteByPasageId(passageId);
            if (result == 0) {
                throw new RuntimeException("删除可用通道持久化异常");
            }

            List<String> ids = new ArrayList<String>(list.size());
            for (SmsPassageAccess access : list) {
                stringRedisTemplate.opsForHash().delete(getMainKey(access.getUserId(), access.getCallType()), getAssistKey(access.getRouteType(), access.getCmcp(), access.getProvinceCode()));

                // 如果通道调用类型为 自取，需要同步到 监管中心（停止轮训扫描）
                if (PassageCallType.STATUS_RECEIPT_WITH_SELF_GET.getCode() == access.getCallType() || PassageCallType.SMS_MO_REPORT_WITH_SELF_GET.getCode() == access.getCallType()) {
                    passageMonitorService.removePasagePull(access);
                }

                ids.add(access.getId().toString());
            }

            if (CollectionUtils.isEmpty(ids)) {
                throw new RuntimeException("查询可用通道Ids数据为空");
            }

            logger.info("删除可用通道: {} 成功", passageId);

            return true;
        } catch (Exception e) {
            logger.error("删除可用通道失败，通道ID: {} 失败信息", passageId, e);
            return false;
        }
    }

    @Override
    public boolean updateAccessStatus(Integer passageId, Integer status) {
        if (passageId == null || status == null) {
            logger.error("数据异常，更改可用通道状态失败，passageId : {}, status : {}", passageId, status);
            return false;
        }

        try {
            List<SmsPassageAccess> list = smsPassageAccessMapper.selectByPassageId(passageId);
            if (CollectionUtils.isEmpty(list)) {
                return true;
            }

            int result = smsPassageAccessMapper.updateStatusByPassageId(passageId, status);
            if (result == 0) {
                throw new RuntimeException("更新可用通道持久化异常");
            }

            for (SmsPassageAccess access : list) {
                // 如果状态一致，则无需修改REDIS缓存数据
                if (access.getStatus() != null && Objects.equals(access.getStatus(), status)) {
                    continue;
                }

                access.setStatus(status);
                stringRedisTemplate.opsForHash().put(getMainKey(access.getUserId(), access.getCallType()), getAssistKey(access.getRouteType(), access.getCmcp(), access.getProvinceCode()), JSON.toJSONString(access));

                // 如果通道调用类型为 自取，需要同步到 监管中心（停止轮训扫描）
                if (PassageCallType.STATUS_RECEIPT_WITH_SELF_GET.getCode() == access.getCallType() || PassageCallType.SMS_MO_REPORT_WITH_SELF_GET.getCode() == access.getCallType()) {
                    if (PassageStatus.ACTIVE.getValue() == status) {
                        passageMonitorService.addPassagePull(access);
                    } else {
                        passageMonitorService.removePasagePull(access);
                    }
                }
            }

            logger.info("更新可用通道: {} 状态：{} 成功", passageId, status);

            return true;
        } catch (Exception e) {
            logger.error("修改可用通道状态失败，通道ID: {}， 状态：{} 失败信息", passageId, status, e);
            return false;
        }

    }

    @Override
    public Map<String, SmsPassageAccess> getByUserId(int userId) {
        Map<String, SmsPassageAccess> passages = new HashMap<String, SmsPassageAccess>();
        try {
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(getMainKey(userId, PassageCallType.DATA_SEND.getCode()));
            if (MapUtils.isNotEmpty(entries)) {

                for (Object key : entries.keySet()) {
                    SmsPassageAccess passage = JSON.parseObject(entries.get(key).toString(), SmsPassageAccess.class);
                    passages.put(getAssistKey(passage.getRouteType(), passage.getCmcp(), passage.getProvinceCode()), passage);
                }

                return passages;
            }

        } catch (Exception e) {
            logger.warn("Redis 用户可用通道信息查询失败, userId : {}, 将在DB获取", userId, e);
            List<SmsPassageAccess> list = smsPassageAccessMapper.selectByUserIdAndCallType(userId, PassageCallType.DATA_SEND.getCode());
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }

            // edity by 20180418 修改之前的redis中值设置
            Map<String, String> redisPassageValues = new HashMap<>(list.size());
            for (SmsPassageAccess passage : list) {
                String assistKey = getAssistKey(passage.getRouteType(), passage.getCmcp(), passage.getProvinceCode());
                passages.put(assistKey, passage);
                redisPassageValues.put(assistKey, JSON.toJSONString(passage));
            }
            
            stringRedisTemplate.opsForHash().putAll(getMainKey(userId, PassageCallType.DATA_SEND.getCode()), redisPassageValues);

            return passages;
        }

        return null;
    }

    @Override
    public SmsPassageAccess get(int userId, int route, int cmcp, int provinceCode) {
        try {
            Object object = stringRedisTemplate.opsForHash().get(getMainKey(userId, PassageCallType.DATA_SEND.getCode()), getAssistKey(route, cmcp, provinceCode));
            if (object != null) {
                return JSON.parseObject(object.toString(), SmsPassageAccess.class);
            }

        } catch (Exception e) {
            logger.warn("Redis 用户可用通道信息查询失败, userId : {}, cmcp : {}", userId, cmcp, e);
        }

        SmsPassageAccess access = null;
        try {
            access = smsPassageAccessMapper.selectByUserIdAndRouteCmcp(userId, route, cmcp, provinceCode);
            if (access == null) {
                return null;
            }

            stringRedisTemplate.opsForHash().put(getMainKey(access.getUserId(), access.getCmcp()), getAssistKey(access.getRouteType(), access.getCallType(), access.getProvinceCode()), JSON.toJSONString(access));
        } catch (Exception e) {
            logger.warn("Redis 用户可用通道信息添加失败, userId : {}, cmcp : {}", userId, cmcp, e);
        }
        return access;
    }

}
