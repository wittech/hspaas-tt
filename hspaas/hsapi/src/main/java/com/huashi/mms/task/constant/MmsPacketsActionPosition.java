package com.huashi.mms.task.constant;

public enum MmsPacketsActionPosition {

    TEMPLATE_MISSED(0, "彩信模板未报备"), TEMPLATE_BLOCKED(1, "彩信模板状态不可用"), PASSAGE_NOT_AVAIABLE(2, "通道不可用");

    private int    position;
    private String title;

    private MmsPacketsActionPosition(int position, String title) {
        this.position = position;
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
