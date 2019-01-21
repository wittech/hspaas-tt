package com.huashi.mms.passage.domain;

public class MmsPassageProvince {

    private Long    id;

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

    public MmsPassageProvince() {
        super();
    }

    public MmsPassageProvince(Integer passageId, Integer provinceCode) {
        super();
        this.passageId = passageId;
        this.provinceCode = provinceCode;
    }

}
