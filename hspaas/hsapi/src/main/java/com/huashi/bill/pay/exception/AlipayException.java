package com.huashi.bill.pay.exception;

public class AlipayException extends RuntimeException {

	private static final long serialVersionUID = -2811006537902209343L;

	public AlipayException() {
		super();
	}

	public AlipayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AlipayException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlipayException(String message) {
		super(message);
	}

	public AlipayException(Throwable cause) {
		super(cause);
	}

}
