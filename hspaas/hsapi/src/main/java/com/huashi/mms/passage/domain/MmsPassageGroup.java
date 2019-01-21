package com.huashi.mms.passage.domain;

import java.util.ArrayList;
import java.util.List;

public class MmsPassageGroup {

    private Integer                     id;

    private String                      passageGroupName;

    private String                      comments;

    private List<MmsPassageGroupDetail> detailList = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassageGroupName() {
        return passageGroupName;
    }

    public void setPassageGroupName(String passageGroupName) {
        this.passageGroupName = passageGroupName == null ? null : passageGroupName.trim();
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments == null ? null : comments.trim();
    }

    public List<MmsPassageGroupDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<MmsPassageGroupDetail> detailList) {
        this.detailList = detailList;
    }
}
