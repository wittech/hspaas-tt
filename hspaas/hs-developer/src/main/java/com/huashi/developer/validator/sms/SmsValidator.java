package com.huashi.developer.validator.sms;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.third.model.MobileCatagory;
import com.huashi.common.user.context.UserBalanceConstant;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode.CommonApiCode;
import com.huashi.developer.exception.ValidateException;
import com.huashi.developer.model.PassportModel;
import com.huashi.developer.model.sms.SmsSendRequest;
import com.huashi.developer.validator.PassportValidator;
import com.huashi.developer.validator.Validator;

@Component
public class SmsValidator extends Validator {

    @Autowired
    private PassportValidator   passportValidator;
    @Reference
    private IUserBalanceService userBalanceService;

    /**
     * TODO 用户参数完整性校验
     * 
     * @param paramMap
     * @param ip
     * @return
     * @throws ValidateException
     */
    public SmsSendRequest validate(Map<String, String[]> paramMap, String ip) throws ValidateException {
        SmsSendRequest smsSendRequest = new SmsSendRequest();
        validateAndParseFields(smsSendRequest, paramMap);

        // 获取授权通行证实体
        PassportModel passportModel = passportValidator.validate(paramMap, ip, smsSendRequest.getMobile());

        smsSendRequest.setIp(ip);
        smsSendRequest.setUserId(passportModel.getUserId());

        // 校验用户短信余额是否满足
        checkBalanceAvaiable(smsSendRequest, passportModel);

        return smsSendRequest;
    }

    /**
     * TODO 验证签名（携带手机号码签名模式）
     * 
     * @param smsSendRequest
     * @param passportModel
     * @return
     * @throws ValidateException
     */
    private void checkBalanceAvaiable(SmsSendRequest smsSendRequest, PassportModel passportModel) throws ValidateException {
        // 获取本次短信内容计费条数
        int fee = userBalanceService.calculateSmsAmount(passportModel.getUserId(), smsSendRequest.getContent());
        if (UserBalanceConstant.CONTENT_WORDS_EXCEPTION_COUNT_FEE == fee) {
            throw new ValidateException(CommonApiCode.COMMON_BALANCE_EXCEPTION);
        }

        // 计费总条数
        int totalFee = fee * smsSendRequest.getMobile().split(MobileCatagory.MOBILE_SPLIT_CHARCATOR).length;

        // 此处需加入是否为后付款，如果为后付则不需判断余额
        // f.用户余额不足（通过计费微服务判断，结合4.1.6中的用户计费规则）
        boolean balanceEnough = userBalanceService.isBalanceEnough(passportModel.getUserId(),
                                                                   PlatformType.SEND_MESSAGE_SERVICE, (double) totalFee);
        if (!balanceEnough) {
            throw new ValidateException(CommonApiCode.COMMON_BALANCE_NOT_ENOUGH);
        }

        smsSendRequest.setFee(fee);
        smsSendRequest.setTotalFee(totalFee);
    }

}