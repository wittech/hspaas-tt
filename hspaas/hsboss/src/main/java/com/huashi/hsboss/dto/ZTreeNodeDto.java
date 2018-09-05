package com.huashi.hsboss.dto;

/**
 * 构建ztree node节点
 * Author youngmeng
 * Created 2018-09-03 15:11
 */
public class ZTreeNodeDto {

    private String id;

    private String pId;

    private String name;

    private boolean open;

    private boolean checked;

    private String code;

    public ZTreeNodeDto() {

    }

    public ZTreeNodeDto(String id,String pId,String name) {
        this.id = id;
        this.pId = pId;
        this.name = name;
    }

    public ZTreeNodeDto(int id,int pId,String name,String code,boolean open) {
        this.id = "menu_" + id;
        this.pId = "menu_" + (pId == -1 ? 0 : pId);
        this.name = name;
        this.code = code;
        this.open = open;
    }

    public ZTreeNodeDto(int id,int pId,String name,boolean open,String code) {
        this.id = "" + id;
        this.pId = "menu_" + (pId == -1 ? 0 : pId);
        this.name = name;
        this.open = open;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
