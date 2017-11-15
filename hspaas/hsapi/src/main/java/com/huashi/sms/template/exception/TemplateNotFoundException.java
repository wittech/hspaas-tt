package com.huashi.sms.template.exception;

public class TemplateNotFoundException extends RuntimeException{
	 
	private static final long serialVersionUID = -64795721242576744L;

	public TemplateNotFoundException() {
		super();
	}

	public TemplateNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TemplateNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateNotFoundException(String message) {
		super(message);
	}

	public TemplateNotFoundException(Throwable cause) {
		super(cause);
	}

	
}
