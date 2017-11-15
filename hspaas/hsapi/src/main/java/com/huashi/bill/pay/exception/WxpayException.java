package com.huashi.bill.pay.exception;

public class WxpayException extends RuntimeException {

	private static final long serialVersionUID = -382538091157118571L;

	public WxpayException() {
		super();
	}

	public WxpayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WxpayException(String message, Throwable cause) {
		super(message, cause);
	}

	public WxpayException(String message) {
		super(message);
	}

	public WxpayException(Throwable cause) {
		super(cause);
	}

}
