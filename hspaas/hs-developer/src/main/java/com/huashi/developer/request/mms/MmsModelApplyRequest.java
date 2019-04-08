package com.huashi.developer.request.mms;

import java.util.List;

import com.huashi.developer.annotation.ValidateField;
import com.huashi.developer.request.AuthorizationRequest;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;

/**
 * 彩信模板申请请求体
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年3月29日 下午1:48:03
 */
public class MmsModelApplyRequest extends AuthorizationRequest {

    private static final long                      serialVersionUID = 7588003988518891643L;

    /**
     * 彩信名称
     */
    @ValidateField(value = "name", required = true)
    private String                                 name;

    /**
     * 彩信标题
     */
    @ValidateField(value = "title", required = true)
    private String                                 title;

    /**
     * 彩信报文（数组模式 如：[{mediaName:”test.jpg”, mediaType:”image”,content:”=bS39888993#jajierj*...”}]）
     */
    @ValidateField(value = "body", required = true)
    private String                                 body;

    /**
     * 转义BODY生成的报文数据
     */
    private transient List<MmsMessageTemplateBody> mmsMessageTemplateBodies;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public synchronized List<MmsMessageTemplateBody> getMmsMessageTemplateBodies() {
        return mmsMessageTemplateBodies;
    }

    public synchronized void setMmsMessageTemplateBodies(List<MmsMessageTemplateBody> mmsMessageTemplateBodies) {
        this.mmsMessageTemplateBodies = mmsMessageTemplateBodies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
