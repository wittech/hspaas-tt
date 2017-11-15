package com.huashi.sms.report.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * TODO 全国运营商统计
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年8月30日 下午5:53:03
 */
public class ChinessCmcpSubmitReportVo implements Serializable {

	private static final long serialVersionUID = -6182367968567214046L;
	private List<CmcpAmount> cmlist = new ArrayList<>();
	private List<CmcpAmount> culist = new ArrayList<>();
	private List<CmcpAmount> ctlist = new ArrayList<>();

	public static class CmcpAmount implements Serializable {

		private static final long serialVersionUID = 8246078493048706459L;
		private String name;
		private Integer value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getValue() {
			return value;
		}

		public void setValue(Integer value) {
			this.value = value;
		}

		public CmcpAmount(String name, Integer value) {
			super();
			this.name = name;
			this.value = value;
		}

		public CmcpAmount() {
			super();
		}
	}

	public List<CmcpAmount> getCmlist() {
		return cmlist;
	}

	public void setCmlist(List<CmcpAmount> cmlist) {
		this.cmlist = cmlist;
	}

	public List<CmcpAmount> getCulist() {
		return culist;
	}

	public void setCulist(List<CmcpAmount> culist) {
		this.culist = culist;
	}

	public List<CmcpAmount> getCtlist() {
		return ctlist;
	}

	public void setCtlist(List<CmcpAmount> ctlist) {
		this.ctlist = ctlist;
	}

}
