package com.huashi.sms.record.model;

import java.io.Serializable;
import java.util.List;

import com.huashi.sms.task.model.SmsMoPushResponse;

/**
 * 
  * TODO 上行待推送数据
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2016年12月21日 下午8:28:14
 */
public class SmsMoWaitPushModel implements Serializable {

	private static final long serialVersionUID = 124270445166898261L;
	private String pushUrl;
	private Integer retryTimes;

	private List<SmsMoPushResponse> list;

	public String getPushUrl() {
		return pushUrl;
	}

	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}

	public Integer getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(Integer retryTimes) {
		this.retryTimes = retryTimes;
	}

	public List<SmsMoPushResponse> getList() {
		return list;
	}

	public void setList(List<SmsMoPushResponse> list) {
		this.list = list;
	}

}
