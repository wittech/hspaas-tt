package com.huashi.mms.template.exception;


public class BodyCheckException extends Exception{
    
    private static final long serialVersionUID = -25816820731332211L;

    public BodyCheckException() {
        super();
    }

    public BodyCheckException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BodyCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public BodyCheckException(String message) {
        super(message);
    }

    public BodyCheckException(Throwable cause) {
        super(cause);
    }
    
}
