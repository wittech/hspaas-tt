package com.huashi.constants;

import com.huashi.common.util.PatternUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
  * TODO 通用枚举类
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年12月28日 下午10:40:25
 */
public class CommonContext {

    /**
     * TODO 三大运营商
     *
     * @author zhengying
     * @version V1.0
     * @date 2016年8月9日 下午3:36:01
     */
    public enum CMCP {
        UNRECOGNIZED(0, "无法识别", null), CHINA_MOBILE(1, "移动", "^((134|135|136|137|138|139|150|151|152|157|158|159|182|183|184|187|188|178|147|198)[0-9]{8}|1705[0-9]{7})$"), CHINA_TELECOM(2, "电信", "^((133|149|153|180|181|189||173|177|199)[0-9]{8}|(1700|1701)[0-9]{7})$"), CHINA_UNICOM(3, "联通", "^((130|131|132|155|156|185|186|175|176|145|166)[0-9]{8}|(1709|1718|1719|1707|1708)[0-9]{7})$"), GLOBAL(4, "全网", "^(13[0-9]|15[012356789]|166|17[05678]|18[0-9]|14[579]|19[89])[0-9]{8}$");

        CMCP(int code, String title, String localRegex) {
            this.code = code;
            this.title = title;
            this.localRegex = localRegex;
        }

        private int code;
        private String title;
        private String localRegex;

        public int getCode() {
            return code;
        }

        public String getTitle() {
            return title;
        }

        public String getLocalRegex() {
            return localRegex;
        }

        public static CMCP getByCode(int code) {
            for (CMCP cmcp : CMCP.values()) {
                if (cmcp.getCode() == code) {
                    return cmcp;
                }
            }
            return CMCP.UNRECOGNIZED;
        }

        /**
         * TODO 获取手机号码归属运营商
         *
         * @param mobileNumber
         * @return
         */
        public static CMCP local(String mobileNumber) {
            if (StringUtils.isEmpty(mobileNumber)) {
                {
                    return CMCP.UNRECOGNIZED;
                }
            }

            for (CMCP cmcp : CMCP.values()) {
                if (PatternUtil.isRight(cmcp.getLocalRegex(), mobileNumber)) {
                    {
                        return cmcp;
                    }
                }
            }
            return CMCP.UNRECOGNIZED;
        }

        /**
         * TODO 是否为有效的手机号码
         *
         * @param mobileNumber
         * @return
         */
        public static boolean isAvaiableMobile(String mobileNumber) {
            if (StringUtils.isEmpty(mobileNumber)) {
                {
                    return false;
                }
            }

            return PatternUtil.isRight(CMCP.GLOBAL.getLocalRegex(), mobileNumber);
        }

    }

    public static void main(String[] args) {
        System.out.println(CMCP.local("13385819856").getTitle());
    }

    /**
     * TODO 平台类型
     *
     * @author zhengying
     * @version V1.0.0
     * @date 2016年8月28日 下午7:22:48
     */
    public enum PlatformType {
        UNDEFINED(0, "未定义"), SEND_MESSAGE_SERVICE(1, "短信服务"), FLUX_SERVICE(2, "流量服务"), VOICE_SERVICE(3, "语音服务");

        private int code;
        private String name;

        private PlatformType(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static PlatformType parse(int code) {
            for (PlatformType pt : PlatformType.values()) {
                if (pt.getCode() == code) {
                    {
                        return pt;
                    }
                }
            }
            return null;
        }

        /**
         * TODO 所有的代码值
         *
         * @return
         */
        public static List<Integer> allCodes() {
            List<Integer> all = new ArrayList<Integer>();
            for (PlatformType pt : PlatformType.values()) {
                if (pt == PlatformType.UNDEFINED) {
                    {
                        continue;
                    }
                }
                all.add(pt.getCode());
            }
            return all;
        }
    }

    /**
     * TODO 推送回执URL类型
     *
     * @author zhengying
     * @version V1.0.0
     * @date 2016年9月5日 下午9:57:52
     */
    public enum CallbackUrlType {
        SMS_STATUS(1, "短信状态报告"), SMS_MO(2, "短信上行报告"), FLUX_CHARGE_RESULT(3, "流量充值结果"), VOICE_SEND_STATUS(4, "语音验证码发送报告");

        private int code;
        private String name;

        private CallbackUrlType(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static PlatformType parse(int code) {
            for (PlatformType pt : PlatformType.values()) {
                if (pt.getCode() == code) {
                    {
                        return pt;
                    }
                }
            }
            return null;
        }
    }

    /**
     * TODO 调用通道函数类型
     *
     * @author zhengying
     * @version V1.0.0
     * @date 2016年9月12日 上午12:28:20
     */
    public enum PassageCallType {
        DATA_SEND(1, "数据发送"), STATUS_RECEIPT_WITH_PUSH(2, "状态报告网关推送"), STATUS_RECEIPT_WITH_SELF_GET(3, "状态回执自取"), SMS_MO_REPORT_WITH_PUSH(4, "短信上行推送"), SMS_MO_REPORT_WITH_SELF_GET(5, "短信上行自取"), PASSAGE_BALANCE_GET(6, "通道余额查询");

        private int code;
        private String name;

        private PassageCallType(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static PassageCallType parse(int code) {
            for (PassageCallType pt : PassageCallType.values()) {
                if (pt.getCode() == code) {
                    {
                        return pt;
                    }
                }
            }
            return null;
        }
    }

    /**
     * TODO 应用类型
     *
     * @author zhengying
     * @version V1.0
     * @date 2016年9月18日 下午2:53:59
     */
    public enum AppType {
        WEB(1, "融合WEB平台"), DEVELOPER(2, "开发者平台"), BOSS(3, "运营支撑系统");

        private int code;
        private String name;

        private AppType(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static AppType parse(int code) {
            for (AppType at : AppType.values()) {
                if (at.getCode() == code) {
                    {
                        return at;
                    }
                }
            }
            return AppType.WEB;
        }
    }

    /**
     * TODO 通道调用协议
     * <p>
     * 1、与CMPP/SGIP协议的差异
     * 1）协议定义比CMPP和SGIP严谨和规范，虽然CMPP和SGIP都是从SMPP派生出来的。
     * 2）CMPP和SGIP中有大量的关于计费的定义，SMPP没有考虑这部分内容。这完全反映了通过短信实现的移动增值业务模式在国内的成熟和流行。
     * 3）SMPP的网络承载层可以是TCP/IP和X.25。
     * 2、SMPP协议解决的是移动网络之外的短消息实体与短消息中心的交互问题。即允许移动网络之外的短消息实体（External Short Message Entities,ESMEs）连接短消息中心（SMSC）来提交和接受短消息。
     * 3、SMPP协议定义的是1）ESME和SMSC之间交互的一组操作和2）ESMS与SMSC交互操作中的数据格式。
     * 4、任何SMPP操作都包含请求PDU（Request Protocol Data Unit）和与之对应的回应PDU（Response Protocol Data Unit）。
     * 5、SMPP把ESMEs分类为Transmitter/Receiver/Transceiver三种交互方式，分别对应仅提交短消息/仅接收短消息/提交和接收短消息三种形态。
     * 6、SMPP会话有5种状态：OPEN / BOUND_TX / BOUND_RX / BOUND_TRX / CLOSED
     * 7、SMPP定义的PDUs包括：
     *
     * @author zhengying
     * @version V1.0
     * @date 2016年9月27日 下午5:53:13
     */
    public enum ProtocolType {
        // HTTP连接
        HTTP,

        WEBSERVICE,

        // 中国移动通信互联网短信网关接口协议，目前分为2.0 和 3.0版本（China Mobile Peer to Peer）
        CMPP2, CMPP3,

        // 中国联合通信公司短消息网关系统接口协议（Short Message Gateway Interface Protocol0）V1.2
        SGIP,

        // SMGW 与其它网元设备（除SMC 外）进行短消息传输的接口协议 （Short Message Gateway Protocol）
        SMGP,

        // 中国网通CNGP协议2.0版 ，本标准描述了PHS短消息网关（SMGW）和服务提供商（SP）之间、短消息网关（SMGW）和短消息网关（SMGW）之间的通信协议（China Netcom Short Message Gateway Protocol）
        CNGP,

        // SMPP（short message peer to peer）协议是一个开放的消息转换协议
        SMPP;

        public static ProtocolType parse(String name) {
            if (StringUtils.isEmpty(name)) {
                return null;
            }

            for (ProtocolType pt : ProtocolType.values()) {
                if (pt.name().equals(name)) {
                    return pt;
                }
            }
            return null;
        }

        /**
         * TODO 是否属于直连协议
         *
         * @param protocol
         * @return
         */
        public static boolean isBelongtoDirect(String protocol) {
            if (StringUtils.isEmpty(protocol)) {
                {
                    return false;
                }
            }

            return !protocol.equalsIgnoreCase(ProtocolType.HTTP.name()) && !protocol.equalsIgnoreCase(ProtocolType.WEBSERVICE.name());
        }
    }

}
