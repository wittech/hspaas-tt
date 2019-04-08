package com.huashi.developer.validator.mms;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.constants.OpenApiCode.MmsApiCode;
import com.huashi.developer.exception.ValidateException;
import com.huashi.developer.request.AuthorizationRequest;
import com.huashi.developer.request.mms.MmsModelApplyRequest;
import com.huashi.developer.validator.AuthorizationValidator;
import com.huashi.developer.validator.Validator;
import com.huashi.mms.template.exception.BodyCheckException;
import com.huashi.mms.template.service.IMmsTemplateBodyService;

@Component
public class MmsModelApplyValidator extends Validator {

    @Autowired
    private AuthorizationValidator  authorizationValidator;
    @Reference
    private IUserBalanceService     userBalanceService;
    @Reference
    private IMmsTemplateBodyService mmsTemplateBodyService;

    /**
     * TODO 用户参数完整性校验
     * 
     * @param paramMap
     * @param ip
     * @return
     * @throws ValidateException
     */
    public MmsModelApplyRequest validate(Map<String, String[]> paramMap, String ip) throws ValidateException {
        MmsModelApplyRequest mmsModelApplyRequest = new MmsModelApplyRequest();
        validateAndParseFields(mmsModelApplyRequest, paramMap);

        // 获取授权通行证实体
        AuthorizationRequest passportModel = authorizationValidator.validate(paramMap, ip);

        passportModel.setIp(ip);
        mmsModelApplyRequest.setUserId(passportModel.getUserId());

        // 校验自定义内容发送模板规则是否符合
        checkModelBodyRule(mmsModelApplyRequest);

        return mmsModelApplyRequest;
    }

    /**
     * TODO 检验模板内容格式是否无误
     * 
     * @param mmsCustomContentSendRequest
     */
    private void checkModelBodyRule(MmsModelApplyRequest mmsModelApplyRequest) throws ValidateException {
        try {
            mmsModelApplyRequest.setMmsMessageTemplateBodies(mmsTemplateBodyService.translateBody(mmsModelApplyRequest.getBody()).values().iterator().next());
        } catch (BodyCheckException e) {
            logger.error("Call method[MmsModelApplyRequest] failed", e);
            throw new ValidateException(MmsApiCode.MMS_MODEL_BODY_RULE_NOT_MATCHED);
        }
    }

}
