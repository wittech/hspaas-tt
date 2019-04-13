package com.huashi.developer.response.mms;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.huashi.constants.OpenApiCode.CommonApiCode;

/**
 * 彩信模板查询回执
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年4月11日 上午10:27:29
 */
public class MmsModelQueryResponse {

    /**
     * 状态码 #com.huashi.constants.OpenApiCode
     */
    private String                  code;

    /**
     * 回执信息描述（中文）
     */
    private String                  message;

    private List<MmsModelQueryBody> rets;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MmsModelQueryBody> getRets() {
        return rets;
    }

    public void setRets(List<MmsModelQueryBody> rets) {
        this.rets = rets;
    }

    public MmsModelQueryResponse(String code, String message, String modelId) {
        super();
        this.code = code;
        this.message = message;
    }

    public MmsModelQueryResponse() {
        super();
    }

    public MmsModelQueryResponse(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public MmsModelQueryResponse(JSONObject jsonObject) {
        super();
        try {
            this.code = jsonObject.getString("code");
            this.message = jsonObject.getString("message");
        } catch (Exception e) {
            this.code = CommonApiCode.COMMON_SERVER_EXCEPTION.getCode();
            this.message = CommonApiCode.COMMON_SERVER_EXCEPTION.getMessage();
        }
    }

    public static class MmsModelQueryBody {

        private String name;
        private String title;
        private int    status;
        private Date   createTime;
        private Date   updateTime;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

    }

}
