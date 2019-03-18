package com.huashi.mms.template.constant;

/**
 * 彩信模板枚举定义
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年3月15日 下午2:36:44
 */
public class MmsTemplateContext {

    /**
     * 无需检验模板模板ID标识
     */
    public static final long SUPER_TEMPLATE_ID       = 0L;

    /**
     * 默认短信提交间隔数（同一手机号码）
     */
    public static final int  DEFAULT_SUBMIT_INTERVAL = 30;

    /**
     * 默认同一手机号码同一天提交上限次数
     */
    public static final int  DEFAULT_LIMIT_TIMES     = 10;

    /**
     * TODO 审批状态
     * 
     * @author zhengying
     * @version V1.0
     * @date 2016-01-17 下午03:20:35
     */
    public enum ApproveStatus {
        WAITING(0, "待审批"), PROCESSING(1, "处理中"), SUCCESS(2, "审批通过"), FAIL(3, "审批失败");

        private int    value;
        private String title;

        private ApproveStatus(int value, String title) {
            this.value = value;
            this.title = title;
        }

        public static ApproveStatus parse(int value) {
            for (ApproveStatus as : ApproveStatus.values()) {
                if (as.getValue() == value) {
                    {
                        return as;
                    }
                }
            }
            return null;
        }

        public int getValue() {
            return value;
        }

        public String getTitle() {
            return title;
        }
    }

    /**
     * TODO 模板多媒体类型
     * 
     * @author zhengying
     * @version V1.0
     * @date 2019年3月15日 下午3:11:11
     */
    public enum MediaType {
        TEXT("text", "文本"), IMAGE("image", "图片"), AUDIO("audio", "音频"), VIDEO("video", "视频");

        private String code;
        private String title;

        private MediaType(String code, String title) {
            this.code = code;
            this.title = title;
        }

        public String getCode() {
            return code;
        }

        public String getTitle() {
            return title;
        }

        public static boolean isTypeRight(String code) {
            for (MediaType mediaType : MediaType.values()) {
                if (mediaType.getCode().equals(code)) {
                    return true;
                }
            }

            return false;
        }
    }

}
