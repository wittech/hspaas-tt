package com.huashi.exchanger.exception;

public class DataEmptyException extends ExchangeProcessException {

	private static final long serialVersionUID = 6797149823311320357L;

	public DataEmptyException() {
		super();
	}

	public DataEmptyException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataEmptyException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataEmptyException(String message) {
		super(message);
	}

	public DataEmptyException(Throwable cause) {
		super(cause);
	}

}
