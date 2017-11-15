package com.huashi.exchanger.resolver.cmpp.constant;

public final class CmppConstant {

	// 相同Msg_id消息的总条数，目前只能为1
	public static int PK_TOTAL = 1;
	// 相同Msg_id的消息序号，目前只能为1
	public static int PK_NUMBER = 1;
	// 是否需要状态报告，0:不需要，1:需要，2:产生SMS话单
	public static int REGISTERED_DELIVERY = 1;
	// 信息类别
	public static int MSG_LEVEL = 1;
	
	// 计费用户类型， 0：对目的终端MSISDN计费；
	// 1：对源终端MSISDN计费；2：对SP计费；3：表示本字段无效，对谁计费参见Fee_terminal_Id字段
	public static int FEE_USERTYPE = 2;
	// GSM协议类型
	public static int TP_PID = 0;
	// GSM协议类型
	public static int TP_UDHI = 0;
	
	// 资费类别，“短消息类型”为“发送”，对“计费用户号码”不计信息费，此类话单仅用于核减SP对称的信道费
	// 01计费用户号免费。02按条计费信息费。03对“计费用户号码”按包月收取信息费 04：对“计费用户号码”的信息费封顶
	// 05：对“计费用户号码”的收费是由SP实现
	public static String FEE_TYPE = "02";
	// 资费代码，以分为单位
	public static String FEE_CODE = "000";
	// // 存活有效期(单位:分钟)
	// public static Date valid_Time = null;
	// // 定时发送时间
	// public static Date at_Time = null;
	// 源终端MSISDN号码(为SP的服务代码或前缀, 为服务代码的长号码,
	// 网关将该号码完整的填到SMPP协议相应的destination_address字段，
	// 该号码最终在用户手机上显示为短消息的主叫号码) (没有可以为空)，短信接入号
//	public static String src_Terminal_Id = "";
	// 保留
	public static String RESERVER = "";
	
	public static final int MSG_FMT_GBK = 15;
	public static final int MSG_FMT_UCS2 = 8;
    

	public static final int DEFAULT_VERSION = 32;
    /**
     * GBK编码单条最大字节数
     */
	public static final int MAX_MESSAGE_GBK_LENGTH = 140;
    /**
     * UCS2编码单挑最大字节数
     */
    public static final int MAX_MESSAGE_UCS2_LENGTH = 140;
	
    // 公共状态回执成功码
 	public static final String COMMON_MT_STATUS_SUCCESS_CODE = "DELIVRD";
}
