package com.huashi.common.settings.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
  * TODO 省份手机号码段
  * @author zhengying
  * @version V1.0   
  * @date 2017年2月8日 上午9:49:37
 */
public class ProvinceLocal implements Serializable{
	 
	private static final long serialVersionUID = 3366136571836319541L;

	private Long id;

    private Integer provinceCode;

    private String numberArea;

    private Integer cmcp;

    private String city;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getNumberArea() {
        return numberArea;
    }

    public void setNumberArea(String numberArea) {
        this.numberArea = numberArea == null ? null : numberArea.trim();
    }

    public Integer getCmcp() {
        return cmcp;
    }

    public void setCmcp(Integer cmcp) {
        this.cmcp = cmcp;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}