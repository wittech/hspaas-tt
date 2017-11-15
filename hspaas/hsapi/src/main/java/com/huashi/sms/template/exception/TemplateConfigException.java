package com.huashi.sms.template.exception;

public class TemplateConfigException extends RuntimeException{
	 
	private static final long serialVersionUID = 3777601921013853953L;

	public TemplateConfigException() {
		super();
	}

	public TemplateConfigException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TemplateConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateConfigException(String message) {
		super(message);
	}

	public TemplateConfigException(Throwable cause) {
		super(cause);
	}

	
}
