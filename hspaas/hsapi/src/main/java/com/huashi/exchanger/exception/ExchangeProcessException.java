package com.huashi.exchanger.exception;

public class ExchangeProcessException extends RuntimeException {

	
	 /**    */
	 
	private static final long serialVersionUID = -5775760053062568763L;

	public ExchangeProcessException() {
		super();
	}

	public ExchangeProcessException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExchangeProcessException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExchangeProcessException(String message) {
		super(message);
	}

	public ExchangeProcessException(Throwable cause) {
		super(cause);
	}

}
