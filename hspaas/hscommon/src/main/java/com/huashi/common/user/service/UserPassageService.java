package com.huashi.common.user.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.config.redis.CommonRedisConstant;
import com.huashi.common.settings.context.SettingsContext.SystemConfigType;
import com.huashi.common.settings.context.SettingsContext.UserDefaultPassageGroupKey;
import com.huashi.common.settings.domain.SystemConfig;
import com.huashi.common.settings.service.SystemConfigService;
import com.huashi.common.user.dao.UserPassageMapper;
import com.huashi.common.user.domain.UserPassage;
import com.huashi.constants.CommonContext.PlatformType;

@Service
public class UserPassageService implements IUserPassageService {

    @Autowired
    private UserPassageMapper   userPassageMapper;
    @Autowired
    private SystemConfigService systemConfigService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private Logger              logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean update(int userId, List<UserPassage> passageList) {
        return false;
    }

    @Override
    public List<UserPassage> findByUserId(int userId) {
        return userPassageMapper.findByUserId(userId);
    }

    @Override
    public Integer getByUserIdAndType(int userId, int type) {
        if (userId == 0 || type == 0) {
            return null;
        }

        try {
            String passageGroupId = stringRedisTemplate.opsForValue().get(String.format("%s:%d:%d", CommonRedisConstant.RED_USER_SMS_PASSAGE,
                                                                userId, type));
            if(StringUtils.isNotBlank(passageGroupId))
                return Integer.parseInt(passageGroupId);
            
        } catch (Exception e) {
            logger.warn("REDIS中查询用户通道信息失败 ：{}", e.getMessage());
        }
        
        UserPassage userPassage = userPassageMapper.selectByUserIdAndType(userId, type);
        if(userPassage == null) {
            return null;
        }

        return userPassage.getPassageGroupId();
    }

    @Override
    public List<UserPassage> getPassageGroupListByGroupId(int passageGroupId) {
        return userPassageMapper.getPassageGroupListByGroupId(passageGroupId);
    }

    @Override
    public boolean save(int userId, UserPassage userPassage) {
        try {
            userPassage.setUserId(userId);
            userPassage.setCreateTime(new Date());
            return userPassageMapper.insertSelective(userPassage) > 0;
        } catch (Exception e) {
            logger.error("添加用户通道错误，{}", e);
            return false;
        }
    }

    /**
     * TODO 保存用户通道组配置
     * 
     * @param passageGroupId 通道组ID
     * @param userId 用户ID
     * @param type 平台类型：短信/语音/...
     */
    private void save(Integer passageGroupId, Integer userId, Integer type) {
        try {
            if (passageGroupId == null || userId == null || type == null) {
                logger.error("插入用户通道失败，passageGroupId: {}, userId:{}, type: {}", passageGroupId, userId, type);
                return;
            }
            UserPassage userPassage = new UserPassage();
            userPassage.setPassageGroupId(passageGroupId);
            userPassage.setType(type);
            userPassage.setUserId(userId);
            userPassage.setCreateTime(new Date());

            int effect = userPassageMapper.insertSelective(userPassage);
            if (effect > 0) {
                pushToRedis(userPassage);
            }

        } catch (Exception e) {
            removeRedis(userId, type);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean initUserPassage(int userId, List<UserPassage> passageList) {
        try {
            if (CollectionUtils.isEmpty(passageList)) {
                // 如果传递的用户通道集合为空，则根据系统参数配置查询平台所有业务的默认可用通道信息，插入值用户通道关系表中
                List<SystemConfig> systemConfigs = systemConfigService.findByType(SystemConfigType.USER_DEFAULT_PASSAGE_GROUP.name());

                if (CollectionUtils.isEmpty(systemConfigs)) {
                    throw new RuntimeException("没有可用默认通道组，请配置");
                }

                Integer type = null;
                for (SystemConfig config : systemConfigs) {
                    if (UserDefaultPassageGroupKey.SMS_DEFAULT_PASSAGE_GROUP.name().equalsIgnoreCase(config.getAttrKey())) {
                        type = PlatformType.SEND_MESSAGE_SERVICE.getCode();
                    } else if (UserDefaultPassageGroupKey.FS_DEFAULT_PASSAGE_GROUP.name().equalsIgnoreCase(config.getAttrKey())) {
                        type = PlatformType.FLUX_SERVICE.getCode();
                    } else if (UserDefaultPassageGroupKey.VS_DEFAULT_PASSAGE_GROUP.name().equalsIgnoreCase(config.getAttrKey())) {
                        type = PlatformType.VOICE_SERVICE.getCode();
                    }

                    save(StringUtils.isEmpty(config.getAttrValue()) ? null : Integer.parseInt(config.getAttrValue()),
                         userId, type);
                }
                return true;
            }

            List<Integer> busiCodes = PlatformType.allCodes();
            // 如果传递的通道和不为空，则遍历传递的通道信息，并对平台所有业务代码进行 差值比较
            for (UserPassage passage : passageList) {
                save(passage.getPassageGroupId(), userId, passage.getType());
                busiCodes.remove(passage.getType());
            }

            // busiCodes 为空则表明，传递的通道集合包含平台所有业务的通道信息，无需补齐
            if (CollectionUtils.isEmpty(busiCodes)) {
                return true;
            }

            // 如果此值不为空，则表明该业务没有设置通道，需要查询是否存在默认通道
            for (Integer code : busiCodes) {
                String key = UserDefaultPassageGroupKey.key(code);
                if (StringUtils.isEmpty(key)) {
                    continue;
                }

                SystemConfig config = systemConfigService.findByTypeAndKey(SystemConfigType.USER_DEFAULT_PASSAGE_GROUP.name(),
                                                                           key);
                if (config == null) {
                    logger.warn("没有可用默认通道组，请配置");
                    continue;
                }

                save(Integer.parseInt(config.getAttrValue()), userId, code);
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    public boolean save(int userId, List<UserPassage> userPassages) {
        if (CollectionUtils.isEmpty(userPassages)) {
            return false;
        }

        try {
            for (UserPassage userPassage : userPassages) {
                save(userId, userPassage);
                pushToRedis(userPassage);
            }

            return true;
        } catch (Exception e) {
            removeRedis(userId, null);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(int userId, int type, int passageGroupId) {
        int effect = userPassageMapper.updateByUserIdAndType(passageGroupId, userId,
                                                             PlatformType.SEND_MESSAGE_SERVICE.getCode());
        if (effect > 0) {
            pushToRedis(new UserPassage(userId, type, passageGroupId));
        }

        return true;
    }

    /**
     * TODO 移除REDIS数据
     * 
     * @param userId
     * @return
     */
    private boolean removeRedis(Integer userId, Integer type) {
        try {
            String redisKey = type == null ? String.format("%s:%d*", CommonRedisConstant.RED_USER_SMS_PASSAGE, userId) : String.format("%s:%d:%d",
                                                                                                                                       CommonRedisConstant.RED_USER_SMS_PASSAGE,
                                                                                                                                       userId,
                                                                                                                                       type);

            stringRedisTemplate.delete(stringRedisTemplate.keys(redisKey));

            return true;
        } catch (Exception e) {
            logger.warn("REDIS 移除用户通道组配置数据失败", e);
            return false;
        }
    }

    private boolean pushToRedis(UserPassage userPassage) {
        try {
            stringRedisTemplate.opsForValue().set(String.format("%s:%d:%d", CommonRedisConstant.RED_USER_SMS_PASSAGE,
                                                                userPassage.getUserId(), userPassage.getType()),
                                                  userPassage.getPassageGroupId() + "");
            return true;
        } catch (Exception e) {
            logger.warn("REDIS 加载用户通道组配置数据失败", e);
            return false;
        }
    }

    @Override
    public boolean reloadModelToRedis() {
        List<UserPassage> list = userPassageMapper.selectAll();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("可用用户通道组数据为空");
            return false;
        }

        stringRedisTemplate.delete(stringRedisTemplate.keys(CommonRedisConstant.RED_USER_SMS_PASSAGE + "*"));
        for (UserPassage userPassage : list) {
            if (!pushToRedis(userPassage)) {
                return false;
            }
        }

        return true;
    }

}
