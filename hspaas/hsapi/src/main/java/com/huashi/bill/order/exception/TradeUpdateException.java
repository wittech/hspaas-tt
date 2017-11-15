package com.huashi.bill.order.exception;

public class TradeUpdateException extends RuntimeException {

	private static final long serialVersionUID = -8299582947914677041L;

	public TradeUpdateException() {
		super();
	}

	public TradeUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TradeUpdateException(String message, Throwable cause) {
		super(message, cause);
	}

	public TradeUpdateException(String message) {
		super(message);
	}

	public TradeUpdateException(Throwable cause) {
		super(cause);
	}

}
