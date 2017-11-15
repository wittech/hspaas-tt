package com.huashi.hsboss.dto;

import com.huashi.common.settings.domain.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by youngmeng on 2017/1/13.
 */
public class PassageGroupProvinceDto {

    private Province province;

    private List<PassageCmcpDto> cmcpList = new ArrayList<PassageCmcpDto>();

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public List<PassageCmcpDto> getCmcpList() {
        return cmcpList;
    }

    public void setCmcpList(List<PassageCmcpDto> cmcpList) {
        this.cmcpList = cmcpList;
    }
}
