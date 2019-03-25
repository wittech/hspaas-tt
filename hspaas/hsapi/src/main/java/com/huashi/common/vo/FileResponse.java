package com.huashi.common.vo;

import java.io.Serializable;

public class FileResponse implements Serializable {

    private static final long serialVersionUID = 227172620402980005L;
    private boolean           success;
    private String            msg;
    private String            url;

    public FileResponse(boolean success) {
        super();
        this.success = success;
    }

    public FileResponse(boolean success, String msg) {
        super();
        this.success = success;
        this.msg = msg;
    }

    public FileResponse(boolean success, String msg, String url) {
        super();
        this.success = success;
        this.msg = msg;
        this.url = url;
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
