package com.huashi.common.config.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.huashi.common.settings.service.IHostWhiteListService;
import com.huashi.common.settings.service.IProvinceService;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.common.settings.service.ISystemConfigService;
import com.huashi.common.third.service.IMobileLocalService;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.user.service.IUserMmsConfigService;
import com.huashi.common.user.service.IUserPassageService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.user.service.IUserSmsConfigService;

@Configuration
@Order(3)
public class CommonInitializeRunner implements CommandLineRunner {

    @Autowired
    private IUserService          userService;
    @Autowired
    private IUserDeveloperService userDeveloperService;
    @Autowired
    private IPushConfigService    pushConfigService;
    @Autowired
    private IHostWhiteListService hostWhiteListService;
    @Autowired
    private IUserSmsConfigService userSmsConfigService;
    @Autowired
    private IProvinceService      provinceService;
    @Autowired
    private IMobileLocalService   mobileLocalService;
    @Autowired
    private ISystemConfigService  systemConfigService;
    @Autowired
    private IUserPassageService   userPassageService;
    @Autowired
    private IUserMmsConfigService userMmsConfigService;

    @Value("${gate.redis.relaod:1}")
    private int                   redisReload;

    private Logger                logger = LoggerFactory.getLogger(getClass());

    @Override
	public void run(String... arg0) throws Exception {
		if(redisReload == 0) {
			logger.info("=======================REDIS数据无需重载=======================");
			return;
		}
		
		logger.info("=======================数据初始化REDIS=======================");
		try {
		    initUserList();
	        initUserDeveloperList();
	        initUserSmsConfig();
	        initUserMmsConfig();
	        initUserPushConfig();
	        initUserPassage();
	        initHostWhiteList();
	        initProvince();
	        initProvinceMobileLocal();
	        initSystemConfig();
	        
	        logger.info("=======================初始化REDIS完成=======================");
        } catch (Exception e) {
            logger.error("=======================初始化REDIS失败=======================", e);
            throw e;
        }
	}

    /**
     * TODO 初始化用户映射数据
     */
    private void initUserList() {
        userService.reloadModelToRedis();
        logger.info("用户映射数据初始化完成");
    }

    /**
     * TODO 初始化用户开发者映射数据
     */
    private void initUserDeveloperList() {
        userDeveloperService.reloadToRedis();
        logger.info("开发者数据初始化完成");
    }

    /**
     * TODO 初始化用户推送设置
     */
    private void initUserPushConfig() {
        pushConfigService.reloadToRedis();
        logger.info("用户推送配置信息初始化完成");
    }

    /**
     * TODO 初始化用户短信配置信息
     */
    private void initUserSmsConfig() {
        userSmsConfigService.reloadToRedis();
        logger.info("用户短信配置信息初始化完成");
    }
    
    /**
     * TODO 初始化用户彩信配置信息
     */
    private void initUserMmsConfig() {
        userMmsConfigService.reloadToRedis();
        logger.info("用户彩信配置信息初始化完成");
    }

    /**
     * TODO 初始化用户通道组参数相关信息
     */
    private void initUserPassage() {
        if (userPassageService.reloadModelToRedis()) {
            logger.info("用户通道组相关信息初始化成功");
            return;
        }
        logger.error("用户通道组相关初始化失败");
    }

    /**
     * TODO 初始化服务器IP报备信息
     */
    private void initHostWhiteList() {
        hostWhiteListService.reloadToRedis();
        logger.info("用户服务器IP配置信息初始化完成");
    }

    /**
     * TODO 初始化省份
     */
    private void initProvince() {
        if (provinceService.reloadToRedis()) {
            logger.info("省份信息初始化成功");
            return;
        }
        logger.error("省份信息初始化失败");
    }

    /**
     * TODO 初始化省份手机号码归属规则
     */
    private void initProvinceMobileLocal() {
        if (mobileLocalService.reload()) {
            logger.info("省份手机号码归属初始化成功");
            return;
        }
        logger.error("省份手机号码归属初始化失败");
    }

    /**
     * TODO 初始化系统参数相关信息
     */
    private void initSystemConfig() {
        if (systemConfigService.reloadSettingsToRedis()) {
            logger.info("系统参数相关信息初始化成功");
            return;
        }
        logger.error("系统参数相关初始化失败");
    }

}
