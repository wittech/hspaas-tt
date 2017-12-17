package com.huashi.developer.prervice;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.settings.service.IHostWhiteListService;
import com.huashi.common.third.model.MobileCatagory;
import com.huashi.common.user.context.UserBalanceConstant;
import com.huashi.common.user.context.UserContext.UserStatus;
import com.huashi.common.user.domain.UserDeveloper;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.util.SecurityUtil;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode.CommonApiCode;
import com.huashi.developer.exception.ValidateException;
import com.huashi.developer.model.PassportModel;

@Service
public class PassportPervice {

    @Reference
    private IUserService          userService;
    @Reference
    private IUserBalanceService   userBalanceService;
    @Reference
    private IUserDeveloperService userDeveloperService;
    @Reference
    private IHostWhiteListService hostWhiteListService;

    protected Logger              logger                   = LoggerFactory.getLogger(PassportPervice.class);
    // 时间戳过期时间秒数
    public static final long      EXPIRE_TIMESTAMP_SECONDS = 30;

    /**
     * TODO 根据参数获取用户ID
     * 
     * @param paramMap
     * @param ip
     * @return
     * @throws ValidateException
     */
    public PassportModel getPassport(Map<String, String[]> paramMap, String ip) throws ValidateException {
        Set<String> paramKeys = paramMap.keySet();
        if (!paramKeys.contains("appkey") || !paramKeys.contains("appsecret") || !paramKeys.contains("timestamp")) {
            throw new ValidateException(CommonApiCode.COMMON_REQUEST_EXCEPTION);
        }

        // 校验时间戳是否失效
        validateTimestampExpired(paramMap.get("timestamp")[0]);

        // 判断开发者是否存在
        UserDeveloper developer = userDeveloperService.getByAppkey(paramMap.get("appkey")[0]);
        if (developer == null) {
            throw new ValidateException(CommonApiCode.COMMON_APPKEY_INVALID);
        }

        // 判断用户密码信息是否正确
        String finalPassword = SecurityUtil.md5Hex(String.format("%s%s", developer.getAppSecret(),
                                                                 paramMap.get("timestamp")[0]));
        if (StringUtils.isEmpty(finalPassword) || !finalPassword.equals(paramMap.get("appsecret")[0])) {
            throw new ValidateException(CommonApiCode.COMMON_AUTHENTICATION_FAILED);
        }

        // 账号冻结
        if (developer.getStatus() != UserStatus.YES.getValue()) {
            throw new ValidateException(CommonApiCode.COMMON_APPKEY_NOT_AVAIABLE);
        }

        PassportModel model = new PassportModel();

        model.setUserId(developer.getUserId());
        model.setIp(ip);

        return model;
    }

    /**
     * TODO 根据参数获取用户ID
     * 
     * @param paramMap
     * @param ip
     * @return
     * @throws ValidateException
     */
    public PassportModel getPassportWithMobile(Map<String, String[]> paramMap, String ip) throws ValidateException {
        Set<String> paramKeys = paramMap.keySet();
        if (!paramKeys.contains("appkey") || !paramKeys.contains("appsecret") || !paramKeys.contains("timestamp")
            || !paramKeys.contains("mobile") || !paramKeys.contains("content")) {

            throw new ValidateException(CommonApiCode.COMMON_REQUEST_EXCEPTION);
        }

        // 校验时间戳是否失效
        validateTimestampExpired(paramMap.get("timestamp")[0]);

        // 判断开发者是否存在
        UserDeveloper developer = userDeveloperService.getByAppkey(paramMap.get("appkey")[0]);
        if (developer == null) {
            throw new ValidateException(CommonApiCode.COMMON_APPKEY_INVALID);
        }

        // 判断用户密码信息是否正确
        String finalPassword = SecurityUtil.md5Hex(String.format("%s%s%s", developer.getAppSecret(),
                                                                 paramMap.get("mobile")[0],
                                                                 paramMap.get("timestamp")[0]));
        if (StringUtils.isEmpty(finalPassword) || !finalPassword.equals(paramMap.get("appsecret")[0])) {
            throw new ValidateException(CommonApiCode.COMMON_AUTHENTICATION_FAILED);
        }

        // 账号冻结
        if (developer.getStatus() != UserStatus.YES.getValue()) {
            throw new ValidateException(CommonApiCode.COMMON_APPKEY_NOT_AVAIABLE);
        }

        // a.服务器IP未报备
        if (!hostWhiteListService.ipAllowedPass(developer.getUserId(), ip)) {
            throw new ValidateException(CommonApiCode.COMMON_REQUEST_IP_INVALID);
        }

        // 获取本次短信内容计费条数
        int fee = userBalanceService.calculateSmsAmount(developer.getUserId(), paramMap.get("content")[0]);
        if (UserBalanceConstant.CONTENT_WORDS_EXCEPTION_COUNT_FEE == fee) {
            throw new ValidateException(CommonApiCode.COMMON_BALANCE_EXCEPTION);
        }

        // 计费总条数
        int totalFee = fee * paramMap.get("mobile")[0].split(MobileCatagory.MOBILE_SPLIT_CHARCATOR).length;

        // 此处需加入是否为后付款，如果为后付则不需判断余额

        // f.用户余额不足（通过计费微服务判断，结合4.1.6中的用户计费规则）
        boolean balanceEnough = userBalanceService.isBalanceEnough(developer.getUserId(),
                                                                   PlatformType.SEND_MESSAGE_SERVICE, (double) totalFee);
        if (!balanceEnough) {
            {
                throw new ValidateException(CommonApiCode.COMMON_BALANCE_NOT_ENOUGH);
            }
        }

        PassportModel model = new PassportModel();

        model.setUserId(developer.getUserId());
        model.setFee(fee);
        model.setTotalFee(totalFee);
        model.setIp(ip);

        return model;
    }

    /**
     * TODO 判断用户时间戳是否过期
     * 
     * @param timestamp
     * @return
     */
    public void validateTimestampExpired(String timestamp) throws ValidateException {
        try {
            boolean isSuccess = System.currentTimeMillis() - Long.valueOf(timestamp) <= EXPIRE_TIMESTAMP_SECONDS * 1000;
            if (isSuccess) {
                {
                    return;
                }
            }

            throw new ValidateException(CommonApiCode.COMMON_REQUEST_TIMESTAMPS_EXPIRED);
        } catch (Exception e) {
            logger.error("时间戳验证失败，{}", e);
            throw new ValidateException(CommonApiCode.COMMON_REQUEST_TIMESTAMPS_EXPIRED);
        }
    }

}
