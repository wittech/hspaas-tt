package com.huashi.common.user.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.config.redis.CommonRedisConstant;
import com.huashi.common.settings.context.SettingsContext.SystemConfigType;
// import com.huashi.common.settings.dao.PushConfigMapper;
import com.huashi.common.settings.domain.PushConfig;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.common.settings.service.ISystemConfigService;
import com.huashi.common.user.dao.UserBalanceMapper;
import com.huashi.common.user.dao.UserFluxDiscountMapper;
import com.huashi.common.user.dao.UserMapper;
import com.huashi.common.user.dao.UserPassageMapper;
import com.huashi.common.user.dao.UserProfileMapper;
import com.huashi.common.user.domain.User;
import com.huashi.common.user.domain.UserBalance;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.domain.UserFluxDiscount;
import com.huashi.common.user.domain.UserPassage;
import com.huashi.common.user.domain.UserProfile;
import com.huashi.common.user.model.UserModel;
import com.huashi.common.user.util.IdBuilder;
import com.huashi.common.util.AESUtil;
import com.huashi.common.util.SecurityUtil;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.sms.record.service.ISmsMtPushService;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserMapper             userMapper;
    @Autowired
    private UserProfileMapper      userProfileMapper;
    @Autowired
    private UserBalanceMapper      userBalanceMapper;
    @Autowired
    private UserPassageMapper      userPassageMapper;
    @Autowired
    private UserProfileMapper      profileMapper;
    @Autowired
    private IUserDeveloperService  developerService;
    @Autowired
    private IUserDeveloperService  userDeveloperService;
    @Reference
    private ISmsMtPushService      smsMtPushService;
    @Resource
    private IPushConfigService     pushConfigService;
    @Autowired
    private IUserAccountService    userAccountService;
    @Resource
    private StringRedisTemplate    stringRedisTemplate;
    @Autowired
    private ISystemConfigService   systemConfigService;
    @Autowired
    private UserFluxDiscountMapper userFluxDiscountMapper;

    private Logger                 logger = LoggerFactory.getLogger(getClass());

    @Override
    public UserDeveloper save(User user, UserProfile profile) {
        String password = user.getPassword();

        // 如果 用户名称为空（主要针对前台）则插入用户的开户手机号码
        if (StringUtils.isEmpty(user.getUserName())) {
            user.setUserName(user.getMobile());
        }

        user.setSalt(SecurityUtil.salt());
        user.setPassword(SecurityUtil.encode(password, user.getSalt()));
        user.setSecretPassword(AESUtil.encrypt(password, user.getSalt()));
        user.setCreateTime(new Date());

        int row = userMapper.insertSelective(user);
        logger.info("New UserId is {}", user.getId());
        boolean isSuccess = row > 0;
        if (isSuccess) {
            if (profile == null) {
                profile = new UserProfile();
            }

            // 设置用户基础信息
            profile.setUserId(user.getId());
            if (StringUtils.isEmpty(profile.getFullName())) {
                profile.setFullName(user.getName());
            }
            if (StringUtils.isEmpty(profile.getCompany())) {
                profile.setCompany(user.getName());
            }
            if (StringUtils.isEmpty(profile.getMobile())) {
                profile.setMobile(user.getMobile());
            }

            profile.setCreateTime(new Date());
            isSuccess = userProfileMapper.insertSelective(profile) > 0;
        }

        // 设置开发者信息
        UserDeveloper developer = userDeveloperService.saveWithReturn(user.getId());
        if (developer == null) {
            throw new RuntimeException("开发者数据生成失败");
        }

        // 设置用户账户信息
        isSuccess = userAccountService.save(user.getId());

        // 添加至REDIS
        saveUserModel(new UserModel(user.getId(), developer.getAppKey(), developer.getAppSecret(), user.getUserName(),
                                    user.getMobile(), user.getEmail(), user.getName(), user.getStatus()));

        try {
            smsMtPushService.addUserMtPushListener(user.getId());
        } catch (Exception e) {
            logger.error("初始化用户下行推送状态监听失败, 用户ID:{}", user.getId(), e);
        }

        return isSuccess ? developer : null;
    }

    @Override
    public List<User> findAll() {
        return userMapper.selectAvaiableUserList();
    }

    @Override
    public List<UserProfile> findAllUserProfile() {
        return profileMapper.findAll();
    }

    @Override
    public boolean isUserExistsByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        return userMapper.selectCountByEmail(email.trim()) > 0;
    }

    @Override
    public boolean isUserExistsByMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return false;
        }
        return userMapper.selectCountByMobile(mobile.trim()) > 0;
    }

    @Override
    public User getByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }

        return userMapper.getByUsername(username);
    }

    @Override
    public User getByEmail(String email) {
        return userMapper.getByEmail(email);
    }

    @Override
    public User getByMobile(String mobile) {
        return userMapper.getByMobile(mobile);
    }

    @Override
    public boolean verify(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return false;
        }

        Map<String, String> params = new HashMap<>();
        params.put("username", username.trim());
        params.put("password", password.trim());
        return userMapper.getByUsernameAndPassword(params) > 0;
    }

    @Override
    public UserProfile getProfileByUserId(int userId) {
        return userProfileMapper.selectProfileByUserId(userId);
    }

    @Override
    public boolean updateByUserId(UserProfile record) {
        if (record.getId() <= 0) {
            return false;
        }

        record.setUpdateTime(new Date());
        return userProfileMapper.updateByPrimaryKeySelective(record) > 0;
    }

    @Override
    public User getById(int userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public BossPaginationVo<UserProfile> findPage(int pageNum, String fullName, String mobile, String company,
                                                  String cardNo, String state, String appkey) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("fullName", fullName);
        paramMap.put("mobile", mobile);
        paramMap.put("company", company);
        paramMap.put("cardNo", cardNo);
        paramMap.put("state", state);
        paramMap.put("appkey", appkey);
        BossPaginationVo<UserProfile> page = new BossPaginationVo<UserProfile>();
        page.setCurrentPage(pageNum);
        int total = userProfileMapper.getCount(paramMap);
        if (total <= 0) {
            return page;
        }
        page.setTotalCount(total);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());

        // 测试通道用户ID
        Integer testUserId = systemConfigService.getUserIdByTypeName(SystemConfigType.PASSAGE_TEST_USER);
        // 告警通道用户ID
        Integer alarmUserId = systemConfigService.getUserIdByTypeName(SystemConfigType.SMS_ALARM_USER);

        List<UserProfile> dataList = userProfileMapper.findList(paramMap);
        for (UserProfile up : dataList) {
            setUserProfileLabel(up, testUserId, alarmUserId);
        }
        page.getList().addAll(dataList);

        testUserId = null;
        alarmUserId = null;
        return page;
    }

    /**
     * TODO 设置用户ID标签信息
     * 
     * @param up
     * @param testUserId
     * @param alarmUserId
     */
    private void setUserProfileLabel(UserProfile up, Integer testUserId, Integer alarmUserId) {
        if (testUserId != null && up.getUserId().equals(testUserId)) {
            up.setLabel(SystemConfigType.PASSAGE_TEST_USER.name());
        }
        if (alarmUserId != null && up.getUserId().equals(alarmUserId)) {
            up.setLabel(SystemConfigType.SMS_ALARM_USER.name());
        }
    }

    /**
     * 更新用户状态
     */
    @Override
    @Transactional
    public boolean changeStatus(int userId, String status) {
        try {
            int result = userMapper.updateUserState(userId, status);
            if (result > 0) {
                result = userProfileMapper.updateUserProfileState(userId, status);
            }

            // 刷新REDIS
            if (result > 0) {
                saveUserModel(userMapper.selectMappingByUserId(userId));
            }

            return result > 0;
        } catch (Exception e) {
            logger.error("更新用户： {} 状态 : {} 失败", userId, status, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateUserInfo(User user, UserDeveloper developer) {
        try {
            // login password not null,update user password and salt
            if (StringUtils.isNotBlank(user.getPassword())) {
                user.setSalt(SecurityUtil.salt());
                user.setPassword(SecurityUtil.encode(user.getPassword(), user.getSalt()));
                user.setSecretPassword(AESUtil.encrypt(user.getPassword(), user.getSalt()));
            }
            userMapper.updateUserInfo(user);
            developerService.update(developer);

            // 更新REDIS
            saveUserModel(userMapper.selectMappingByUserId(user.getId()));

            return true;
        } catch (Exception e) {
            logger.warn("更新用户数据失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public boolean updateUserProfile(UserProfile userProfile) {
        try {
            int result = userProfileMapper.updateUserProfileInfo(userProfile);
            if (result > 0) {
                // 更新REDIS
                saveUserModel(userMapper.selectMappingByUserId(userProfile.getUserId()));
            }
            return result > 0;
        } catch (Exception e) {
            logger.error("更新用户基础信息失败", e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public boolean updateSms(int userId, String balance, int payType, List<PushConfig> pushConfigList,
                             int passageGroupId) {
        try {
            userBalanceMapper.updateByUserId(new UserBalance(userId, 1, payType, Double.valueOf(balance)));
            userPassageMapper.updateByUserIdAndType(new UserPassage(userId, 1, passageGroupId));
            for (PushConfig config : pushConfigList) {
                pushConfigService.updateByUserId(config);
            }
            
            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public boolean updateFs(int userId, String balance, int payType, PushConfig pushConfig,
                            UserFluxDiscount userFluxDiscount, int passageGroupId) {
        try {
            userBalanceMapper.updateByUserId(new UserBalance(userId, 2, payType, Double.valueOf(balance)));
            userPassageMapper.updateByUserIdAndType(new UserPassage(userId, 2, passageGroupId));
            userFluxDiscount.setUserId(userId);
            userFluxDiscountMapper.updateByUserId(userFluxDiscount);
            pushConfigService.updateByUserId(pushConfig);
            
            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public boolean updateVs(int userId, String balance, int payType, PushConfig pushConfig, int passageGroupId) {
        try {
            userBalanceMapper.updateByUserId(new UserBalance(userId, 3, payType, Double.valueOf(balance)));
            userPassageMapper.updateByUserIdAndType(new UserPassage(userId, 3, passageGroupId));
            pushConfigService.updateByUserId(pushConfig);
            
            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public boolean updatePasword(int userId, String plainPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户数据异常");
        }

        boolean isSuccess = SecurityUtil.verify(user.getPassword(), plainPassword, user.getSalt());
        if (!isSuccess) {
            throw new RuntimeException("用户原密码校验失败");
        }

        user.setSalt(SecurityUtil.salt());
        user.setPassword(SecurityUtil.encode(newPassword, user.getSalt()));
        user.setSecretPassword(AESUtil.encrypt(newPassword, user.getSalt()));
        user.setUpdateTime(new Date());
        return userMapper.updateByPrimaryKeySelective(user) > 0;
    }

    @Override
    public boolean passwordExists(int userId, String password) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户数据异常");
        }
        return SecurityUtil.encode(password, user.getSalt()).equals(user.getPassword());
    }

    @Override
    public boolean updateByPrimaryKeySelective(User user) {
        return userMapper.updateByPrimaryKeySelective(user) > 0;
    }

    @Override
    public UserModel getByUserId(int userId) {
        try {
            Object obj = stringRedisTemplate.opsForHash().get(CommonRedisConstant.RED_USER_LIST, userId + "");
            if (obj != null) {
                return JSON.parseObject(obj.toString(), UserModel.class);
            }
        } catch (Exception e) {
            logger.warn("REDIS 加载失败，将于DB加载", e);
        }

        return userMapper.selectMappingByUserId(userId);
    }

    @Override
    public boolean saveUserModel(UserModel userModel) {
        return pushToRedis(userModel);
    }

    @Override
    public boolean reloadModelToRedis() {
        List<UserModel> list = userMapper.selectAllMapping();
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("可用用户数据为空");
            return false;
        }

        stringRedisTemplate.delete(stringRedisTemplate.keys(CommonRedisConstant.RED_USER_LIST + "*"));
        for (UserModel userModel : list) {
            if (!pushToRedis(userModel)) {
                return false;
            }
        }

        return true;
    }

    private boolean pushToRedis(UserModel user) {
        try {
            stringRedisTemplate.opsForValue().set(String.format("%s:%d", CommonRedisConstant.RED_USER_LIST,
                                                                user.getUserId()), JSON.toJSONString(user));
            return true;
        } catch (Exception e) {
            logger.warn("REDIS 加载用户映射数据失败", e);
            return false;
        }
    }

    @Override
    public List<UserModel> findUserModels() {
        return userMapper.selectAllMapping();
    }

    @Override
    public Set<Integer> findAvaiableUserIds() {
        List<UserModel> list = findUserModels();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        Set<Integer> set = new HashSet<>();
        for (UserModel um : list) {
            set.add(um.getUserId());
        }
        return set;
    }

    @Override
    public UserDeveloper generatePassword() {
        UserDeveloper developer = new UserDeveloper();
        developer.setSalt(BCrypt.gensalt());
        developer.setAppSecret(SecurityUtil.encode(IdBuilder.userIdBuider(), developer.getSalt()));
        return developer;
    }

}
