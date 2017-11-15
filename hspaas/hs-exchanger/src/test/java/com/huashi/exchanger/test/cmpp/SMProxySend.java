package com.huashi.exchanger.test.cmpp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JTextArea;

import com.huawei.insa2.comm.cmpp.message.CMPPCancelMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPCancelRepMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPDeliverMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPSubmitMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPSubmitRepMessage;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.Cfg;

//时间：20081213发送短信
//
public class SMProxySend {
	// 从xml中读取配置信息
	private static Args argsconn; // 读取连接信息
	private static Args argssumbit; // 读取提交信息
	// 基本提交信息参数说明
	private static int pk_Total = 1;
	private static int pk_Number = 1;
	private static int registered_Delivery = 1;
	private static int msg_Level = 1;
	private static String service_Id = "";
	private static int fee_UserType = 2;
	private static String fee_Terminal_Id = "";
	private static int tp_Pid = 0;
	private static int tp_Udhi = 0;
	private static int msg_Fmt = 15;
	private static String msg_Src = "";
	private static String fee_Type = "02";
	private static String fee_Code = "000";
	private static Date valid_Time = null;
	private static Date at_Time = null;
	private static String src_Terminal_Id = "";
	private static String[] dest_Terminal_Id = { "" };
	private static byte[] msg_Content = null;
	private static String reserve = "";

	/** 短信收发接口 */
	public static SMProxyRec myProxy = null;

	/** 应用程序的提示信息处理 * */
	JTextArea txtArea = null;

	// 基本参数设置
	public void ProBaseConf() {
		try {
			// 连接配置信息
			argsconn = new Cfg("Smproxy.xml", false).getArgs("CMPPConnect");
			// argsconn.set("source-addr", "");
			// argsconn.set("shared-secret", "");
			// 初始化短信收发接口
			myProxy = new SMProxyRec(this, argsconn);
			// 提交参数设置
			argssumbit = new Cfg("Smproxy.xml", false)
					.getArgs("CMPPSubmitMessage");
			pk_Total = argssumbit.get("pk_Total", 1);
			pk_Number = argssumbit.get("pk_Number", 1);
			registered_Delivery = argssumbit.get("registered_Delivery", 1);
			msg_Level = argssumbit.get("msg_Level", 1);
			service_Id = argssumbit.get("service_Id", "");
			fee_UserType = argssumbit.get("fee_UserType", 2);
			fee_Terminal_Id = argssumbit.get("fee_Terminal_Id", "");
			tp_Pid = argssumbit.get("tp_Pid", 1);
			tp_Udhi = argssumbit.get("tp_Udhi", 1);
			msg_Fmt = argssumbit.get("msg_Fmt", 15);
			msg_Src = argssumbit.get("msg_Src", "");
			fee_Type = argssumbit.get("fee_Type", "02");
			fee_Code = argssumbit.get("fee_Code", "000");
			src_Terminal_Id = argssumbit.get("src_Terminal_Id", "");
			reserve = argssumbit.get("reserve", "");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 构造方法
	 */
	public SMProxySend() {
		// 处理基本的配置信息
		// ProBaseConf();
	}

	/**
	 * 华为短信发送类处理（公用类）
	 * 
	 * @param 短信类
	 *            ，包括手机号，内容，序号
	 */
	public int SendMessage(String mobiles, String content) {
		// 返回结果
		int result = 0;
		// 发送内容
		try {
			msg_Content = content.getBytes("GBK");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 发送号码
		List<String> telList = new ArrayList<>();
		String[] mobileArray = mobiles.split(";");
		for (int i = 0; i < mobileArray.length; i++) {
			telList.add(mobileArray[i]);
		}
		dest_Terminal_Id = new String[telList.size()];
		for (int i = 0; i < telList.size(); i++) {
			dest_Terminal_Id[i] = telList.get(i).toString();
		}
		// 存活有效期
		valid_Time = new Date(System.currentTimeMillis() + (long) 0xa4cb800); // new
																				// Date();//
		// 定时发送时间
		at_Time = null;// new Date(System.currentTimeMillis() + (long)
						// 0xa4cb800); //new Date();
		// 用户手机上显示为短消息的主叫号码
		// src_Terminal_Id=src_Terminal_Id+"001";

		// 初始化提交信息
		CMPPSubmitMessage submitMsg = new CMPPSubmitMessage(pk_Total,
				pk_Number, registered_Delivery, msg_Level, service_Id,
				fee_UserType, fee_Terminal_Id, tp_Pid, tp_Udhi, msg_Fmt,
				msg_Src, fee_Type, fee_Code, valid_Time, at_Time,
				src_Terminal_Id, dest_Terminal_Id, msg_Content, reserve);
		try {
			CMPPSubmitRepMessage submitRepMsg = (CMPPSubmitRepMessage) myProxy
					.send(submitMsg);
			if (submitRepMsg.getResult() == 0) {
				result = 1;
				System.out.println("发送短信成功msgid:"
						+ submitRepMsg.getMsgId().toString() + "\t SequenceId:"
						+ submitRepMsg.getSequenceId());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 对于Proxy分发短信的接收
	 * 
	 * @param msg
	 */
	public void ProcessRecvDeliverMsg(CMPPMessage msg) {
		CMPPDeliverMessage deliverMsg = (CMPPDeliverMessage) msg;

		//deliverMsg.getRegisteredDeliver()等于0时，为上行短信；等于1时为状态报告
		if (deliverMsg.getRegisteredDeliver() == 0)
			try {
				// 编号方式
				if (deliverMsg.getMsgFmt() == 8) {
					System.out.println(String.valueOf(String
							.valueOf((new StringBuffer("1接收消息: 主叫号码="))
									.append(deliverMsg.getSrcterminalId())
									.append(";内容=")
									.append(new String(deliverMsg
											.getMsgContent(), "UTF-16BE")))));
					txtArea.append("来源手机号："
							+ deliverMsg.getSrcterminalId()
							+ "\r\n"
							+ "接收内容"
							+ new String(deliverMsg.getMsgContent(), "UTF-16BE")
							+ "\r\n");
				}
				// 编号方式GBK
				else {
					System.out.println(String.valueOf(String
							.valueOf((new StringBuffer(
									"2我的客户端fjfdszj接收消息: 主叫号码="))
									.append(deliverMsg.getSrcterminalId())
									.append(";内容=")
									.append(new String(deliverMsg
											.getMsgContent()))
									.append(";destterm=")
									.append(new String(deliverMsg
											.getDestnationId()))
									.append(";serviceid=")
									.append(new String(deliverMsg
											.getServiceId()))
									.append(";tppid=")
									.append(deliverMsg.getTpPid())
									.append(";tpudhi=")
									.append(deliverMsg.getTpUdhi())
									.append(";msgfmt")
									.append(deliverMsg.getMsgFmt())
									.append(";srctermid=")
									.append(new String(deliverMsg
											.getSrcterminalId()))
									.append(";deliver=")
									.append(deliverMsg.getRegisteredDeliver())
									.append(";msgcontent=")
									.append(new String(deliverMsg
											.getMsgContent())))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		else
			System.out.println(String.valueOf(String.valueOf((new StringBuffer(
					"3收到状态报告消息： stat="))
					.append(new String(deliverMsg.getStat()))
					.append("dest_termID=")
					.append(new String(deliverMsg.getDestTerminalId()))
					.append(";destterm=")
					.append(new String(deliverMsg.getDestnationId()))
					.append(";serviceid=")
					.append(new String(deliverMsg.getServiceId()))
					.append(";tppid=").append(deliverMsg.getTpPid())
					.append(";tpudhi=").append(deliverMsg.getTpUdhi())
					.append(";msgfmt").append(deliverMsg.getMsgFmt())
					.append(";srctermid=")
					.append(new String(deliverMsg.getSrcterminalId()))
					.append(";deliver=")
					.append(deliverMsg.getRegisteredDeliver()))));
	}

	public void Terminate() {
		System.out.println("SMC下发终断消息");
		myProxy.close();
		myProxy = null;
	}

	public void SearchMsg() {
		// 删除短信
		byte[] msg_id = "2".getBytes();
		CMPPCancelMessage cancelMsg = new CMPPCancelMessage(msg_id);
		try {
			CMPPCancelRepMessage cancelRepMsg = (CMPPCancelRepMessage) myProxy
					.send(cancelMsg);
			System.out.println("getCommandId" + cancelRepMsg.getCommandId()
					+ "\t getSuccessId" + cancelRepMsg.getSuccessId()
					+ "\tgetSequenceId: " + cancelRepMsg.getSequenceId());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String stateDesc = myProxy.getConnState();
		System.out.println(stateDesc);
	}

	/**
	 * 关闭连接
	 */
	public void Close() {
		// 查询SMProxy与ISMG的TCP连接状态
		// String stateDesc = myProxy.getConnState();
		myProxy.close();
		myProxy = null;
	}

	// 测试方法
	public static void main(String[] args) {
	}
}
