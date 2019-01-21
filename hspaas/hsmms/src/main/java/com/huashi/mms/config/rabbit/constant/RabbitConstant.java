package com.huashi.mms.config.rabbit.constant;


/**
 * TODO 消息队列常量定义
 *
 * @author zhengying
 * @version V1.0
 * @date 2016年9月27日 下午2:47:27
 */
public class RabbitConstant {

    /**
     * 融合平台彩信交换机
     */
    public static final String EXCHANGE_MMS                = "hspaas.mms";

    /*-----------------------------------彩信下行队列-----------------------------------------*/

    /**
     * 彩信下行 已完成前置校验（包括用户授权、余额校验等），待处理分包逻辑队列
     */
    public static final String MQ_MMS_MT_WAIT_PROCESS      = "mq_mms_mt_wait_process";

    /**
     * 彩信下行 已完整归正数据逻辑，待提交网关队列（待调用上家通道）
     */
    public static final String MQ_MMS_MT_WAIT_SUBMIT       = "mq_mms_mt_wait_submit";

    /**
     * 彩信下行 已完成上家通道调用，待网关回执队列(deliver)
     */
    public static final String MQ_MMS_MT_WAIT_RECEIPT      = "mq_mms_mt_wait_receipt";

    /**
     * 分包失败，分包数据备份队列
     */
    public static final String MQ_MMS_MT_PACKETS_EXCEPTION = "mq_mms_mt_packets_exception";

    /*-----------------------------------彩信上行队列----------------------------------------*/

    /**
     * 彩信上行回执数据（一般用于直连或者HTTP 推送处理）
     */
    public static final String MQ_MMS_MO_RECEIVE           = "mq_mms_mo_receive";

    /**
     * 彩信上行待推送队列
     */
    public static final String MQ_MMS_MO_WAIT_PUSH         = "mq_mms_mo_wait_push";

    /**
     * 需人工处理队列信息
     */
    public static final String MQ_MMS_MANUAL_HANDING       = "mq_mms_manual_handling";

    /**
     * TODO 待处理队列状态信息
     *
     * @author zhengying
     * @version V1.0
     * @date 2016年9月27日 下午2:47:27
     */
    public enum WaitProcessStatus {
        WAITING_REFORMED(0, "待归正"), COMPLETE(1, "归正完成，已提交"), WAITING_APPROVE(2, "归正完成，待审核"), REFORMED_FAILED(3, "归正失败");

        private int    code;
        private String message;

        WaitProcessStatus(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

    }

}
