package com.huashi.sms.task.exception;

public class QueueProcessException extends RuntimeException {

	private static final long serialVersionUID = -2752792857604503037L;

	public QueueProcessException() {
		super();
	}

	public QueueProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public QueueProcessException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueueProcessException(String message) {
		super(message);
	}

	public QueueProcessException(Throwable cause) {
		super(cause);
	}

}
