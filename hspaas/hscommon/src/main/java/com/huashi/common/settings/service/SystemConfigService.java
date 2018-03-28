/**
 * 
 */
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
import com.huashi.common.settings.context.SettingsContext;
import com.huashi.common.settings.context.SettingsContext.SystemConfigType;
import com.huashi.common.settings.context.SettingsContext.WordsLibrary;
import com.huashi.common.settings.dao.SystemConfigMapper;
import com.huashi.common.settings.domain.SystemConfig;
import com.huashi.constants.CommonContext.CMCP;

/**
 * 推送设置服务接口实现类
 * 
 * @author Administrator
 */
@Service
public class SystemConfigService implements ISystemConfigService {

    @Autowired
    private SystemConfigMapper  systemConfigMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private Logger              logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<SystemConfig> findByType(String type) {
        if (StringUtils.isEmpty(type)) {
            return null;
        }

        return systemConfigMapper.findByType(type);
    }

    @Override
    public SystemConfig findByTypeAndKey(String type, String key) {
        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(key)) {
            return null;
        }

        return systemConfigMapper.findByTypeAndKey(type, key);
    }

    @Override
    public SystemConfig findById(int id) {
        return systemConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public Map<String, Object> update(SystemConfig systemConfig) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("result", true);
        resultMap.put("message", "修改配置成功！");
        try {
            SystemConfig source = systemConfigMapper.findByTypeAndKey(systemConfig.getType(), systemConfig.getAttrKey());

            if (source != null && !source.getId().equals(systemConfig.getId())) {
                return resultMap;
            }

            systemConfigMapper.updateByPrimaryKeySelective(systemConfig);

            // edit by 2017-06-19 判断是否加入REDIS缓存
            if (checkPushRedis(systemConfig.getType(), systemConfig.getAttrKey())) {

                if (SystemConfigType.REGULAR_EXPRESSION.name().equals(systemConfig.getType())) {
                    pushSettingsToRedis(getCmcpRedisName(systemConfig.getAttrKey()), systemConfig.getAttrValue());
                } else {
                    pushSettingsToRedis(systemConfig.getAttrKey(), systemConfig.getAttrValue());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("result", true);
            resultMap.put("message", "修改配置失败！");
        }
        return resultMap;
    }

    @Override
    public boolean deleteById(int id) {
        try {
            systemConfigMapper.deleteByPrimaryKey(Long.valueOf(id + ""));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Integer getUserIdByTypeName(SystemConfigType type) {
        SystemConfig systemConfig = findByTypeAndKey(type.name(), SettingsContext.USER_ID_KEY_NAME);
        if (systemConfig == null) {
            return null;
        }

        try {
            return Integer.parseInt(systemConfig.getAttrValue());
        } catch (Exception e) {
            logger.error("获取测试通道或高精通道用户ID失败", e);
            return null;
        }
    }

    @Override
    public String[] getBlacklistWords() {
        try {
            String value = stringRedisTemplate.opsForValue().get(SystemConfigType.WORDS_LIBRARY.name());
            if (StringUtils.isEmpty(value)) {
                return null;
            }

            return value.split(",");
        } catch (Exception e) {
            logger.error("查询黑名单词库失败", e);
            return null;
        }
    }

    @Override
    public String getCmcpRegex(Integer cmcpCode) {
        CMCP cmcp = CMCP.getByCode(cmcpCode);
        if (cmcp == CMCP.UNRECOGNIZED) {
            logger.warn("CMCP类型未识别，无法获取正则表达式：{}", cmcpCode);
            return null;
        }

        if (cmcp == CMCP.GLOBAL) {
            return CMCP.GLOBAL.getLocalRegex();
        }

        return null;
    }

    private static String getCmcpRedisName(String cmcpName) {
        return String.format("%s:%s", CommonRedisConstant.RED_CMCP_REGEX, cmcpName);
    }

    /**
     * TODO 根据类型和键名判断是否需要缓存到REDIS
     * 
     * @param type
     * @return
     */
    private boolean checkPushRedis(String type, String key) {
        if (StringUtils.isEmpty(type)) {
            return false;
        }

        if (SystemConfigType.REGULAR_EXPRESSION.name().equals(type)) {
            return true;
        }

        if (SystemConfigType.WORDS_LIBRARY.name().equals(type)) {
            return true;
        }

        return false;
    }

    /**
     * TODO 添加到REDIS缓存
     * 
     * @param key
     * @param value
     * @return
     */
    private boolean pushSettingsToRedis(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("黑名单黑词");
            return false;
        }
    }

    /**
     * TODO 缓存所有黑名单词库数据
     * 
     * @return
     */
    private boolean reloadAllBlacklistWords() {
        SystemConfig config = findByTypeAndKey(SystemConfigType.WORDS_LIBRARY.name(), WordsLibrary.BLACKLIST.name());
        if (config == null) {
            logger.warn("黑名单词库未设置，请检查 ");
            return false;
        }

        if (StringUtils.isEmpty(config.getAttrValue())) {
            return false;
        }

        return pushSettingsToRedis(CommonRedisConstant.RED_BLACKLIST_WORDS, config.getAttrValue());
    }

    /**
     * TODO 加载正则表达式
     * 
     * @return
     */
    private boolean reloadAllCmcpRegex() {
        List<SystemConfig> list = findByType(SystemConfigType.REGULAR_EXPRESSION.name());
        if (CollectionUtils.isNotEmpty(list)) {
            for (SystemConfig sc : list) {
                if (!pushSettingsToRedis(getCmcpRedisName(sc.getAttrKey()), sc.getAttrValue())) {
                    return false;
                }
            }

            return true;
        }

        logger.warn("运营商黑名单词库未设置，将采用默认正则表达式 ");
        for (CMCP c : CMCP.values()) {
            if (!pushSettingsToRedis(getCmcpRedisName(c.name()), c.getLocalRegex())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean reloadSettingsToRedis() {
        reloadAllCmcpRegex();
        return reloadAllBlacklistWords();
    }

}
