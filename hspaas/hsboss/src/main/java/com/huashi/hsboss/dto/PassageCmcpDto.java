package com.huashi.hsboss.dto;

import com.huashi.constants.CommonContext;
import com.huashi.sms.passage.domain.SmsPassage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO
 * Author youngmeng
 * Created 2016-10-10 23:13
 */
public class PassageCmcpDto {

    private CommonContext.CMCP cmcp;

    private List<String> passageInfos = new LinkedList<String>();


    private List<SmsPassage> passageList = new ArrayList<SmsPassage>();

    private List<SmsPassage> targetPassageList = new ArrayList<SmsPassage>();

    public CommonContext.CMCP getCmcp() {
        return cmcp;
    }

    public void setCmcp(CommonContext.CMCP cmcp) {
        this.cmcp = cmcp;
    }

    public List<SmsPassage> getPassageList() {
        return passageList;
    }

    public void setPassageList(List<SmsPassage> passageList) {
        this.passageList = passageList;
    }

    public List<SmsPassage> getTargetPassageList() {
        return targetPassageList;
    }

    public void setTargetPassageList(List<SmsPassage> targetPassageList) {
        this.targetPassageList = targetPassageList;
    }

    public List<String> getPassageInfos() {
        return passageInfos;
    }

    public void setPassageInfos(List<String> passageInfos) {
        this.passageInfos = passageInfos;
    }
}
