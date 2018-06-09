package com.huashi.sms.passage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.druid.util.StringUtils;
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
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.passage.domain.SmsPassageGroupDetail;
import com.huashi.sms.passage.domain.SmsPassageParameter;

@Service
public class SmsPassageAccessService implements ISmsPassageAccessService {

    @Reference
    private IUserService                                  userService;
    @Autowired
    private ISmsPassageService                            smsPassageService;
    @Reference
    private IUserPassageService                           userPassageService;
    @Autowired
    private SmsPassageAccessMapper                        smsPassageAccessMapper;
    @Autowired
    private SmsPassageGroupDetailMapper                   smsPassageGroupDetailMapper;
    @Autowired
    private ISmsPassageParameterService                   smsPassageParameterService;
    @Resource
    private StringRedisTemplate                           stringRedisTemplate;
    @Reference
    private IPassageMonitorService                        passageMonitorService;
    @Reference
    private IProvinceService                              provinceService;

    /**
     * 全局可用通道缓存
     */
    public static volatile Map<String, SmsPassageAccess>  GLOBAL_PASSAGE_ACCESS_CONTAINER = new ConcurrentHashMap<>();

    /**
     * 通道代码+通道类型组装的共享本地数据
     */
    private static volatile Map<String, SmsPassageAccess> CODE_TYPE_ACCESS_CONTAINER      = new ConcurrentHashMap<>();

    /**
     * 日志输入
     */
    private final Logger                                  logger                          = LoggerFactory.getLogger(getClass());

    @Override
    public List<SmsPassageAccess> findPassageAccess() {
        return smsPassageAccessMapper.selectAll();
    }

    @Override
    public boolean save(SmsPassageAccess access) {
        access.setCreateTime(new Date());
        try {
            loadToRedis(access);

            // 如果通道调用类型为 自取，需要同步到 监管中心
            monitorThreadNotice(access, PassageStatus.ACTIVE.getValue());

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

        // Set<String> keys = stringRedisTemplate.keys(SmsRedisConstant.RED_USER_PASSAGE_ACCESS + "*");
        // stringRedisTemplate.delete(keys);

        for (SmsPassageAccess access : list) {
            loadToRedis(access);
        }
        return true;
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
    private String getFullKey(Integer userId, Integer callType, Integer routeType, Integer cmcp, Integer provinceCode) {
        return getMainKey(userId, callType) + getAssistKey(routeType, cmcp, provinceCode);
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
            loadToRedis(access);

            // 如果通道调用类型为 自取，需要同步到 监管中心
            if (PassageCallType.STATUS_RECEIPT_WITH_SELF_GET.getCode() == access.getCallType()
                || PassageCallType.SMS_MO_REPORT_WITH_SELF_GET.getCode() == access.getCallType()) {
                // 修改暂时不同步 监控中心，需要判断通道参数是否发生变动

            }

        } catch (Exception e) {
            logger.warn("Redis 用户可用通道信息更新失败, userId : {}, cmcp : {}, routeType :{}", access.getUserId(),
                        access.getCmcp(), access.getRouteType(), e);
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
                    SmsPassage smsPassage = smsPassageService.findById(p.getPassageId());
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
            Integer passageGroupId = userPassageService.getByUserIdAndType(userId, PassageTemplateType.SMS.getValue());
            if (passageGroupId == null) {
                logger.error("根据用户：{} 查不到相关短信通道对应关系", userId);
                return false;
            }

            rebandPassageAccessInGroup(userId, passageGroupId);

            return true;
        } catch (Exception e) {
            logger.error("userId: [" + userId + "] 更改可用通道失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

    }

    /**
     * TODO 根据通道组内的通道数据重新绑定可用通道信息
     * 
     * @param userId
     * @param passageGroupId
     */
    private void rebandPassageAccessInGroup(int userId, int passageGroupId) {
        try {
            // 根据用户ID删除所有的可用通道信息
            smsPassageAccessMapper.deleteByUserId(userId);
            // 删除该用户的ACCESS Redis中信息
            stringRedisTemplate.delete(stringRedisTemplate.keys(getMainLikeKey(userId)));

            // 根据通道组ID查询通道组详细信息
            List<SmsPassageGroupDetail> detailList = smsPassageGroupDetailMapper.findPassageByGroupId(passageGroupId);
            if (CollectionUtils.isEmpty(detailList)) {
                logger.warn("通道组ID：{} 查不到相关短信通道集合数据", passageGroupId);
            }

            // 已经处理过的可用通道信息则不重复处理
            Set<String> distinctAccessKeys = new HashSet<>();
            for (SmsPassageGroupDetail detail : detailList) {

                // 如果通道状态为不可用，直接忽略
                if (detail == null || detail.getSmsPassage() == null || detail.getSmsPassage().getStatus() == null
                    || PassageStatus.ACTIVE.getValue() != detail.getSmsPassage().getStatus()) {
                    continue;
                }

                if (ignoreIfHasDone(distinctAccessKeys, detail)) {
                    continue;
                }

                replacePassageValue2Access(detail, userId, passageGroupId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
    private static String repeatConditionKey(Integer routeType, Integer cmcp, Integer provinceCode) {
        return String.format("%d:%d:%d", routeType, cmcp, provinceCode);
    }

    /**
     * TODO 判断是否已经处理完成 根据用户ID确定的情况下， 运营商：路由类型：省份代码 确定唯一性处理，如已处理，则本次忽略
     *
     * @param distinctAccessKeys
     * @param detail
     * @return
     */
    private boolean ignoreIfHasDone(Set<String> distinctAccessKeys, SmsPassageGroupDetail detail) {
        String conditionKey = repeatConditionKey(detail.getRouteType(), detail.getCmcp(), detail.getProvinceCode());
        if (CollectionUtils.isEmpty(distinctAccessKeys)) {
            distinctAccessKeys.add(conditionKey);
            return false;
        }

        if (!distinctAccessKeys.contains(conditionKey)) {
            distinctAccessKeys.add(conditionKey);
            return false;
        }

        return true;
    }

    /**
     * TODO 替换本次新的通道信息至可用通道中（access）
     *
     * @param detail
     * @param userId 用户ID
     * @param passageGroupId 通道组ID
     */
    private void replacePassageValue2Access(SmsPassageGroupDetail detail, Integer userId, Integer passageGroupId) {
        SmsPassage smsPassage = detail.getSmsPassage();

        try {
            List<SmsPassageParameter> parameterList = smsPassageParameterService.findByPassageId(detail.getPassageId());
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

    /**
     * TODO 配装可用通道KEY
     * 
     * @param callType
     * @param passageCode
     * @return
     */
    private static String passageAccessKey(PassageCallType callType, String passageCode) {
        return callType.getCode() + ":" + (StringUtils.isEmpty(passageCode) ? "" : passageCode);
    }

    @Override
    public SmsPassageAccess getByType(PassageCallType callType, String passageCode) {
        String passageAccessKey = passageAccessKey(callType, passageCode);
        if (CODE_TYPE_ACCESS_CONTAINER.containsKey(passageAccessKey)) {
            return CODE_TYPE_ACCESS_CONTAINER.get(passageAccessKey);
        }

        SmsPassageAccess access = smsPassageAccessMapper.getByTypeAndUrl(callType.getCode(), passageCode);
        if (access != null) {
            CODE_TYPE_ACCESS_CONTAINER.put(passageAccessKey, access);
            return access;
        }

        // 获取通道参数信息
        SmsPassageParameter passageParameter = smsPassageParameterService.getByType(callType, passageCode);
        if (passageParameter == null) {
            return null;
        }

        access = copyProperties(passageParameter);
        CODE_TYPE_ACCESS_CONTAINER.put(passageAccessKey, access);

        logger.warn("通道简码： [" + passageCode + "] 调用类型 [" + callType.getName() + "] 可用通道数据为空，参数failover可用");

        return access;
    }

    /**
     * TODO 从通道参数拷贝属性值给可用通道
     * 
     * @param passageParameter
     * @return
     */
    private SmsPassageAccess copyProperties(SmsPassageParameter passageParameter) {
        SmsPassageAccess smsPassageAccess = new SmsPassageAccess();
        smsPassageAccess.setPassageId(passageParameter.getPassageId());
        smsPassageAccess.setProtocol(passageParameter.getProtocol());
        smsPassageAccess.setParams(passageParameter.getParams());
        smsPassageAccess.setSuccessCode(passageParameter.getSuccessCode());
        return smsPassageAccess;
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

                removeFromRedis(access);

                // 如果通道调用类型为 自取，需要同步到 监管中心（停止轮训扫描）
                monitorThreadNotice(access, PassageStatus.HANGUP.getValue());

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

                loadToRedis(access);

                monitorThreadNotice(access, status);
            }

            logger.info("更新可用通道: {} 状态：{} 成功", passageId, status);

            return true;
        } catch (Exception e) {
            logger.error("修改可用通道状态失败，通道ID: {}， 状态：{} 失败信息", passageId, status, e);
            return false;
        }
    }

    /**
     * 
       * TODO 更新监控应用相关自定义线程信息
       * 
       * @param access
       * @param status
     */
    private void monitorThreadNotice(SmsPassageAccess access, Integer status) {
        try {
            // 如果通道调用类型为 自取，需要同步到 监管中心（停止轮训扫描）
            if (PassageCallType.STATUS_RECEIPT_WITH_SELF_GET.getCode() == access.getCallType()
                || PassageCallType.SMS_MO_REPORT_WITH_SELF_GET.getCode() == access.getCallType()) {
                if (PassageStatus.ACTIVE.getValue() == status) {
                    passageMonitorService.addPassagePull(access);
                } else {
                    passageMonitorService.removePasagePull(access);
                }
            }
        } catch (Exception e) {
            logger.warn("更新监听服务线程失败 [" + e.getMessage() +"]");
        }
    }

    @Override
    public Map<String, SmsPassageAccess> getByUserId(int userId) {
        Map<String, SmsPassageAccess> passages = new HashMap<String, SmsPassageAccess>();
        try {
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(getMainKey(userId,
                                                                                              PassageCallType.DATA_SEND.getCode()));
            if (MapUtils.isNotEmpty(entries)) {

                for (Object key : entries.keySet()) {
                    SmsPassageAccess passage = JSON.parseObject(entries.get(key).toString(), SmsPassageAccess.class);
                    passages.put(getAssistKey(passage.getRouteType(), passage.getCmcp(), passage.getProvinceCode()),
                                 passage);
                }

                return passages;
            }

        } catch (Exception e) {
            logger.warn("Redis 用户可用通道信息查询失败, userId : {}, 将在DB获取", userId, e);
            List<SmsPassageAccess> list = smsPassageAccessMapper.selectByUserIdAndCallType(userId,
                                                                                           PassageCallType.DATA_SEND.getCode());
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

            stringRedisTemplate.opsForHash().putAll(getMainKey(userId, PassageCallType.DATA_SEND.getCode()),
                                                    redisPassageValues);

            return passages;
        }

        return null;
    }

    @Override
    public SmsPassageAccess get(int userId, int routeType, int cmcp, int provinceCode) {
        String dataKey = getFullKey(userId, PassageCallType.DATA_SEND.getCode(), routeType, cmcp, provinceCode);
        if (GLOBAL_PASSAGE_ACCESS_CONTAINER.containsKey(dataKey)) {
            return GLOBAL_PASSAGE_ACCESS_CONTAINER.get(dataKey);
        }

        SmsPassageAccess passageAccess = getAccessFromRedis(userId, routeType, cmcp, provinceCode);
        return passageAccess != null ? passageAccess : getAccessFromDb(userId, routeType, cmcp, provinceCode);
    }

    /**
     * TODO 在DB中查询可用通道数据
     * 
     * @param userId
     * @param routeType
     * @param cmcp
     * @param provinceCode
     * @return
     */
    private SmsPassageAccess getAccessFromDb(int userId, int routeType, int cmcp, int provinceCode) {
        String mainKey = getMainKey(userId, PassageCallType.DATA_SEND.getCode());
        String assistKey = getAssistKey(routeType, cmcp, provinceCode);
        try {
            SmsPassageAccess passageAccess = smsPassageAccessMapper.selectByUserIdAndRouteCmcp(userId, routeType, cmcp,
                                                                                               provinceCode);
            if (passageAccess == null) {
                return null;
            }

            GLOBAL_PASSAGE_ACCESS_CONTAINER.put(mainKey + assistKey, passageAccess);

            loadToRedis(passageAccess);
        } catch (Exception e) {
            logger.warn("Redis 用户可用通道信息添加失败, userId : {}, cmcp : {}", userId, cmcp, e);
        }
        return null;
    }

    /**
     * TODO 在redis中查询可用通道
     * 
     * @param userId
     * @param routeType
     * @param cmcp
     * @param provinceCode
     * @return
     */
    private SmsPassageAccess getAccessFromRedis(int userId, int routeType, int cmcp, int provinceCode) {
        SmsPassageAccess passageAccess = null;
        String mainKey = getMainKey(userId, PassageCallType.DATA_SEND.getCode());
        String assistKey = getAssistKey(routeType, cmcp, provinceCode);
        try {
            Object object = stringRedisTemplate.opsForHash().get(mainKey, assistKey);
            if (object != null) {
                passageAccess = JSON.parseObject(object.toString(), SmsPassageAccess.class);
                GLOBAL_PASSAGE_ACCESS_CONTAINER.put(mainKey + assistKey, passageAccess);
                return passageAccess;
            }
        } catch (Exception e) {
            logger.warn("Redis 用户可用通道信息查询失败, userId : {}, cmcp : {}", userId, cmcp, e);
        }
        return null;
    }

    /**
     * TODO 加载到REDIS
     * 
     * @param access
     */
    private void loadToRedis(SmsPassageAccess access) {
        try {
            String mainKey = getMainKey(access.getUserId(), access.getCallType());
            String assistKey = getAssistKey(access.getRouteType(), access.getCmcp(), access.getProvinceCode());

            stringRedisTemplate.opsForHash().put(mainKey, assistKey, JSON.toJSONString(access));
            publishToRedis();
        } catch (Exception e) {
            logger.warn("Redis 用户可用通道信息加载到REDIS失败, userId : {}, cmcp : {}", access.getUserId(), access.getCmcp(), e);
        }
    }

    /**
     * TODO 移除REDIS数据
     * 
     * @param access
     */
    private void removeFromRedis(SmsPassageAccess access) {
        try {
            String mainKey = getMainKey(access.getUserId(), access.getCallType());
            String assistKey = getAssistKey(access.getRouteType(), access.getCmcp(), access.getProvinceCode());

            stringRedisTemplate.opsForHash().delete(mainKey, assistKey);
            publishToRedis();
        } catch (Exception e) {
            logger.warn("Redis 移除可用通道信息加载到REDIS失败, userId : {}, cmcp : {}", access.getUserId(), access.getCmcp(), e);
        }
    }

    /**
     * TODO REDIS队列数据操作(订阅发布模式)
     *
     * @param mobile
     * @param action
     * @return
     */
    private void publishToRedis() {
        try {

            stringRedisTemplate.convertAndSend(SmsRedisConstant.BROADCAST_PASSAGE_ACCESS_TOPIC, "clear");
        } catch (Exception e) {
            logger.error("发布清除JVM可用通道失败", e);
        }
    }

}
