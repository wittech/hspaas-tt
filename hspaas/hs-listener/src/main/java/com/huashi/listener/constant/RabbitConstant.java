package com.huashi.listener.constant;

/**
 * TODO rabbitmq常量相关
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年12月26日 下午2:33:29
 */
public class RabbitConstant {

    /*-----------------------------------短信下行队列-----------------------------------------*/

    /**
     * 短信下行 已完成上家通道调用，待网关回执队列
     */
    public static final String MQ_SMS_MT_WAIT_RECEIPT = "mq_sms_mt_wait_receipt";

    /*-----------------------------------短信上行队列----------------------------------------*/

    /**
     * 短信上行回执数据
     */
    public static final String MQ_SMS_MO_RECEIVE      = "mq_sms_mo_receive";

    /*-----------------------------------彩信下行队列-----------------------------------------*/

    /**
     * 彩信下行 已完成上家通道调用，待网关回执队列
     */
    public static final String MQ_MMS_MT_WAIT_RECEIPT = "mq_mms_mt_wait_receipt";

    /*-----------------------------------短信上行队列----------------------------------------*/

    /**
     * 彩信上行回执数据
     */
    public static final String MQ_MMS_MO_RECEIVE      = "mq_mms_mo_receive";

    /**
     * 彩信模板报备回执数据
     */
    public static final String MQ_MMS_MODEL_RECEIVE   = "mq_mms_model_receive";

}
