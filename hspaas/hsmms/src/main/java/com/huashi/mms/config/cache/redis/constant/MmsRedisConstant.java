package com.huashi.mms.config.cache.redis.constant;

/**
 * TODO 彩信REDIS缓存常量类
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月12日 下午3:33:28
 */
public class MmsRedisConstant {

    /**
     * 彩信通道
     */
    public static final String RED_MMS_PASSAGE                       = "red_mms_passage";

    /**
     * 用户可用通道前缀
     */
    public static final String RED_USER_PASSAGE_ACCESS               = "red_user_passage_access";

    /**
     * 用户彩信模板
     */
    public static final String RED_USER_MESSAGE_TEMPLATE             = "red_user_message_template";

    /**
     * 用户签名扩展号码
     */
    public static final String RED_USER_SIGNATURE_EXT_NO             = "red_user_signature_ext_no";

    /**
     * 用户通道彩信模板
     */
    public static final String RED_USER_PASSAGE_MESSAGE_TEMPLATE     = "red_user_passage_message_template";

    /**
     * 针对下行彩信已发送，待网关回执信息记录相关关联关系（sid,msg_id,mobile,create_time）
     */
    public static final String RED_READY_MESSAGE_WAIT_RECEIPT        = "ready_message_wait_receipt";

    /**
     * redis中 消息是否需要推送KEY前缀(结构采用HASH)
     */
    public static final String RED_READY_MT_PUSH_CONFIG              = "mms_ready_mt_push_config";

    /**
     * redis中消息状态回执成功（通道回执）但我方对接解析失败，需要留底以备重新 更新数据
     */
    public static final String RED_MMS_STATUS_RECEIPT_EXCEPTION_LIST = "mms_exception_receipt_status";

    // **************加入分布式名称定义 edit by zhengying 20170726 ************/

    /**
     * 针对下行彩信回执状态已回,但推送状态未知情况(一般是回执状态回来过快而 SUBMIT消息还未入库) 分布式
     */
    public static String       RED_QUEUE_MMS_DELIVER_FAILOVER        = "queue_mms_deliver_failover";

    /**
     * 彩信下行 已回执状态，待推送给下家客户队列信息（分布式）
     */
    public static String       RED_QUEUE_MMS_MT_WAIT_PUSH            = "queue_mms_mt_wait_push";

    /**
     * redis中消息上行（通道回执）但我方对接解析失败，需要留底以备重新 更新数据
     */
    public static final String RED_MMS_MO_RECEIPT_EXCEPTION_LIST     = "mms_mo_receipt_exception_list";

    // ** -----------------------------------手机号码防护墙 相关 ----------------------------------------------**/

    /**
     * 同一用户统一彩信模板统一手机号码每天计数器
     */
    public static final String RED_MOBILE_GREEN_TABLES               = "red_mobile_green_tables";

    // ** -----------------------------------广播通知数据 相关 ----------------------------------------------**/

    /**
     * 彩信模板订阅频道定义
     */
    public static final String BROADCAST_MESSAGE_TEMPLATE_TOPIC      = "broadcast_message_template_topic";

    /**
     * 可用通道订阅频道定义
     */
    public static final String BROADCAST_PASSAGE_ACCESS_TOPIC        = "broadcast_passage_access_topic";

    /**
     * TODO 消息操作动作
     *
     * @author zhengying
     * @version V1.0
     * @date 2016年12月27日 下午5:50:14
     */
    public enum MessageAction {

        /**
         * 广播添加
         */
        ADD(1),

        /**
         * 广播移除
         */
        REMOVE(2),

        /**
         * 广播更新
         */
        MODIFY(3);

        private int code;

        MessageAction(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static MessageAction parse(int code) {
            for (MessageAction ma : MessageAction.values()) {
                if (code == ma.getCode()) {
                    return ma;
                }
            }

            return MessageAction.ADD;
        }

    }
}
