package com.huashi.bill.order;

public class OrderBuilderException extends RuntimeException {

	private static final long serialVersionUID = -6581309635121519151L;

	public OrderBuilderException() {
		super();
	}

	public OrderBuilderException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public OrderBuilderException(String message, Throwable cause) {
		super(message, cause);
	}

	public OrderBuilderException(String message) {
		super(message);
	}

	public OrderBuilderException(Throwable cause) {
		super(cause);
	}
}
