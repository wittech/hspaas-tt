package com.huashi.sms.passage.domain;

public class SmsPassageProvince {

    public SmsPassageProvince(){

    }

    public SmsPassageProvince(Integer passageId,Integer provinceCode){
        this.passageId = passageId;
        this.provinceCode = provinceCode;
    }

    private Long id;

    private Integer passageId;

    private Integer provinceCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPassageId() {
        return passageId;
    }

    public void setPassageId(Integer passageId) {
        this.passageId = passageId;
    }

    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }
}