package com.huashi.mms.task.constant;


public enum MmsPacketsProcessStatus {

    DOING(0, "正在分包"), PROCESS_COMPLETE(1, "分包完成，待提交网关"), PROCESS_EXCEPTION(2, "分包异常，待处理");

    private int    code;
    private String name;

    private MmsPacketsProcessStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static MmsPacketsProcessStatus parse(int code) {
        for (MmsPacketsProcessStatus pt : MmsPacketsProcessStatus.values()) {
            if (pt.getCode() == code) {
                {
                    return pt;
                }
            }
        }
        return PROCESS_EXCEPTION;
    }
}
