package com.huashi.developer.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.huashi.constants.OpenApiCode.CommonApiCode;
import com.huashi.developer.exception.ValidateException;
import com.huashi.developer.model.PassportModel;
import com.huashi.developer.model.SmsModel;
import com.huashi.developer.model.SmsP2PModel;
import com.huashi.developer.model.SmsP2PTemplateModel;
import com.huashi.developer.prervice.SmsPrervice;
import com.huashi.developer.response.sms.SmsBalanceResponse;
import com.huashi.developer.response.sms.SmsApiResponse;
import com.huashi.developer.util.IpUtil;
import com.huashi.developer.validator.SmsP2PTemplateValidator;
import com.huashi.developer.validator.SmsP2PValidator;
import com.huashi.developer.validator.SmsValidator;

@RestController
@RequestMapping(value = "/sms")
public class SmsApi extends BasicApiSupport {

    @Autowired
    private SmsPrervice smsPrervice;
    @Autowired
    private SmsValidator smsValidator;
    @Autowired
    private SmsP2PValidator smsP2PValidator;
    @Autowired
    private SmsP2PTemplateValidator smsP2PTemplateValidator;

    /**
     * TODO 发送短信
     *
     * @return
     */
    @RequestMapping(value = "/send", method = {RequestMethod.POST, RequestMethod.GET})
    public SmsApiResponse send() {
        try {
            SmsModel model = smsValidator.validate(request.getParameterMap(), getClientIp());
            model.setAppType(getAppType());

            return smsPrervice.sendMessage(model);

        } catch (ValidateException e) {
            return saveInvokeFailedRecord(e.getMessage());
        } catch (Exception e) {
            logger.error("用户短信发送失败", e);
            return new SmsApiResponse(CommonApiCode.COMMON_SERVER_EXCEPTION);
        }
    }

    /**
     * TODO 普通点对点提交短信
     *
     * @return
     */
    @RequestMapping(value = "/p2p")
    public SmsApiResponse p2pSend() {
        try {
            SmsP2PModel model = smsP2PValidator.validate(request.getParameterMap(), getClientIp());
            model.setAppType(getAppType());

            return smsPrervice.sendP2PMessage(model);

        } catch (ValidateException e) {
            return saveInvokeFailedRecord(e.getMessage());
        } catch (Exception e) {
            logger.error("用户普通点对点短信发送失败", e);
            return new SmsApiResponse(CommonApiCode.COMMON_SERVER_EXCEPTION);
        }
    }

    /**
     * TODO 保存错误信息
     *
     * @param message
     * @return
     */
    private SmsApiResponse saveInvokeFailedRecord(String message) {
        SmsApiResponse response = new SmsApiResponse(JSON.parseObject(message));
        try {
            // 如果处理失败则持久化到DB
            smsPrervice.saveErrorLog(response.getCode(), request.getRequestURL().toString(), IpUtil.getClientIp(request), request.getParameterMap(), getAppType());
        } catch (Exception e) {
            logger.error("持久化提交接口错误失败", e);
        }

        return response;
    }

    /**
     * TODO 模板点对点提交短信
     *
     * @return
     */
    @RequestMapping(value = "/p2p_template")
    public SmsApiResponse p2pTemplateSend() {
        try {
            SmsP2PTemplateModel model = smsP2PTemplateValidator.validate(request.getParameterMap(), getClientIp());
            model.setAppType(getAppType());

            return smsPrervice.sendP2PTemplateMessage(model);

        } catch (Exception e) {
            logger.error("用户模板点对点短信发送失败", e);

            if (e instanceof ValidateException) {
                return saveInvokeFailedRecord(e.getMessage());
            }

            return new SmsApiResponse(CommonApiCode.COMMON_SERVER_EXCEPTION);
        }
    }

    /**
     * TODO 获取余额
     *
     * @return
     */
    @RequestMapping(value = "/balance", method = {RequestMethod.POST, RequestMethod.GET})
    public SmsBalanceResponse getBalance() {
        try {
            PassportModel model = passportValidator.validate(request.getParameterMap(), getClientIp());
            model.setAppType(getAppType());

            return smsPrervice.getBalance(model.getUserId());
        } catch (Exception e) {
            // 如果失败则存储错误日志
            String code = CommonApiCode.COMMON_SERVER_EXCEPTION.getCode();
            if (e instanceof ValidateException) {
                SmsApiResponse response = saveInvokeFailedRecord(e.getMessage());
                if (response != null) {
                    code = response.getCode();
                }
            }
            return new SmsBalanceResponse(code);
        }

    }
}
