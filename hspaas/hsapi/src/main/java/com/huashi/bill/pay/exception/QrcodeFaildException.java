package com.huashi.bill.pay.exception;

public class QrcodeFaildException extends WxpayException {

	private static final long serialVersionUID = 4945823790668279537L;

	public QrcodeFaildException(String message, Throwable cause) {
		super(message, cause);
	}

	public QrcodeFaildException(String message) {
		super(message);
	}

	public QrcodeFaildException(Throwable cause) {
		super(cause);
	}

}
