package com.huashi.hsboss.dto;

import com.huashi.common.settings.domain.Province;

import java.util.ArrayList;
import java.util.List;

public class MmsPassageGroupProvinceDto {

    private Province                province;

    private List<MmsPassageCmcpDto> cmcpList = new ArrayList<>();

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public List<MmsPassageCmcpDto> getCmcpList() {
        return cmcpList;
    }

    public void setCmcpList(List<MmsPassageCmcpDto> cmcpList) {
        this.cmcpList = cmcpList;
    }
}
