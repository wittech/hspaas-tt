package com.huashi.sms.passage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.notice.service.IMessageSendService;
import com.huashi.common.notice.vo.SmsResponse;
import com.huashi.common.settings.context.SettingsContext;
import com.huashi.common.settings.context.SettingsContext.SystemConfigType;
import com.huashi.common.settings.domain.SystemConfig;
import com.huashi.common.settings.service.ISystemConfigService;
import com.huashi.common.user.context.UserContext.UserStatus;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.user.service.IUserPassageService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.CommonContext.ProtocolType;
import com.huashi.constants.OpenApiCode;
import com.huashi.exchanger.service.ISmsProxyManager;
import com.huashi.monitor.passage.service.IPassageMonitorService;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.passage.dao.SmsPassageMapper;
import com.huashi.sms.passage.dao.SmsPassageParameterMapper;
import com.huashi.sms.passage.dao.SmsPassageProvinceMapper;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.passage.domain.SmsPassageProvince;
import com.huashi.sms.record.service.ISmsMtSubmitService;

/**
 * @author ym
 * @created_at 2016年8月25日下午5:25:06
 */
@Service
public class SmsPassageService implements ISmsPassageService {

    @Autowired
    private SmsPassageMapper          smsPassageMapper;
    @Autowired
    private SmsPassageParameterMapper parameterMapper;
    @Resource
    private StringRedisTemplate       stringRedisTemplate;
    @Autowired
    private SmsPassageProvinceMapper  smsPassageProvinceMapper;
    @Reference
    private ISystemConfigService      systemConfigService;
    @Reference
    private IUserPassageService       userPassageService;
    @Autowired
    private ISmsPassageGroupService   passageGroupService;
    @Reference
    private IMessageSendService       messageSendService;
    @Reference
    private IUserDeveloperService     userDeveloperService;
    @Autowired
    private ISmsPassageAccessService  smsPassageAccessService;
    @Autowired
    private ISmsMtSubmitService       smsMtSubmitService;
    @Reference
    private ISmsProxyManager          smsProxyManageService;
    @Reference
    private IPassageMonitorService    passageMonitorService;

    private Logger                    logger       = LoggerFactory.getLogger(getClass());

    /**
     * 非中文表达式
     */
    private static final String       LETTER_REGEX = "[0-9A-Za-z_.]*";

    /**
     * TODO 是否是字符
     * 
     * @param code
     * @return
     */
    private static boolean isLetter(String code) {
        if (StringUtils.isEmpty(code)) {
            return false;
        }

        return code.matches(LETTER_REGEX);
    }

    private void validate(SmsPassage passage) throws Exception {
        if (StringUtils.isEmpty(passage.getCode())) {
            throw new IllegalArgumentException("通道代码为空，无法操作");
        }

        if (!isLetter(passage.getCode().trim())) {
            throw new IllegalArgumentException("通道代码不合法[字母|数字|下划线]");
        }

        if (passage.getId() != null) {
            return;
        }

        SmsPassage originPassage = smsPassageMapper.getPassageByCode(passage.getCode().trim());
        if (originPassage != null) {
            throw new IllegalArgumentException("通道编码 [" + passage.getCode().trim() + "] 已存在，无法添加");
        }
    }

    /**
     * TODO 绑定通道和省份关系记录
     * 
     * @param passage
     * @param provinceCodes
     * @param isModify 是否为修改模式
     */
    private void bindPassageProvince(SmsPassage passage, String provinceCodes, boolean isModify) {
        if (StringUtils.isEmpty(provinceCodes)) {
            return;
        }

        String[] codeArray = provinceCodes.split(",");
        if (codeArray.length == 0) {
            return;
        }

        for (String code : codeArray) {
            passage.getProvinceList().add(new SmsPassageProvince(passage.getId(), Integer.valueOf(code)));
        }

        if (CollectionUtils.isNotEmpty(passage.getProvinceList())) {
            if (isModify) {
                smsPassageProvinceMapper.deleteByPassageId(passage.getId());
            }

            smsPassageProvinceMapper.batchInsert(passage.getProvinceList());
        }
    }

    /**
     * TODO 绑定通道参数信息
     * 
     * @param passage
     * @param isModify 是否为修改模式
     */
    private boolean bindPassageParameters(SmsPassage passage, boolean isModify) {
        if (CollectionUtils.isEmpty(passage.getParameterList())) {
            return false;
        }

        String passageSendProtocol = null;
        for (SmsPassageParameter parameter : passage.getParameterList()) {
            parameter.setPassageId(passage.getId());
            parameter.setCreateTime(new Date());

            if (passageSendProtocol != null) {
                continue;
            }

            if (PassageCallType.DATA_SEND.getCode() == parameter.getCallType()) {
                passageSendProtocol = parameter.getProtocol();

                // add by zhengying 20170502 针对通道类型为CMPP等协议类型需要创建PROXY
                startProxyIfMatched(parameter, passage.getPacketsSize());
            }
        }

        if (isModify) {
            parameterMapper.deleteByPassageId(passage.getId());
        }

        int rows = parameterMapper.batchInsert(passage.getParameterList());
        if (isModify && rows > 0) {
            return true;
        }

        if (rows > 0) {
            // add by zhengying 20170319 每个通道单独分开 提交队列
            return smsMtSubmitService.declareNewSubmitMessageQueue(passageSendProtocol, passage.getCode());
        }

        return false;
    }

    /**
     * TODO 释放已经产生的无用资源，如销毁队列和移除REDIS数据
     * 
     * @param passage
     * @param isQueueCreateFinished
     * @param isRedisPushFinished
     */
    private void release(SmsPassage passage, boolean isQueueCreateFinished, boolean isRedisPushFinished) {
        if (isQueueCreateFinished) {
            smsMtSubmitService.removeSubmitMessageQueue(passage.getCode().trim());
        }

        if (isRedisPushFinished) {
            removeFromRedis(passage.getId());
        }
    }

    @Override
    @Transactional
    public Map<String, Object> create(SmsPassage passage, String provinceCodes) {
        boolean isQueueCreateFinished = false;
        boolean isRedisPushFinished = false;
        try {
            validate(passage);

            int rows = smsPassageMapper.insert(passage);
            if (rows == 0) {
                return response(false, "数据操作异常");
            }

            isQueueCreateFinished = bindPassageParameters(passage, false);

            bindPassageProvince(passage, provinceCodes, false);

            isRedisPushFinished = pushToRedis(passage);

            return response(true, "添加成功");

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                return response(false, e.getMessage());
            }

            release(passage, isQueueCreateFinished, isRedisPushFinished);

            logger.error("添加短信通道[{}], provinceCodes[{}] 失败", JSON.toJSONString(passage), provinceCodes, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return response(false, "添加失败");
        }
    }

    /**
     * TODO 加载通道代理信息
     * 
     * @param parameter
     * @param packetsSize
     * @return
     */
    private boolean startProxyIfMatched(SmsPassageParameter parameter, Integer packetsSize) {
        try {
            if (parameter.getCallType() != PassageCallType.DATA_SEND.getCode()) {
                return true;
            }

            if (StringUtils.isEmpty(parameter.getProtocol())) {
                return true;
            }

            // 是否需要出发通道代理逻辑(目前主要针对CMPP,SGIP,SGMP等直连协议),通道未使用中不无加载,后续会延迟加载(发短信逻辑初始发现无代理会初始化相关代理)
            if (!smsProxyManageService.isProxyAvaiable(parameter.getPassageId())) {
                return true;
            }

            // 重新刷新代理信息
            parameter.setPacketsSize(packetsSize);
            smsProxyManageService.startProxy(parameter);

            return true;
        } catch (Exception e) {
            logger.error("加载通道代理失败", e);
            return false;
        }
    }

    /**
     * TODO 更新通道信息
     * 
     * @param passage
     * @return
     */
    private boolean updatePassage(SmsPassage passage) {
        SmsPassage originPassage = findById(passage.getId());
        if (originPassage == null) {
            throw new IllegalArgumentException("通道数据不存在");
        }

        passage.setStatus(originPassage.getStatus());
        passage.setCreateTime(originPassage.getCreateTime());
        passage.setModifyTime(new Date());

        // 更新通道信息
        return smsPassageMapper.updateByPrimaryKey(passage) > 0;
    }

    @Override
    @Transactional
    public Map<String, Object> update(SmsPassage passage, String provinceCodes) {
        try {

            validate(passage);

            boolean isSuccess = updatePassage(passage);
            if (!isSuccess) {
                return response(false, "更新通道失败");
            }

            // 绑定通道参数信息
            bindPassageParameters(passage, true);

            // 绑定省份通道关系
            bindPassageProvince(passage, provinceCodes, false);

            // 更新可用通道信息
            smsPassageAccessService.updateByModifyPassage(passage.getId());

            pushToRedis(passage);

            return response(true, "修改成功");
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                return response(false, e.getMessage());
            }

            logger.error("修改短信通道[{}], provinceCodes[{}] 失败", JSON.toJSONString(passage), provinceCodes, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return response(false, "修改失败");
        }
    }

    /**
     * TODO 拼接返回值
     * 
     * @param result
     * @param msg
     * @return
     */
    private Map<String, Object> response(boolean result, String msg) {
        Map<String, Object> report = new HashMap<String, Object>();
        report.put("result", result);
        report.put("message", msg);
        return report;
    }

    @Override
    @Transactional
    public boolean deleteById(int id) {
        try {
            SmsPassage passage = smsPassageMapper.selectByPrimaryKey(id);
            if (passage == null) {
                throw new RuntimeException("查询通道ID：" + id + "数据为空");
            }

            int result = smsPassageMapper.deleteByPrimaryKey(id);
            if (result == 0) {
                throw new RuntimeException("删除通道失败");
            }

            result = parameterMapper.deleteByPassageId(id);
            if (result == 0) {
                throw new RuntimeException("删除通道参数失败");
            }

            result = smsPassageProvinceMapper.deleteByPassageId(id);
            if (result == 0) {
                throw new RuntimeException("删除通道省份关系数据失败");
            }

            boolean isOk = smsPassageAccessService.deletePassageAccess(id);
            if (!isOk) {
                throw new RuntimeException("删除可用通道失败");
            }

            smsMtSubmitService.removeSubmitMessageQueue(passage.getCode());

            return true;
        } catch (Exception e) {
            logger.error("删除通道：{} 信息失败", id, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return false;
    }

    @Override
    @Transactional
    public boolean disabledOrActive(int passageId, int status) {
        try {
            SmsPassage passage = new SmsPassage();
            passage.setId(passageId);
            passage.setStatus(status);
            int result = smsPassageMapper.updateByPrimaryKeySelective(passage);
            if (result == 0) {
                throw new RuntimeException("更新通道状态失败");
            }

            // 更新REDIS资源数据
            updateSmsPassageStatus(passageId, status);

            // edit by 20180609 禁用/启用都需要重新筛查通道组相关信息（首选通道，备用通道需要及时切换回来）
            boolean isOk = smsPassageAccessService.updateByModifyPassage(passageId);
            if (!isOk) {
                throw new RuntimeException("更新可用通道状态失败");
            }

            logger.info("更新通道：{} 状态：{} 成功", passageId, status);
            return isOk;

        } catch (Exception e) {
            logger.error("通道: {} 状态修改失败：{} 失败", passageId, status, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public BossPaginationVo<SmsPassage> findPage(int pageNum, String keyword) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("keyword", keyword);
        BossPaginationVo<SmsPassage> page = new BossPaginationVo<SmsPassage>();
        page.setCurrentPage(pageNum);
        int total = smsPassageMapper.findCount(paramMap);
        if (total <= 0) {
            return page;
        }
        page.setTotalCount(total);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());
        List<SmsPassage> dataList = smsPassageMapper.findList(paramMap);
        page.getList().addAll(dataList);
        return page;
    }

    @Override
    public List<SmsPassage> findAll() {
        try {
            Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(SmsRedisConstant.RED_SMS_PASSAGE);
            if (MapUtils.isNotEmpty(map)) {
                List<SmsPassage> passages = new ArrayList<>();

                map.forEach((k, v) -> {

                    SmsPassage smsPassage = JSON.parseObject(v.toString(), SmsPassage.class);
                    if (passages.contains(smsPassage)) {
                        return;
                    }

                    passages.add(smsPassage);
                });
                return passages;
            }
        } catch (Exception e) {
            logger.warn("通道REDIS加载出错 {}", e.getMessage());
        }

        return smsPassageMapper.findAll();
    }

    @Override
    public SmsPassage findById(int id) {
        try {
            Object obj = stringRedisTemplate.opsForHash().get(SmsRedisConstant.RED_SMS_PASSAGE, id + "");
            if (obj != null) {
                return JSON.parseObject(obj.toString(), SmsPassage.class);
            }
        } catch (Exception e) {
            logger.warn("REDIS 加载失败，将于DB加载", e);
        }

        SmsPassage passage = smsPassageMapper.selectByPrimaryKey(id);
        if (passage != null) {
            List<SmsPassageParameter> parameterList = parameterMapper.findByPassageId(id);
            passage.getParameterList().addAll(parameterList);
        }
        return passage;
    }

    @Override
    public List<SmsPassage> findByGroupId(int groupId) {
        return smsPassageMapper.selectByGroupId(groupId);
    }

    @Override
    public SmsPassage getBestAvaiable(int groupId) {
        List<SmsPassage> list = findByGroupId(groupId);

        // 此逻辑需要结合REDIS判断

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        SmsPassage passage = list.iterator().next();
        passage.setParameterList(parameterMapper.findByPassageId(passage.getId()));

        return CollectionUtils.isEmpty(list) ? null : passage;
    }

    @Override
    public List<SmsPassage> getByCmcp(int cmcp) {
        return smsPassageMapper.getByCmcp(cmcp);
    }

    @Override
    public List<SmsPassage> findAccessPassages(int groupId, int cmcp, int routeType) {
        // 0代表可用状态
        return smsPassageMapper.selectAvaiablePassages(groupId, cmcp, routeType, 0);
    }

    @Override
    public List<SmsPassage> findByCmcpOrAll(int cmcp) {
        return smsPassageMapper.findByCmcpOrAll(cmcp);
    }

    @Override
    public boolean reloadToRedis() {
        List<SmsPassage> list = smsPassageMapper.findAll();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("短信通道数据为空");
            return false;
        }

        List<Object> con = stringRedisTemplate.execute(new RedisCallback<List<Object>>() {

            @Override
            public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
                connection.openPipeline();
                for (SmsPassage smsPassage : list) {
                    byte[] mainKey = serializer.serialize(SmsRedisConstant.RED_SMS_PASSAGE);
                    byte[] assistKey = serializer.serialize(smsPassage.getId().toString());

                    connection.hSet(mainKey, assistKey, serializer.serialize(JSON.toJSONString(smsPassage)));
                }

                return connection.closePipeline();
            }

        }, false, true);

        return CollectionUtils.isNotEmpty(con);
    }

    @Override
    public List<SmsPassageProvince> getPassageProvinceById(Integer passageId) {
        return smsPassageProvinceMapper.getListByPassageId(passageId);
    }

    @Override
    public List<SmsPassage> getByProvinceAndCmcp(Integer provinceCode, int cmcp) {
        return smsPassageMapper.getByProvinceAndCmcp(provinceCode, cmcp);
    }

    private boolean pushToRedis(SmsPassage smsPassage) {
        try {
            stringRedisTemplate.opsForHash().put(SmsRedisConstant.RED_SMS_PASSAGE, smsPassage.getId() + "",
                                                 JSON.toJSONString(smsPassage));
            return true;
        } catch (Exception e) {
            logger.warn("REDIS 加载短信通道[" + JSON.toJSONString(smsPassage) + "]数据失败", e);
            return false;
        }
    }

    private boolean removeFromRedis(Integer passageId) {
        try {
            stringRedisTemplate.opsForHash().delete(SmsRedisConstant.RED_SMS_PASSAGE, passageId + "");
            return true;
        } catch (Exception e) {
            logger.warn("REDIS 删除短信通道[" + passageId + "]数据失败", e);
            return false;
        }
    }

    /**
     * TODO 更新REDIS 通道的状态信息
     * 
     * @param passageId
     * @param status
     * @return
     */
    private boolean updateSmsPassageStatus(int passageId, int status) {
        try {
            SmsPassage smsPassage = null;
            Object object = stringRedisTemplate.opsForHash().get(SmsRedisConstant.RED_SMS_PASSAGE, passageId + "");
            if (object != null) {
                smsPassage = JSON.parseObject(object.toString(), SmsPassage.class);
            } else {
                smsPassage = findById(passageId);
            }

            if (smsPassage != null) {
                smsPassage.setStatus(status);
                stringRedisTemplate.opsForHash().put(SmsRedisConstant.RED_SMS_PASSAGE, smsPassage.getId() + "",
                                                     JSON.toJSONString(smsPassage));
                return true;
            }

            logger.error("REDIS 更新短信通道数据失败");
            return false;

        } catch (Exception e) {
            logger.warn("REDIS 加载短信通道数据失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean doMonitorSmsSend(String mobile, String content) {
        SystemConfig systemConfig = systemConfigService.findByTypeAndKey(SystemConfigType.SMS_ALARM_USER.name(),
                                                                         "user_id");
        if (systemConfig == null) {
            logger.error("告警用户未配置，请于系统配置表进行配置");
            return false;
        }

        String userId = systemConfig.getAttrValue();
        if (StringUtils.isEmpty(userId)) {
            logger.error("告警用户数据为空，请配置");
            return false;
        }

        try {
            // 根据用户ID获取开发者相关信息
            UserDeveloper developer = userDeveloperService.getByUserId(Integer.parseInt(userId));
            if (developer == null) {
                logger.error("用户ID：{}，开发者信息为空", userId);
                return false;
            }

            // 如果用户无效则报错
            if (UserStatus.YES.getValue() != developer.getStatus()) {
                logger.error("用户ID：{}，开发者信息状态[" + developer.getStatus() + "]无效", userId);
                return false;
            }

            // 调用发送短信接口
            SmsResponse response = messageSendService.sendCustomMessage(developer.getAppKey(),
                                                                        developer.getAppSecret(), mobile, content);
            return OpenApiCode.SUCCESS.equals(response.getCode());
        } catch (Exception e) {
            logger.error("通道告警逻辑失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean doTestPassage(Integer passageId, String mobile, String content) {
        SystemConfig systemConfig = systemConfigService.findByTypeAndKey(SystemConfigType.PASSAGE_TEST_USER.name(),
                                                                         SettingsContext.USER_ID_KEY_NAME);
        if (systemConfig == null) {
            logger.error("通道测试用户未配置，请于系统配置表进行配置");
            return false;
        }

        String userId = systemConfig.getAttrValue();
        if (StringUtils.isEmpty(userId)) {
            logger.error("通道测试用户数据为空，请配置");
            return false;
        }

        try {
            Integer passageGroupId = userPassageService.getByUserIdAndType(Integer.parseInt(userId),
                                                                           PlatformType.SEND_MESSAGE_SERVICE.getCode());
            if (passageGroupId == null) {
                logger.error("通道测试用户未配置短信通道组信息");
                return false;
            }

            boolean result = passageGroupService.doChangeGroupPassage(passageGroupId, passageId);
            if (!result) {
                logger.error("通道组ID：{}，切换通道ID：{} 失败", passageGroupId, passageId);
                return false;
            }

            // 更新通道组下 的可用通道相关
            result = smsPassageAccessService.updateByModifyPassageGroup(passageGroupId);
            if (!result) {
                logger.error("通道组ID：{}，切换可用通道失败", passageGroupId);
                return false;
            }

            // 根据用户ID获取开发者相关信息
            UserDeveloper developer = userDeveloperService.getByUserId(Integer.parseInt(userId));
            if (developer == null) {
                logger.error("用户ID：{}，开发者信息为空", userId);
                return false;
            }

            // 调用发送短信接口
            SmsResponse response = messageSendService.sendCustomMessage(developer.getAppKey(),
                                                                        developer.getAppSecret(), mobile, content);
            return OpenApiCode.SUCCESS.equals(response.getCode());
        } catch (Exception e) {
            logger.error("通道测试逻辑失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public List<String> findPassageCodes() {
        return smsPassageMapper.selectAvaiableCodes();
    }

    @Override
    public boolean isPassageBelongtoDirect(String protocol, String passageCode) {
        if (StringUtils.isNotEmpty(protocol)) {
            if (ProtocolType.isBelongtoDirect(protocol)) {
                return true;
            }

            return false;
        }

        SmsPassage passage = smsPassageMapper.getPassageByCode(passageCode.trim());
        if (passage == null) {
            return false;
        }

        SmsPassageParameter parameter = parameterMapper.selectSendProtocol(passage.getId());
        if (parameter == null) {
            return false;
        }

        return ProtocolType.isBelongtoDirect(parameter.getProtocol());
    }

    @Override
    public boolean kill(int id) {
        try {
            // 是否需要出发通道代理逻辑(目前主要针对CMPP,SGIP,SGMP等直连协议)
            if (smsProxyManageService.isProxyAvaiable(id)) {
                if (!smsProxyManageService.stopProxy(id)) {
                    logger.error("通道 [" + id + "] 断开连接失败");
                    return false;
                }
            } else {
                logger.info("通道 [" + id + "] 连接不存在，忽略断开连接操作");
                return true;
            }

            logger.info("通道 [" + id + "] 断开连接成功");
            return true;

        } catch (Exception e) {
            logger.error("通道 [" + id + "] 断开连接操作失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }
}
