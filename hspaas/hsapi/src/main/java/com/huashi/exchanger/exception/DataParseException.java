package com.huashi.exchanger.exception;

public class DataParseException extends ExchangeProcessException {

	private static final long serialVersionUID = 4272412398005239220L;

	public DataParseException() {
		super();
	}

	public DataParseException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataParseException(String message) {
		super(message);
	}

	public DataParseException(Throwable cause) {
		super(cause);
	}

}
