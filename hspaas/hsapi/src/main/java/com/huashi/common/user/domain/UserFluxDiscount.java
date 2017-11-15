package com.huashi.common.user.domain;

import java.io.Serializable;
import java.util.Date;

public class UserFluxDiscount  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5792471794398840808L;

	private Integer id;

    private Integer userId;

    private Integer localStatus;

    private Double localCmOff;

    private Double localCtOff;

    private Double localCuOff;

    private Double globalCmOff;

    private Double globalCtOff;

    private Double globalCuOff;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getLocalStatus() {
        return localStatus;
    }

    public void setLocalStatus(Integer localStatus) {
        this.localStatus = localStatus;
    }

    public Double getLocalCmOff() {
        return localCmOff;
    }

    public void setLocalCmOff(Double localCmOff) {
        this.localCmOff = localCmOff;
    }

    public Double getLocalCtOff() {
        return localCtOff;
    }

    public void setLocalCtOff(Double localCtOff) {
        this.localCtOff = localCtOff;
    }

    public Double getLocalCuOff() {
        return localCuOff;
    }

    public void setLocalCuOff(Double localCuOff) {
        this.localCuOff = localCuOff;
    }

    public Double getGlobalCmOff() {
        return globalCmOff;
    }

    public void setGlobalCmOff(Double globalCmOff) {
        this.globalCmOff = globalCmOff;
    }

    public Double getGlobalCtOff() {
        return globalCtOff;
    }

    public void setGlobalCtOff(Double globalCtOff) {
        this.globalCtOff = globalCtOff;
    }

    public Double getGlobalCuOff() {
        return globalCuOff;
    }

    public void setGlobalCuOff(Double globalCuOff) {
        this.globalCuOff = globalCuOff;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}