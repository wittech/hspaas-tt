package com.huashi.sms.settings.constant;

/**
 * 
 * TODO 黑名单类型
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年5月8日 下午5:01:34
 */
public enum MobileBlacklistType {

	NORMAL(0, "一般黑名单"), COMPLAINT(1, "投诉黑名单"), UNSUBSCRIBE(2, "退订黑名单"), OTHER(
			10, "其他黑名单");

	MobileBlacklistType(int code, String title) {
		this.code = code;
		this.title = title;
	}
	
	public static String parse(int code) {
		for(MobileBlacklistType mbt : MobileBlacklistType.values()) {
			if(mbt.code == code) {
                {
                    return mbt.getTitle();
                }
            }
		}
		return MobileBlacklistType.NORMAL.getTitle();
	}

	private int code;
	private String title;

	public int getCode() {
		return code;
	}

	public String getTitle() {
		return title;
	}

}
