package com.huashi.mms.passage.domain;


public class MmsPassageGroupDetail {
    private Integer id;

    private Integer groupId;

    private Integer passageId;

    private Integer provinceCode;

    private Integer routeType;

    private Integer priority;

    private Integer cmcp;
    
    private static final String SPLIT_TAG = "#passage_split#";
    
    private MmsPassage mmsPassage;

    public MmsPassageGroupDetail(){
        super();
    }

    public MmsPassageGroupDetail(String formData){
        //passageId + split_tag + passageName + split_tag + provinceCode + split_tag + cmcp + split_tag + routeType;
        String[] datas = formData.split(SPLIT_TAG);
        this.passageId = Integer.parseInt(datas[0]);
        this.provinceCode = Integer.parseInt(datas[2]);
        this.cmcp = Integer.parseInt(datas[3]);
        this.routeType = Integer.parseInt(datas[4]);
    }

    public String disponsePassageToSplitStr(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(passageId);
        buffer.append(SPLIT_TAG);
        buffer.append(mmsPassage.getName());
        if(mmsPassage.getCmcp().intValue() == 4){
            buffer.append(" [全]");
        }

        buffer.append(SPLIT_TAG);
        buffer.append(provinceCode);
        buffer.append(SPLIT_TAG);
        buffer.append(cmcp);
        buffer.append(SPLIT_TAG);
        return buffer.toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getPassageId() {
        return passageId;
    }

    public void setPassageId(Integer passageId) {
        this.passageId = passageId;
    }

    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getCmcp() {
        return cmcp;
    }

    public void setCmcp(Integer cmcp) {
        this.cmcp = cmcp;
    }

    
    public MmsPassage getMmsPassage() {
        return mmsPassage;
    }

    
    public void setMmsPassage(MmsPassage mmsPassage) {
        this.mmsPassage = mmsPassage;
    }
}