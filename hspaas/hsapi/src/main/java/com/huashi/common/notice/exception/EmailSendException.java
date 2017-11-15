package com.huashi.common.notice.exception;

public class EmailSendException extends RuntimeException {

	private static final long serialVersionUID = 5689508309452363182L;

	public EmailSendException() {
		super();
	}

	public EmailSendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EmailSendException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmailSendException(String message) {
		super(message);
	}

	public EmailSendException(Throwable cause) {
		super(cause);
	}

}
