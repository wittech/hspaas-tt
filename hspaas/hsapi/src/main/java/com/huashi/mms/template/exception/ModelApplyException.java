package com.huashi.mms.template.exception;

public class ModelApplyException extends Exception {

    private static final long serialVersionUID = -1482795193599188515L;

    public ModelApplyException() {
        super();
    }

    public ModelApplyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ModelApplyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelApplyException(String message) {
        super(message);
    }

    public ModelApplyException(Throwable cause) {
        super(cause);
    }

}
