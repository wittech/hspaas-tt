package com.huashi.developer.constant;

import org.apache.commons.lang3.StringUtils;

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
     * 短信下行 已完成前置校验（包括用户授权、余额校验等），待处理归正逻辑队列
     */
    public static final String MQ_SMS_MT_WAIT_PROCESS     = "mq_sms_mt_wait_process";

    /**
     * 短信点对点短信下行 已完成前置校验（包括用户授权、余额校验等），待处理归正逻辑队列
     */
    public static final String MQ_SMS_MT_P2P_WAIT_PROCESS = "mq_sms_mt_p2p_wait_process";

    /**
     * 彩信下行任务提交队列
     */
    public static final String MQ_MMS_MT_WAIT_PROCESS     = "mq_mms_mt_wait_process";

    /**
     * TODO 关键词优先级
     *
     * @author zhengying
     * @version V1.0.0
     * @date 2016年10月4日 下午10:20:05
     */
    public enum WordsPriority {
        L10(10, new String[] { "验证码", "动态码", "校验码" }), L5(5, new String[] { "分钟", "" }),
        L1(1, new String[] { "回复TD" }), DEFAULT(3, new String[] { "" });

        private int      level;
        private String[] words;

        private WordsPriority(int level, String[] words) {
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
                return WordsPriority.DEFAULT.getLevel();
            }

            for (WordsPriority wp : WordsPriority.values()) {
                for (String w : wp.getWords()) {
                    if (content.contains(w)) {
                        return wp.getLevel();
                    }
                }

            }

            return WordsPriority.DEFAULT.getLevel();
        }

    }

}
