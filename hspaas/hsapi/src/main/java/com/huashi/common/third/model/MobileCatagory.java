package com.huashi.common.third.model;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * TODO 手机号码分类
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年2月9日 上午11:30:27
 */
public class MobileCatagory implements Serializable {

	// 数据分隔符
	public static final String MOBILE_SPLIT_CHARCATOR = ",";

	private static final long serialVersionUID = -8382221594219947563L;
	// 处理结果: true or false
	private boolean success;
	// 结果描述，处理错误才有值
	private String msg;
	// 移动号码，以半角逗号分隔
	private Map<Integer, String> cmNumbers;
	// 联通号码
	private Map<Integer, String> cuNumbers;
	// 电信号码
	private Map<Integer, String> ctNumbers;
	// 过滤号码总数
	private int filterSize;
	// 过滤信息
	private String filterNumbers;
	// 重复号码
	private String repeatNumbers;
	// 重复号码总数
	private int repeatSize;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getFilterNumbers() {
		return filterNumbers;
	}

	public void setFilterNumbers(String filterNumbers) {
		this.filterNumbers = filterNumbers;
	}

	public int getFilterSize() {
		return filterSize;
	}

	public void setFilterSize(int filterSize) {
		this.filterSize = filterSize;
	}

	public Map<Integer, String> getCmNumbers() {
		return cmNumbers;
	}

	public void setCmNumbers(Map<Integer, String> cmNumbers) {
		this.cmNumbers = cmNumbers;
	}

	public Map<Integer, String> getCuNumbers() {
		return cuNumbers;
	}

	public void setCuNumbers(Map<Integer, String> cuNumbers) {
		this.cuNumbers = cuNumbers;
	}

	public Map<Integer, String> getCtNumbers() {
		return ctNumbers;
	}

	public void setCtNumbers(Map<Integer, String> ctNumbers) {
		this.ctNumbers = ctNumbers;
	}

	public String getRepeatNumbers() {
		return repeatNumbers;
	}

	public void setRepeatNumbers(String repeatNumbers) {
		this.repeatNumbers = repeatNumbers;
	}

	public int getRepeatSize() {
		return repeatSize;
	}

	public void setRepeatSize(int repeatSize) {
		this.repeatSize = repeatSize;
	}

	@Override
	public String toString() {
		return "MobileCatagory [success=" + success + ", msg=" + msg + ", cmNumbers=" + cmNumbers + ", cuNumbers="
				+ cuNumbers + ", ctNumbers=" + ctNumbers + ", filterSize=" + filterSize + ", filterNumbers="
				+ filterNumbers + ", repeatNumbers=" + repeatNumbers + ", repeatSize=" + repeatSize + "]";
	}

}
