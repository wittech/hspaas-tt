package com.huashi.developer.validator.mms;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode.CommonApiCode;
import com.huashi.constants.OpenApiCode.MmsApiCode;
import com.huashi.developer.exception.ValidateException;
import com.huashi.developer.model.PassportModel;
import com.huashi.developer.model.mms.MmsCustomContentSendRequest;
import com.huashi.developer.prervice.MmsPrervice;
import com.huashi.developer.validator.PassportValidator;
import com.huashi.developer.validator.Validator;
import com.huashi.mms.template.exception.BodyCheckException;
import com.huashi.mms.template.service.IMmsTemplateService;

@Component
public class MmsCustomContentSendValidator extends Validator {

    @Autowired
    private PassportValidator   passportValidator;
    @Reference
    private IUserBalanceService userBalanceService;
    @Reference
    private IMmsTemplateService mmsTemplateService;

    /**
     * TODO 用户参数完整性校验
     * 
     * @param paramMap
     * @param ip
     * @return
     * @throws ValidateException
     */
    public MmsCustomContentSendRequest validate(Map<String, String[]> paramMap, String ip) throws ValidateException {
        MmsCustomContentSendRequest mmsCustomContentSendRequest = new MmsCustomContentSendRequest();
        validateAndParseFields(mmsCustomContentSendRequest, paramMap);

        // 获取授权通行证实体
        PassportModel passportModel = passportValidator.validate(paramMap, ip, mmsCustomContentSendRequest.getMobile());

        mmsCustomContentSendRequest.setIp(ip);
        mmsCustomContentSendRequest.setUserId(passportModel.getUserId());

        // 校验用户短信余额是否满足
        checkBalanceAvaiable(mmsCustomContentSendRequest, passportModel);

        // 校验自定义内容发送模板规则是否符合
        checkCustomBodyRule(mmsCustomContentSendRequest);

        return mmsCustomContentSendRequest;
    }

    /**
     * TODO 检验自定义内容格式是否无误
     * 
     * @param mmsCustomContentSendRequest
     */
    private void checkCustomBodyRule(MmsCustomContentSendRequest mmsCustomContentSendRequest) throws ValidateException {
        try {
            mmsCustomContentSendRequest.setContext(mmsTemplateService.checkBodyRuleIsRight(mmsCustomContentSendRequest.getBody()));
        } catch (BodyCheckException e) {
            logger.error("Call method[checkCustomBodyRule] failed", e);
            throw new ValidateException(MmsApiCode.MMS_CUSTOM_BODY_RULE_NOT_MATCHED);
        }
    }

    /**
     * TODO 验证签名（携带手机号码签名模式）
     * 
     * @param mmsCustomContentSendRequest
     * @param passportModel
     * @return
     * @throws ValidateException
     */
    private void checkBalanceAvaiable(MmsCustomContentSendRequest mmsCustomContentSendRequest,
                                      PassportModel passportModel) throws ValidateException {
        // 此处需加入是否为后付款，如果为后付则不需判断余额
        // f.用户余额不足（通过计费微服务判断，结合4.1.6中的用户计费规则）
        boolean balanceEnough = userBalanceService.isBalanceEnough(passportModel.getUserId(),
                                                                   PlatformType.MULTIMEDIA_MESSAGE_SERVICE,
                                                                   MmsPrervice.DEFAULT_FEE_IN_SINGLE.doubleValue());
        if (!balanceEnough) {
            throw new ValidateException(CommonApiCode.COMMON_BALANCE_NOT_ENOUGH);
        }

        mmsCustomContentSendRequest.setFee(MmsPrervice.DEFAULT_FEE_IN_SINGLE);
        mmsCustomContentSendRequest.setTotalFee(MmsPrervice.DEFAULT_FEE_IN_SINGLE);
    }

}
