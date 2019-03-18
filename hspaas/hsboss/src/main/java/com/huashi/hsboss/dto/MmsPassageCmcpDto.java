package com.huashi.hsboss.dto;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.huashi.constants.CommonContext;
import com.huashi.mms.passage.domain.MmsPassage;

public class MmsPassageCmcpDto {

    private CommonContext.CMCP cmcp;

    private List<String>       passageInfos      = new LinkedList<String>();

    private List<MmsPassage>   passageList       = new ArrayList<>();

    private List<MmsPassage>   targetPassageList = new ArrayList<>();

    public CommonContext.CMCP getCmcp() {
        return cmcp;
    }

    public void setCmcp(CommonContext.CMCP cmcp) {
        this.cmcp = cmcp;
    }

    public List<MmsPassage> getPassageList() {
        return passageList;
    }

    public void setPassageList(List<MmsPassage> passageList) {
        this.passageList = passageList;
    }

    public List<MmsPassage> getTargetPassageList() {
        return targetPassageList;
    }

    public void setTargetPassageList(List<MmsPassage> targetPassageList) {
        this.targetPassageList = targetPassageList;
    }

    public List<String> getPassageInfos() {
        return passageInfos;
    }

    public void setPassageInfos(List<String> passageInfos) {
        this.passageInfos = passageInfos;
    }
}
