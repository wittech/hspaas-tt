package com.huashi.mms.passage.service;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.notice.service.IMessageSendService;
import com.huashi.common.notice.vo.SmsResponse;
import com.huashi.common.settings.context.SettingsContext.SystemConfigType;
import com.huashi.common.settings.domain.SystemConfig;
import com.huashi.common.settings.service.ISystemConfigService;
import com.huashi.common.user.context.UserContext.UserStatus;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.user.service.IUserPassageService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.constants.CommonContext.ProtocolType;
import com.huashi.constants.OpenApiCode;
import com.huashi.mms.passage.dao.MmsPassageMapper;
import com.huashi.mms.passage.dao.MmsPassageParameterMapper;
import com.huashi.mms.passage.dao.MmsPassageProvinceMapper;
import com.huashi.mms.passage.domain.MmsPassage;
import com.huashi.mms.passage.domain.MmsPassageParameter;
import com.huashi.mms.passage.domain.MmsPassageProvince;
import com.huashi.mms.record.service.IMmsMtSubmitService;
import com.huashi.monitor.passage.service.IPassageMonitorService;

/**
 * TODO 彩信通道服务
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月20日 下午11:34:14
 */
@Service
public class MmsPassageService implements IMmsPassageService {

    @Autowired
    private MmsPassageMapper          mmsPassageMapper;
    @Autowired
    private MmsPassageParameterMapper parameterMapper;
    @Resource
    private StringRedisTemplate       stringRedisTemplate;
    @Autowired
    private MmsPassageProvinceMapper  mmsPassageProvinceMapper;
    @Reference
    private ISystemConfigService      systemConfigService;
    @Reference
    private IUserPassageService       userPassageService;
    @Autowired
    private IMmsPassageGroupService   passageGroupService;
    @Reference
    private IMessageSendService       messageSendService;
    @Reference
    private IUserDeveloperService     userDeveloperService;
    @Autowired
    private IMmsPassageAccessService  mmsPassageAccessService;
    @Autowired
    private IMmsMtSubmitService       mmsMtSubmitService;
    @Reference
    private IPassageMonitorService    passageMonitorService;

    private final Logger              logger       = LoggerFactory.getLogger(getClass());

    /**
     * 非中文表达式
     */
    private static final String       NOT_CHINESS_REGEX = "[0-9A-Za-z_.]*";

    /**
     * 是否是中文字符
     *
     * @param code 编码
     * @return 处理结果
     */
    private static boolean isLetter(String code) {
        if (StringUtils.isEmpty(code)) {
            return false;
        }

        return code.matches(NOT_CHINESS_REGEX);
    }

    private void validate(MmsPassage passage) {
        if (StringUtils.isEmpty(passage.getCode())) {
            throw new IllegalArgumentException("通道代码为空，无法操作");
        }

        if (!isLetter(passage.getCode().trim())) {
            throw new IllegalArgumentException("通道代码不合法[字母|数字|下划线]");
        }

        if (passage.getId() != null) {
            return;
        }

        MmsPassage originPassage = mmsPassageMapper.getPassageByCode(passage.getCode().trim());
        if (originPassage != null) {
            throw new IllegalArgumentException("通道编码 [" + passage.getCode().trim() + "] 已存在，无法添加");
        }
    }

    /**
     * 绑定通道和省份关系记录
     *
     * @param passage 通道
     * @param provinceCodes 省份代码（半角分号分割）
     */
    private void bindPassageProvince(MmsPassage passage, String provinceCodes) {
        if (StringUtils.isEmpty(provinceCodes)) {
            return;
        }

        String[] codeArray = provinceCodes.split(",");
        if (codeArray.length == 0) {
            return;
        }

        for (String code : codeArray) {
            passage.getProvinceList().add(new MmsPassageProvince(passage.getId(), Integer.valueOf(code)));
        }

        if (CollectionUtils.isNotEmpty(passage.getProvinceList())) {
            mmsPassageProvinceMapper.batchInsert(passage.getProvinceList());
        }
    }

    /**
     * 绑定通道参数信息
     *
     * @param passage 通道
     * @param isModify 是否为修改模式
     */
    private boolean bindPassageParameters(MmsPassage passage, boolean isModify) {
        if (CollectionUtils.isEmpty(passage.getParameterList())) {
            return false;
        }

        String passageSendProtocol = null;
        for (MmsPassageParameter parameter : passage.getParameterList()) {
            parameter.setPassageId(passage.getId());
            parameter.setCreateTime(new Date());
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
            return mmsMtSubmitService.declareNewSubmitMessageQueue(passageSendProtocol, passage.getCode());
        }

        return false;
    }

    /**
     * 释放已经产生的无用资源，如销毁队列和移除REDIS数据
     *
     * @param passage 通道
     * @param isQueueCreateFinished 队列是否创建完成
     * @param isRedisPushFinished 是否发送至redis
     */
    private void release(MmsPassage passage, boolean isQueueCreateFinished, boolean isRedisPushFinished) {
        if (isQueueCreateFinished) {
            smsMtSubmitService.removeSubmitMessageQueue(passage.getCode().trim());
        }

        if (isRedisPushFinished) {
            removeFromRedis(passage.getId());
        }
    }

    @Override
    @Transactional
    public Map<String, Object> create(MmsPassage passage, String provinceCodes) {
        boolean isQueueCreateFinished = false;
        boolean isRedisPushFinished = false;
        try {
            validate(passage);

            int rows = mmsPassageMapper.insert(passage);
            if (rows == 0) {
                return response(false, "数据操作异常");
            }

            isQueueCreateFinished = bindPassageParameters(passage, false);

            bindPassageProvince(passage, provinceCodes);

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
     * 更新通道信息
     *
     * @param passage 通道
     * @return 更新结果
     */
    private boolean updatePassage(MmsPassage passage) {
        MmsPassage originPassage = findById(passage.getId());
        if (originPassage == null) {
            throw new IllegalArgumentException("通道数据不存在");
        }

        passage.setStatus(originPassage.getStatus());
        passage.setCreateTime(originPassage.getCreateTime());
        passage.setModifyTime(new Date());

        // 更新通道信息
        return mmsPassageMapper.updateByPrimaryKey(passage) > 0;
    }

    @Override
    @Transactional
    public Map<String, Object> update(MmsPassage passage, String provinceCodes) {
        try {

            validate(passage);

            boolean isSuccess = updatePassage(passage);
            if (!isSuccess) {
                return response(false, "更新通道失败");
            }

            // 绑定通道参数信息
            bindPassageParameters(passage, true);

            // 绑定省份通道关系
            bindPassageProvince(passage, provinceCodes);

            // 更新可用通道信息
            mmsPassageAccessService.updateByModifyPassage(passage.getId());

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
     * 拼接返回值
     *
     * @param result 处理结果
     * @param msg 消息
     * @return 结果
     */
    private Map<String, Object> response(boolean result, String msg) {
        Map<String, Object> report = new HashMap<>();
        report.put("result", result);
        report.put("message", msg);
        return report;
    }

    @Override
    @Transactional
    public boolean deleteById(int id) {
        try {
            MmsPassage passage = mmsPassageMapper.selectByPrimaryKey(id);
            if (passage == null) {
                throw new RuntimeException("查询通道ID：" + id + "数据为空");
            }

            int result = mmsPassageMapper.deleteByPrimaryKey(id);
            if (result == 0) {
                throw new RuntimeException("删除通道失败");
            }

            result = parameterMapper.deleteByPassageId(id);
            if (result == 0) {
                throw new RuntimeException("删除通道参数失败");
            }

            result = mmsPassageProvinceMapper.deleteByPassageId(id);
            if (result == 0) {
                throw new RuntimeException("删除通道省份关系数据失败");
            }

            boolean isOk = mmsPassageAccessService.deletePassageAccess(id);
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
            MmsPassage passage = new MmsPassage();
            passage.setId(passageId);
            passage.setStatus(status);
            int result = mmsPassageMapper.updateByPrimaryKeySelective(passage);
            if (result == 0) {
                throw new RuntimeException("更新通道状态失败");
            }

            // 更新REDIS资源数据
            reloadMmsPassageInRedis(passageId, status);

            // edit by 20180609 禁用/启用都需要重新筛查通道组相关信息（首选通道，备用通道需要及时切换回来）
            boolean isOk = mmsPassageAccessService.updateByModifyPassage(passageId);
            if (!isOk) {
                throw new RuntimeException("更新可用通道状态失败");
            }

            logger.info("更新通道：{} 状态：{} 成功", passageId, status);
            return true;

        } catch (Exception e) {
            logger.error("通道: {} 状态修改失败：{} 失败", passageId, status, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public BossPaginationVo<MmsPassage> findPage(int pageNum, String keyword) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keyword", keyword);
        BossPaginationVo<MmsPassage> page = new BossPaginationVo<>();
        page.setCurrentPage(pageNum);
        int total = mmsPassageMapper.findCount(paramMap);
        if (total <= 0) {
            return page;
        }
        page.setTotalCount(total);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());
        List<MmsPassage> dataList = mmsPassageMapper.findList(paramMap);
        page.getList().addAll(dataList);
        return page;
    }

    @Override
    public List<MmsPassage> findAll() {
        try {
            Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(SmsRedisConstant.RED_SMS_PASSAGE);
            if (MapUtils.isNotEmpty(map)) {
                List<MmsPassage> passages = new ArrayList<>();

                map.forEach((k, v) -> {

                    MmsPassage mmsPassage = JSON.parseObject(v.toString(), MmsPassage.class);
                    if (passages.contains(mmsPassage)) {
                        return;
                    }

                    passages.add(mmsPassage);
                });
                return passages;
            }
        } catch (Exception e) {
            logger.warn("通道REDIS加载出错 {}", e.getMessage());
        }

        return mmsPassageMapper.findAll();
    }

    @Override
    public MmsPassage findById(int id) {
        MmsPassage mmsPassage = null;
        try {
            Object obj = stringRedisTemplate.opsForHash().get(SmsRedisConstant.RED_SMS_PASSAGE, id + "");
            if (obj != null) {
                mmsPassage = JSON.parseObject(obj.toString(), MmsPassage.class);
            }
        } catch (Exception e) {
            logger.warn("REDIS 加载失败，将于DB加载", e);
        }

        if (mmsPassage == null) {
            mmsPassage = mmsPassageMapper.selectByPrimaryKey(id);
        }

        setPassageParamsIfEmpty(mmsPassage);

        return mmsPassage;
    }

    /**
     * 设置通道参数集合信息
     *
     * @param mmsPassage 通道
     */
    private void setPassageParamsIfEmpty(MmsPassage mmsPassage) {
        if (mmsPassage == null || mmsPassage.getId() == null) {
            return;
        }

        if (CollectionUtils.isNotEmpty(mmsPassage.getParameterList())) {
            return;
        }

        mmsPassage.getParameterList().addAll(parameterMapper.findByPassageId(mmsPassage.getId()));

    }

    @Override
    public List<MmsPassage> findByGroupId(int groupId) {
        return mmsPassageMapper.selectByGroupId(groupId);
    }

    @Override
    public MmsPassage getBestAvaiable(int groupId) {
        List<MmsPassage> list = findByGroupId(groupId);

        // 此逻辑需要结合REDIS判断

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        MmsPassage mmsPassage = list.iterator().next();

        setPassageParamsIfEmpty(mmsPassage);

        return CollectionUtils.isEmpty(list) ? null : mmsPassage;
    }

    @Override
    public List<MmsPassage> getByCmcp(int cmcp) {
        return mmsPassageMapper.getByCmcp(cmcp);
    }

    @Override
    public List<MmsPassage> findAccessPassages(int groupId, int cmcp, int routeType) {
        // 0代表可用状态
        return mmsPassageMapper.selectAvaiablePassages(groupId, cmcp, routeType, 0);
    }

    @Override
    public List<MmsPassage> findByCmcpOrAll(int cmcp) {
        return mmsPassageMapper.findByCmcpOrAll(cmcp);
    }

    @Override
    public boolean reloadToRedis() {
        List<MmsPassage> list = mmsPassageMapper.findAll();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("短信通道数据为空");
            return false;
        }

        List<Object> con = stringRedisTemplate.execute((connection) -> {
            RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
            connection.openPipeline();
            for (MmsPassage mmsPassage : list) {

                byte[] mainKey = serializer.serialize(SmsRedisConstant.RED_SMS_PASSAGE);
                byte[] assistKey = serializer.serialize(mmsPassage.getId().toString());

                connection.hSet(mainKey, assistKey, serializer.serialize(JSON.toJSONString(mmsPassage)));
            }

            return connection.closePipeline();
        }, false, true);

        return CollectionUtils.isNotEmpty(con);
    }

    @Override
    public List<MmsPassageProvince> getPassageProvinceById(Integer passageId) {
        return mmsPassageProvinceMapper.getListByPassageId(passageId);
    }

    @Override
    public List<MmsPassage> getByProvinceAndCmcp(Integer provinceCode, int cmcp) {
        return mmsPassageMapper.getByProvinceAndCmcp(provinceCode, cmcp);
    }

    private boolean pushToRedis(MmsPassage mmsPassage) {
        try {
            stringRedisTemplate.opsForHash().put(SmsRedisConstant.RED_SMS_PASSAGE, mmsPassage.getId() + "",
                    JSON.toJSONString(mmsPassage));
            return true;
        } catch (Exception e) {
            logger.warn("REDIS 加载短信通道[" + JSON.toJSONString(mmsPassage) + "]数据失败", e);
            return false;
        }
    }

    private void removeFromRedis(Integer passageId) {
        try {
            stringRedisTemplate.opsForHash().delete(SmsRedisConstant.RED_SMS_PASSAGE, passageId + "");
        } catch (Exception e) {
            logger.warn("REDIS 删除短信通道[" + passageId + "]数据失败", e);
        }
    }

    /**
     * 更新REDIS 通道的状态信息
     *
     * @param passageId 通道ID
     * @param status 状态
     */
    private void reloadMmsPassageInRedis(int passageId, int status) {
        try {
            MmsPassage mmsPassage = findById(passageId);
            if (mmsPassage != null) {
                mmsPassage.setStatus(status);
                stringRedisTemplate.opsForHash().put(SmsRedisConstant.RED_SMS_PASSAGE, passageId + "",
                        JSON.toJSONString(mmsPassage));
            }

        } catch (Exception e) {
            logger.warn("REDIS 加载短信通道数据失败", e);
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
            SmsResponse response = messageSendService.sendCustomMessage(developer.getAppKey(), developer.getAppSecret(),
                    mobile, content);
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
            result = mmsPassageAccessService.updateByModifyPassageGroup(passageGroupId);
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
            SmsResponse response = messageSendService.sendCustomMessage(developer.getAppKey(), developer.getAppSecret(),
                    mobile, content);
            return OpenApiCode.SUCCESS.equals(response.getCode());
        } catch (Exception e) {
            logger.error("通道测试逻辑失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public List<String> findPassageCodes() {
        return mmsPassageMapper.selectAvaiableCodes();
    }

    @Override
    public boolean isPassageBelongtoDirect(String protocol, String passageCode) {
        if (StringUtils.isNotEmpty(protocol)) {
            return ProtocolType.isBelongtoDirect(protocol);
        }

        MmsPassage passage = mmsPassageMapper.getPassageByCode(passageCode.trim());
        if (passage == null) {
            return false;
        }

        MmsPassageParameter parameter = parameterMapper.selectSendProtocol(passage.getId());
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
