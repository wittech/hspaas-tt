package com.huashi.common.notice.vo;

import java.io.Serializable;

public class BaseResponse implements Serializable {

    private static final long serialVersionUID = 5222673901870961675L;
    private boolean           success;
    private String            msg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public BaseResponse(boolean success) {
        super();
        this.success = success;
    }

    public BaseResponse(boolean success, String msg) {
        super();
        this.success = success;
        this.msg = msg;
    }

    public BaseResponse() {
        super();
    }

}
