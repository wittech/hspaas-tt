package com.huashi.developer.exception;

import com.huashi.constants.OpenApiCode.ApiReponseCode;

/**
 * TODO 验证接入数据异常
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年9月5日 下午4:30:52
 */
public class ValidateException extends Exception {

    private static final long serialVersionUID = -8438594254125172486L;

    private ApiReponseCode    apiReponseCode;

    public ValidateException() {
        super();
    }

    public ValidateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ValidateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidateException(String message) {
        super(message);
    }

    public ValidateException(Throwable cause) {
        super(cause);
    }

    public ValidateException(ApiReponseCode apiReponseCode) {
        super(apiReponseCode.getMessage());
        this.apiReponseCode = apiReponseCode;
    }

    public ApiReponseCode getApiReponseCode() {
        return apiReponseCode;
    }

    public void setApiReponseCode(ApiReponseCode apiReponseCode) {
        this.apiReponseCode = apiReponseCode;
    }

}
