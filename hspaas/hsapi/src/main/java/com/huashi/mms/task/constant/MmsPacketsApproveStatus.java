package com.huashi.mms.task.constant;

public enum MmsPacketsApproveStatus {

    WAITING(0, "待审核"), AUTO_COMPLETE(1, "自动通过"), HANDLING_COMPLETE(2, "手动通过"), REJECT(3, "驳回");

    private int    code;
    private String name;

    private MmsPacketsApproveStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static MmsPacketsApproveStatus parse(int code) {
        for (MmsPacketsApproveStatus pt : MmsPacketsApproveStatus.values()) {
            if (pt.getCode() == code) {
                {
                    return pt;
                }
            }
        }
        return WAITING;
    }
}
