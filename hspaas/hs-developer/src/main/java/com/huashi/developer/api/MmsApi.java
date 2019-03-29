package com.huashi.developer.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.constants.OpenApiCode.CommonApiCode;
import com.huashi.developer.exception.ValidateException;
import com.huashi.developer.model.PassportModel;
import com.huashi.developer.model.mms.MmsCustomContentSendRequest;
import com.huashi.developer.model.mms.MmsModelSendRequest;
import com.huashi.developer.prervice.MmsPrervice;
import com.huashi.developer.response.mms.MmsBalanceResponse;
import com.huashi.developer.response.mms.MmsSendResponse;
import com.huashi.developer.validator.mms.MmsCustomContentSendValidator;
import com.huashi.developer.validator.mms.MmsModelSendValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/mms")
public class MmsApi extends BasicApiSupport {

    @Autowired
    private MmsPrervice                   mmsPrervice;
    @Autowired
    private MmsModelSendValidator         mmsModelSendValidator;
    @Autowired
    private MmsCustomContentSendValidator mmsCustomContentSendValidator;

    @RequestMapping(value = "/send")
    public MmsSendResponse send() {
        try {
            MmsCustomContentSendRequest mmsCustomContentSendRequest = mmsCustomContentSendValidator.validate(request.getParameterMap(),
                                                                                                             getClientIp());
            mmsCustomContentSendRequest.setAppType(getAppType());

            return mmsPrervice.sendMessage(mmsCustomContentSendRequest);

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
            MmsModelSendRequest mmsModelSendRequest = mmsModelSendValidator.validate(request.getParameterMap(),
                                                                                     getClientIp());
            mmsModelSendRequest.setAppType(getAppType());

            return mmsPrervice.sendMessage(mmsModelSendRequest);

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
            PassportModel model = passportValidator.validate(request.getParameterMap(), getClientIp());
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
    public MmsSendResponse applyModel() {
        try {
            MmsCustomContentSendRequest mmsCustomContentSendRequest = mmsCustomContentSendValidator.validate(request.getParameterMap(),
                    getClientIp());
            mmsCustomContentSendRequest.setAppType(getAppType());

            return mmsPrervice.sendMessage(mmsCustomContentSendRequest);

        } catch (ValidateException e) {
            return new MmsSendResponse(JSON.parseObject(e.getMessage()));
        } catch (Exception e) {
            logger.error("用户自定义彩信发送失败", e);
            return new MmsSendResponse(CommonApiCode.COMMON_SERVER_EXCEPTION);
        }
    }

}
