package com.huashi.hsboss.dto;

import java.util.ArrayList;
import java.util.List;

import com.huashi.mms.passage.domain.MmsPassage;
import com.huashi.sms.passage.context.PassageContext.RouteType;

public class MmsRoutePassageGroup {

    private RouteType        type;

    private List<MmsPassage> passageList = new ArrayList<>();

    public RouteType getType() {
        return type;
    }

    public void setType(RouteType type) {
        this.type = type;
    }

    public List<MmsPassage> getPassageList() {
        return passageList;
    }

    public void setPassageList(List<MmsPassage> passageList) {
        this.passageList = passageList;
    }

}
