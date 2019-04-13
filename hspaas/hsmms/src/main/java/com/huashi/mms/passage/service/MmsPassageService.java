package com.huashi.mms.passage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import com.huashi.mms.config.cache.redis.constant.MmsRedisConstant;
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

    private void validate(MmsPassage passage) throws Exception {
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
     * TODO 绑定通道和省份关系记录
     * 
     * @param passage
     * @param provinceCodes
     * @param isModify 是否为修改模式
     */
    private void bindPassageProvince(MmsPassage passage, String provinceCodes, boolean isModify) {
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
            if (isModify) {
                mmsPassageProvinceMapper.deleteByPassageId(passage.getId());
            }

            mmsPassageProvinceMapper.batchInsert(passage.getProvinceList());
        }
    }

    /**
     * TODO 绑定通道参数信息
     * 
     * @param passage
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

            if (passageSendProtocol != null) {
                continue;
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
            return mmsMtSubmitService.declareNewSubmitMessageQueue(passageSendProtocol, passage.getCode());
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
    private void release(MmsPassage passage, boolean isQueueCreateFinished, boolean isRedisPushFinished) {
        if (isQueueCreateFinished) {
            mmsMtSubmitService.removeSubmitMessageQueue(passage.getCode().trim());
        }

        if (isRedisPushFinished) {
            removeFromRedis(passage.getId());
        }
    }

    @Override
    @Transactional
    public Map<String, Object> add(MmsPassage passage, String provinceCodes) {
        boolean isQueueCreateFinished = false;
        boolean isRedisPushFinished = false;
        try {
            validate(passage);

            int rows = mmsPassageMapper.insert(passage);
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
            
            logger.error("添加彩信通道[{}], provinceCodes[{}] 失败", JSON.toJSONString(passage), provinceCodes, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return response(false, "添加失败");
        }
    }

    /**
     * TODO 更新通道信息
     * 
     * @param passage
     * @return
     */
    private boolean updatePassage(MmsPassage passage) {
        MmsPassage originPassage = findById(passage.getId());
        if (originPassage == null) {
            throw new IllegalArgumentException("通道数据不存在");
        }

        passage.setStatus(originPassage.getStatus());
        passage.setCreateTime(originPassage.getCreateTime());
        passage.setUpdateTime(new Date());

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
            bindPassageProvince(passage, provinceCodes, false);

            // 更新可用通道信息
            mmsPassageAccessService.updateByModifyPassage(passage.getId());

            pushToRedis(passage);

            return response(true, "修改成功");
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                return response(false, e.getMessage());
            }

            logger.error("修改彩信通道[{}], provinceCodes[{}] 失败", JSON.toJSONString(passage), provinceCodes, e);
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

            mmsMtSubmitService.removeSubmitMessageQueue(passage.getCode());

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
            updateMmsPassageStatus(passageId, status);

            // edit by 20180609 禁用/启用都需要重新筛查通道组相关信息（首选通道，备用通道需要及时切换回来）
            boolean isOk = mmsPassageAccessService.updateByModifyPassage(passageId);
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
    public BossPaginationVo<MmsPassage> findPage(int pageNum, String keyword) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("keyword", keyword);
        BossPaginationVo<MmsPassage> page = new BossPaginationVo<MmsPassage>();
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
            Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(MmsRedisConstant.RED_MMS_PASSAGE);
            if (MapUtils.isNotEmpty(map)) {
                List<MmsPassage> passages = new ArrayList<>();

                map.forEach((k, v) -> {

                    MmsPassage smsPassage = JSON.parseObject(v.toString(), MmsPassage.class);
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

        return mmsPassageMapper.findAll();
    }

    @Override
    public MmsPassage findById(int id) {
        try {
            Object obj = stringRedisTemplate.opsForHash().get(MmsRedisConstant.RED_MMS_PASSAGE, id + "");
            if (obj != null) {
                return JSON.parseObject(obj.toString(), MmsPassage.class);
            }
        } catch (Exception e) {
            logger.warn("REDIS 加载失败，将于DB加载", e);
        }

        MmsPassage passage = mmsPassageMapper.selectByPrimaryKey(id);
        if (passage != null) {
            List<MmsPassageParameter> parameterList = parameterMapper.findByPassageId(id);
            passage.getParameterList().addAll(parameterList);
        }
        return passage;
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

        MmsPassage passage = list.iterator().next();
        passage.setParameterList(parameterMapper.findByPassageId(passage.getId()));

        return CollectionUtils.isEmpty(list) ? null : passage;
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
    @PostConstruct
    public boolean reloadToRedis() {
        List<MmsPassage> list = mmsPassageMapper.findAll();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("彩信通道数据为空");
            return false;
        }

        for (MmsPassage smsPassage : list) {
            List<MmsPassageParameter> paramList = parameterMapper.findByPassageId(smsPassage.getId());
            smsPassage.getParameterList().addAll(paramList);

            pushToRedis(smsPassage);
        }

        return true;
    }

    @Override
    public List<MmsPassageProvince> getPassageProvinceById(Integer passageId) {
        return mmsPassageProvinceMapper.getListByPassageId(passageId);
    }

    @Override
    public List<MmsPassage> getByProvinceAndCmcp(Integer provinceCode, int cmcp) {
        return mmsPassageMapper.getByProvinceAndCmcp(provinceCode, cmcp);
    }

    private boolean pushToRedis(MmsPassage smsPassage) {
        try {
            stringRedisTemplate.opsForHash().put(MmsRedisConstant.RED_MMS_PASSAGE, smsPassage.getId() + "",
                                                 JSON.toJSONString(smsPassage));
            return true;
        } catch (Exception e) {
            logger.warn("REDIS 加载彩信通道[" + JSON.toJSONString(smsPassage) + "]数据失败", e);
            return false;
        }
    }

    private boolean removeFromRedis(Integer passageId) {
        try {
            stringRedisTemplate.opsForHash().delete(MmsRedisConstant.RED_MMS_PASSAGE, passageId + "");
            return true;
        } catch (Exception e) {
            logger.warn("REDIS 删除彩信通道[" + passageId + "]数据失败", e);
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
    private boolean updateMmsPassageStatus(int passageId, int status) {
        try {
            MmsPassage smsPassage = null;
            Object object = stringRedisTemplate.opsForHash().get(MmsRedisConstant.RED_MMS_PASSAGE, passageId + "");
            if (object != null) {
                smsPassage = JSON.parseObject(object.toString(), MmsPassage.class);
            } else {
                smsPassage = findById(passageId);
            }

            if (smsPassage != null) {
                smsPassage.setStatus(status);
                stringRedisTemplate.opsForHash().put(MmsRedisConstant.RED_MMS_PASSAGE, smsPassage.getId() + "",
                                                     JSON.toJSONString(smsPassage));
                return true;
            }

            logger.error("REDIS 更新彩信通道数据失败");
            return false;

        } catch (Exception e) {
            logger.warn("REDIS 加载彩信通道数据失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean doMonitorMmsSend(String mobile, String content) {
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

            // 调用发送彩信接口
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
    public List<String> findPassageCodes() {
        return mmsPassageMapper.selectAvaiableCodes();
    }

    @Override
    public boolean isPassageBelongtoDirect(String protocol, String passageCode) {
        if (StringUtils.isNotEmpty(protocol)) {
            if (ProtocolType.isBelongtoDirect(protocol)) {
                return true;
            }

            return false;
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

}
