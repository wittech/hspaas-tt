package com.huashi.developer.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.constants.OpenApiCode.CommonApiCode;
import com.huashi.developer.exception.ValidateException;
import com.huashi.developer.prervice.MmsPrervice;
import com.huashi.developer.request.AuthorizationRequest;
import com.huashi.developer.request.mms.MmsModelApplyRequest;
import com.huashi.developer.request.mms.MmsSendByModelRequest;
import com.huashi.developer.request.mms.MmsSendRequest;
import com.huashi.developer.response.mms.MmsBalanceResponse;
import com.huashi.developer.response.mms.MmsModelResponse;
import com.huashi.developer.response.mms.MmsSendResponse;
import com.huashi.developer.validator.mms.MmsModelApplyValidator;
import com.huashi.developer.validator.mms.MmsSendByModelValidator;
import com.huashi.developer.validator.mms.MmsSendValidator;

@RestController
@RequestMapping(value = "/mms")
public class MmsApi extends BasicApiSupport {

    @Autowired
    private MmsPrervice             mmsPrervice;
    @Autowired
    private MmsSendValidator        mmsSendValidator;
    @Autowired
    private MmsSendByModelValidator mmsSendByModelValidator;
    @Autowired
    private MmsModelApplyValidator  mmsModelApplyValidator;

    @RequestMapping(value = "/send")
    public MmsSendResponse send() {
        try {
            MmsSendRequest mmsSendRequest = mmsSendValidator.validate(request.getParameterMap(), getClientIp());
            mmsSendRequest.setAppType(getAppType());

            return mmsPrervice.sendMessage(mmsSendRequest);

        } catch (ValidateException e) {
            return new MmsSendResponse(JSON.parseObject(e.getMessage()));
        } catch (Exception e) {
            logger.error("用户自定义彩信发送失败", e);
            return new MmsSendResponse(CommonApiCode.COMMON_SERVER_EXCEPTION);
        }
    }

    /**
     * 发送彩信
     *
     * @return
     */
    @RequestMapping(value = "/sendByModel", method = { RequestMethod.POST, RequestMethod.GET })
    public MmsSendResponse sendByModelId() {
        try {
            MmsSendByModelRequest mmsSendByModelRequest = mmsSendByModelValidator.validate(request.getParameterMap(),
                                                                                           getClientIp());
            mmsSendByModelRequest.setAppType(getAppType());

            return mmsPrervice.sendMessageByModel(mmsSendByModelRequest);

        } catch (ValidateException e) {
            return new MmsSendResponse(JSON.parseObject(e.getMessage()));
        } catch (Exception e) {
            logger.error("用户模版彩信发送失败", e);
            return new MmsSendResponse(CommonApiCode.COMMON_SERVER_EXCEPTION);
        }
    }

    /**
     * 获取余额
     *
     * @return
     */
    @RequestMapping(value = "/balance", method = { RequestMethod.POST, RequestMethod.GET })
    public MmsBalanceResponse getBalance() {
        try {
            AuthorizationRequest model = passportValidator.validate(request.getParameterMap(), getClientIp());
            model.setAppType(getAppType());

            return mmsPrervice.getBalance(model.getUserId());
        } catch (Exception e) {
            // 如果失败则存储错误日志
            String code = CommonApiCode.COMMON_SERVER_EXCEPTION.getCode();
            if (e instanceof ValidateException) {
                JSONObject jsonObect = JSON.parseObject(e.getMessage());
                code = jsonObect.getString("code");
            }
            return new MmsBalanceResponse(code);
        }
    }

    @RequestMapping(value = "/applyModel")
    public MmsModelResponse applyModel() {
        try {
            MmsModelApplyRequest mmsModelApplyRequest = mmsModelApplyValidator.validate(request.getParameterMap(),
                                                                                        getClientIp());
            mmsModelApplyRequest.setAppType(getAppType());

            return mmsPrervice.applyModel(mmsModelApplyRequest);

        } catch (ValidateException e) {
            return new MmsModelResponse(JSON.parseObject(e.getMessage()));
        } catch (Exception e) {
            logger.error("用户彩信模板报备失败", e);
            return new MmsModelResponse(CommonApiCode.COMMON_SERVER_EXCEPTION.getCode(),
                                        CommonApiCode.COMMON_SERVER_EXCEPTION.getMessage());
        }
    }

}
