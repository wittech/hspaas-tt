package com.huashi.sms.config.rabbit.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * TODO 消息队列常量定义
 *
 * @author zhengying
 * @version V1.0
 * @date 2016年9月27日 下午2:47:27
 */
public class RabbitConstant {

    /**
     * 融合平台短信交换机
     */
    public static final String EXCHANGE_SMS                = "hspaas.sms";

    /*-----------------------------------短信下行队列-----------------------------------------*/

    /**
     * 短信下行 已完成前置校验（包括用户授权、余额校验等），待处理分包逻辑队列
     */
    public static final String MQ_SMS_MT_WAIT_PROCESS      = "mq_sms_mt_wait_process";

    /**
     * 短信点对点/模板点对点短信下行 已完成前置校验（包括用户授权、余额校验等），待处理分包逻辑队列
     */
    public static final String MQ_SMS_MT_P2P_WAIT_PROCESS  = "mq_sms_mt_p2p_wait_process";

    /**
     * 短信下行 已完整归正数据逻辑，待提交网关队列（待调用上家通道）
     */
    public static final String MQ_SMS_MT_WAIT_SUBMIT       = "mq_sms_mt_wait_submit";

    /**
     * 短信下行 已完成上家通道调用，待网关回执队列(deliver)
     */
    public static final String MQ_SMS_MT_WAIT_RECEIPT      = "mq_sms_mt_wait_receipt";

    /**
     * 分包失败，分包数据备份队列
     */
    public static final String MQ_SMS_MT_PACKETS_EXCEPTION = "mq_sms_mt_packets_exception";

    /*-----------------------------------短信上行队列----------------------------------------*/

    /**
     * 短信上行回执数据（一般用于直连或者HTTP 推送处理）
     */
    public static final String MQ_SMS_MO_RECEIVE           = "mq_sms_mo_receive";

    /**
     * 短信上行待推送队列
     */
    public static final String MQ_SMS_MO_WAIT_PUSH         = "mq_sms_mo_wait_push";

    /**
     * 需人工处理队列信息
     */
    public static final String MQ_SMS_MANUAL_HANDING       = "mq_sms_manual_handling";

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

    /**
     * TODO 关键词优先级
     *
     * @author zhengying
     * @version V1.0.0
     * @date 2016年10月4日 下午10:20:05
     */
    public enum WordsPriority {

        L10(10, new String[] { "验证码", "动态码", "校验码" }), 
        
        L5(5, new String[] { "分钟", "【富宝】" }),
        
        L1(1, new String[] { "回复TD" }), 
        
        DEFAULT(3, new String[] { "" });

        private int      level;
        private String[] words;

        WordsPriority(int level, String[] words) {
            this.level = level;
            this.words = words;
        }

        public int getLevel() {
            return level;
        }

        public String[] getWords() {
            return words;
        }

        /**
         * TODO 获取关键词的优先级
         *
         * @param content
         * @return
         */
        public static int getLevel(String content) {
            if (StringUtils.isEmpty(content)) {
                {
                    return WordsPriority.DEFAULT.getLevel();
                }
            }

            for (WordsPriority wp : WordsPriority.values()) {
                for (String w : wp.getWords()) {
                    if (content.contains(w)) {
                        {
                            return wp.getLevel();
                        }
                    }
                }

            }

            return WordsPriority.DEFAULT.getLevel();
        }

    }

}
