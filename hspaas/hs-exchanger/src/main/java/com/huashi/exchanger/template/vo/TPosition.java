package com.huashi.exchanger.template.vo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.huashi.exchanger.exception.DataParseException;

/**
 * TODO 模板坐标定位信息
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年9月28日 下午5:08:06
 */
public class TPosition extends HashMap<String, Object> implements Map<String, Object> {

    private static final long  serialVersionUID      = 1757829668329500114L;

    /**
     * 手机号码节点名称
     */
    public static final String MOBILE_NODE_NAME      = "mobile";

    /**
     * 状态节点名称
     */
    public static final String STATUS_CODE_NODE_NAME = "statusCode";

    /**
     * 任务ID
     */
    public static final String SID_NODE_NAME         = "sid";

    /**
     * 消息ID节点名称
     */
    public static final String MSGID_NODE_NAME       = "msgId";

    /**
     * 发送时间
     */
    public static final String SEND_TIME_NODE_NAME   = "sendTime";

    public Integer getPosition(String key) {
        Object obj = this.get(key);
        if (obj == null) {
            throw new DataParseException("坐标位置解析失败");
        }

        // 如果节点值为空，则无需解析此数据
        if (StringUtils.isEmpty(obj.toString())) {
            return null;
        }

        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            throw new DataParseException("坐标位置转换失败");
        }
    }
}
