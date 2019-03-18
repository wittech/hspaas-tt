package com.huashi.common.settings.context;

import com.huashi.constants.CommonContext.PlatformType;

/**
 *
 * 参数配置枚举类
 *
 * @author tenx
 */
public class SettingsContext {

    /**
     * TODO 系统参数类型
     *
     * @author zhengying
     * @version V1.0.0
     * @date 2016年6月30日 下午11:43:24
     */
    public enum SystemConfigType {

        // 用户注册/平台开户 余额初始化设置
        USER_REGISTER_BALANCE("余额初始化设置"),

        // 用户注册/平台开户 流量折扣默认值
        USER_REGISTER_FLUX_DISCOUNT("流量折扣默认值设置"),

        // 站内消息通知模板
        NOTIFICATION_MESSAGE_TEMPLATE("站内消息通知模板"),

        // 短信首条默认计费字数
        SMS_WORDS_PER_NUM("短信收条默认计费字数"),

        // 注册用户默认通道组
        USER_DEFAULT_PASSAGE_GROUP("注册用户默认通道组"),

        // 通道测试用户
        PASSAGE_TEST_USER("通道测试用户"),

        // 告警用户
        SMS_ALARM_USER("短信告警用户"),

        // 正则表达式配置（目前主要用于 运营商）
        REGULAR_EXPRESSION("正则表达式"),

        // 词汇库
        WORDS_LIBRARY("词汇库");

        private String title;

        SystemConfigType(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    /**
     * TODO 默认通道组KEY定义
     *
     * @author zhengying
     * @version V1.0
     * @date 2016年11月8日 下午6:49:02
     */
    public enum UserDefaultPassageGroupKey {

        // 短信默认通道组
        SMS_DEFAULT_PASSAGE_GROUP,

        // 流量默认通道组
        FS_DEFAULT_PASSAGE_GROUP,

        // 语音默认通道组
        VS_DEFAULT_PASSAGE_GROUP;

        public static String key(int platformType) {
            if (PlatformType.SEND_MESSAGE_SERVICE.getCode() == platformType) {
                return UserDefaultPassageGroupKey.SMS_DEFAULT_PASSAGE_GROUP
                        .name().toLowerCase();
            } else if (PlatformType.FLUX_SERVICE.getCode() == platformType) {
                return UserDefaultPassageGroupKey.FS_DEFAULT_PASSAGE_GROUP
                        .name().toLowerCase();
            } else if (PlatformType.VOICE_SERVICE.getCode() == platformType) {
                return UserDefaultPassageGroupKey.VS_DEFAULT_PASSAGE_GROUP
                        .name().toLowerCase();
            }

            return null;
        }
    }

    /**
     * TODO 站内消息通知模板类型
     *
     * @author zhengying
     * @version V1.0.0
     * @date 2016年7月3日 上午12:34:07
     */
    public enum NotificationMessageTemplateType {

        REGISTER_SUCCESS(1, "欢迎您加入华时融合平台！", "您于%s开户成功，欢迎您加入华时融合平台！"), USER_BALACE_CHANGE(
                2, "余额变更通知", "您的%s余额变更%s"), USER_ACCOUNT_CHANGE(3, "站内金额变更通知",
                "您的站内金额增加%s元"), USER_MOBILE_CHANGE(4, "手机号码变更",
                "您的手机号码由[%s]变更为[%s]");
        // MESSAGE_TEMPLATE_APPROVE(5, "短信模板报备结果"， "您的短信模板");

        private int code;
        private String title;
        private String content;

        public int getCode() {
            return code;
        }

        public String getTitle() {

            return title;
        }

        public String getContent() {
            return content;
        }

        NotificationMessageTemplateType(int code, String title,
                                                String content) {
            this.code = code;
            this.title = title;
            this.content = content;
        }

    }

    /**
     * TODO 推送回执配置类型
     *
     * @author zhengying
     * @version V1.0.0
     * @date 2016年7月3日 上午12:03:44
     */
    public enum PushConfigType {
        // 短信状态报告(下行)
        SMS_STATUS_REPORT(1),

        // 短信上行回执报告
        SMS_MO_REPORT(2),

        // 流量充值报告
        FS_CHARGE_REPORT(3),

        // 语音发送报告
        VS_SEND_REPORT(4),
        
        // 彩信状态报告(下行)
        MMS_STATUS_REPORT(5),

        // 彩信上行回执报告
        MMS_MO_REPORT(6);

        private int code;

        public int getCode() {
            return code;
        }

        private PushConfigType(int code) {
            this.code = code;
        }

    }

    /**
     * TODO 推送配置状态
     *
     * @author zhengying
     * @version V1.0
     * @date 2016年10月11日 下午2:32:16
     */
    public enum PushConfigStatus {
        NO(0, "无效"), YES_WITH_CONSTANT(1, "固定推送地址"), YES_WITH_POST(2, "不设置固定推送地址"),;

        private int code;
        private String title;

        public int getCode() {
            return code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        PushConfigStatus(int code, String title) {
            this.code = code;
            this.title = title;
        }

    }

    /**
     * 针对各种环境下的user_id KEY命名（如测试通道用户，告警通道用户等）
     */
    public static final String USER_ID_KEY_NAME = "user_id";


    /**
     * TODO 词汇库详细
     *
     * @author zhengying
     * @version V1.0.0
     * @date 2017年6月17日 下午7:23:12
     */
    public enum WordsLibrary {

        // 短信上行回复词汇库加入 黑名单
        BLACKLIST,

        // 敏感词便签
        FORBIDDEN_LABEL
    }

    /**
     * 针对一条配置存在多个值分隔符（默认，可根据特殊情况另行自定义）
     */
    public static final String MULTI_VALUE_SEPERATOR = ",";
}
