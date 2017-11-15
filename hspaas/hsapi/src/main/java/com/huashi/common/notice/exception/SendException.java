package com.huashi.common.notice.exception;

public class SendException extends RuntimeException{

	private static final long serialVersionUID = 3718378699292293710L;

	public SendException() {
		super();
	}

	public SendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SendException(String message, Throwable cause) {
		super(message, cause);
	}

	public SendException(String message) {
		super(message);
	}

	public SendException(Throwable cause) {
		super(cause);
	}
	
}
