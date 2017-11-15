package com.huashi.bill.order.exception;

public class ExchangeException extends RuntimeException {

	private static final long serialVersionUID = -5210193270356373648L;

	public ExchangeException() {
		super();
	}

	public ExchangeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExchangeException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExchangeException(String message) {
		super(message);
	}

	public ExchangeException(Throwable cause) {
		super(cause);
	}

}
