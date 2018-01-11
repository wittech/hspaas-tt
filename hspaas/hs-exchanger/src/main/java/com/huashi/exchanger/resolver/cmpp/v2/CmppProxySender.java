package com.huashi.exchanger.resolver.cmpp.v2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.huashi.common.user.context.UserBalanceConstant;
import com.huashi.common.util.DateUtil;
import com.huashi.common.util.MobileNumberCatagoryUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.cmpp.constant.CmppConstant;
import com.huashi.exchanger.service.ISmsProxyManageService;
import com.huashi.exchanger.service.SmsProxyManageService;
import com.huashi.exchanger.template.handler.RequestTemplateHandler;
import com.huashi.exchanger.template.vo.TParameter;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.service.ISmsMoMessageService;
import com.huashi.sms.record.service.ISmsMtDeliverService;
import com.huashi.sms.task.context.TaskContext.MessageSubmitStatus;
import com.huawei.insa2.comm.cmpp.message.CMPPDeliverMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPSubmitMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPSubmitRepMessage;

@Component
public class CmppProxySender {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Reference
	private ISmsMtDeliverService smsMtDeliverService;
	@Reference
	private ISmsMoMessageService smsMoMessageService;

	@Autowired
	private ISmsProxyManageService smsProxyManageService;
	
	private final Object lock = new Object();
	
	/**
	 * 对于Proxy分发短信的接收
	 * 
	 * @param msg
	 */
	public void doProcessDeliverMessage(CMPPMessage msg) {
		CMPPDeliverMessage deliverMsg = (CMPPDeliverMessage) msg;
//		logger.info("回执，{}", JSON.toJSONString(msg));
		// deliverMsg.getRegisteredDeliver()等于0时，为上行短信；等于1时为状态报告
		if (deliverMsg.getRegisteredDeliver() == 0) {
            moReceive(deliverMsg);
        } else {
            mtDeliver(deliverMsg);
        }
	}

	/**
	 * 
	 * TODO 转义回执的消息ID（包括下行状态和上行回复信息）
	 * 
	 * @param bt
	 * @return
	 */
	public static String getMsgId(byte[] bt) {
		int mask = 0xf0;
		int month = (bt[0] & mask) >> 4;

		mask = 0xF;
		int date = (bt[0] & mask) << 1;
		mask = 0x80;
		int date2 = bt[1] >> 7;
		date = (date & (date2 | 0x1E));

		// 01111100
		mask = 0x7C;
		int hour = (bt[1] & mask) >> 2;

		mask = 0x3;
		int minute = (bt[1] & mask) << 4;
		minute |= (bt[2] & 0xF0) >> 4;

		mask = 0xf;
		int second = (bt[2] & mask) << 2;
		second |= (bt[3] & 0xff) >> 6;

		mask = 0x3F;
		int Msg_Id = (bt[3] & mask) << 16;
		Msg_Id |= (bt[4] & 0xFF) << 8;
		Msg_Id |= (bt[5] & 0xFF);

		mask = 0xFF;
		int seq_id = (bt[6] & mask) << 8;
		seq_id |= (bt[7] & mask);

		StringBuffer sb = new StringBuffer();
		sb.append(month).append(date + 1).append((hour < 10) ? "0" + hour : hour)
				.append(minute < 10 ? "0" + minute : minute).append(second < 10 ? "0" + second : second).append(Msg_Id)
				.append(seq_id);
		return sb.toString();
	}

	/**
	 * 长短信计数器
	 */
	private final AtomicInteger longTextCounter = new AtomicInteger(1);

	/**
	 * 短信内容转换为 字节数。普通短信 GBK编码。长短信 UCS2编码
	 * 
	 * @param message
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private List<byte[]> getLongByte(String message, Integer feeByWords) throws UnsupportedEncodingException {
		List<byte[]> list = new ArrayList<byte[]>();
		byte[] messageUCS2 = message.getBytes("UnicodeBigUnmarked");
		// int messageGBKLen = messageGBK.length;// 短信长度
		// 超过70字符以长短信发送
		if (message.length() > 70) {
			// if (messageGBKLen > maxMessageGBKLen) {
			// 如果字节数大于 GBK，则使用UCS2编码以长短信发送
			// byte[] messageUCS2 = message.getBytes("UnicodeBigUnmarked");
			int messageUCS2Len = messageUCS2.length;
			int yushu = messageUCS2Len % (CmppConstant.MAX_MESSAGE_UCS2_LENGTH - 6);
			int add = 0;
			if (yushu > 0) {
				add = 1;
			}
			int messageUCS2Count = messageUCS2Len / (CmppConstant.MAX_MESSAGE_UCS2_LENGTH - 6) + add;// 长短信分为多少条发送

			byte[] tp_udhiHead = new byte[6];
			tp_udhiHead[0] = 0x05;
			tp_udhiHead[1] = 0x00;
			tp_udhiHead[2] = 0x03;
			tp_udhiHead[3] = (byte) longTextCounter.get();// 0x0A
			tp_udhiHead[4] = (byte) messageUCS2Count;
			tp_udhiHead[5] = 0x01;// 默认为第一条
			if (longTextCounter.incrementAndGet() == 256) {
				longTextCounter.set(1);
			}

			for (int i = 0; i < messageUCS2Count; i++) {
				tp_udhiHead[5] = (byte) (i + 1);
				byte[] msgContent;
				if (i != messageUCS2Count - 1) {// 不为最后一条
					msgContent = byteAdd(tp_udhiHead, messageUCS2, i * (CmppConstant.MAX_MESSAGE_UCS2_LENGTH - 6),
							(i + 1) * (CmppConstant.MAX_MESSAGE_UCS2_LENGTH - 6), i, feeByWords);
					list.add(msgContent);
				} else {
					msgContent = byteAdd(tp_udhiHead, messageUCS2, i * (CmppConstant.MAX_MESSAGE_UCS2_LENGTH - 6),
							messageUCS2Len, i, feeByWords);
					list.add(msgContent);
				}
				// logger.debug("msgContent "+new
				// String(msgContent,"UnicodeBigUnmarked"));
			}
		} else {
			if(isSignatureBelongtoPointCustomer(feeByWords)){
				list.add(message.substring(UserBalanceConstant.WORDS_SIZE_PER_NUM-feeByWords).getBytes("GBK"));
			}else{
				list.add(message.getBytes("GBK"));
			}

		}
		return list;
	}
	
	/**
	 * 
	   * TODO 是否为一客一签模式
	   * 
	   * @param feeByWords
	   * @return
	 */
	private static boolean isSignatureBelongtoPointCustomer(Integer feeByWords) {
		return feeByWords != null && feeByWords != UserBalanceConstant.WORDS_SIZE_PER_NUM;
	}

	private byte[] byteAdd(byte[] tpUdhiHead, byte[] messageUCS2, int i, int j, int index, Integer feeByWords)
			throws UnsupportedEncodingException {

		// 如果为一客一签并且为第一条短信则预留签名字节数
		if (isSignatureBelongtoPointCustomer(feeByWords) && index == 0) {
			int signLength = (UserBalanceConstant.WORDS_SIZE_PER_NUM - feeByWords) * 2;
			i = i + signLength;
			// 移动872通道需要 第一段预留 字节,截取去掉 签名字节
			byte[] msgb = new byte[j - i + 6];
			System.arraycopy(tpUdhiHead, 0, msgb, 0, 6);
			System.arraycopy(messageUCS2, i, msgb, 6, j - i);
			return msgb;
		}

		byte[] msgb = new byte[j - i + 6];
		System.arraycopy(tpUdhiHead, 0, msgb, 0, 6);
		System.arraycopy(messageUCS2, i, msgb, 6, j - i);
		return msgb;
	}

	/**
	 * 
	 * TODO 发送短信接口
	 * 
	 * @param parameter
	 *            通道参数信息
	 * @param extNumber
	 *            客户扩展号码
	 * @param mobile
	 *            手机号码（支持多个）
	 * @param content
	 *            短信内容
	 * @return
	 */
	public List<ProviderSendResponse> send(SmsPassageParameter parameter, String extNumber, String mobile,
			String content) {
		try {
			TParameter tparameter = RequestTemplateHandler.parse(parameter.getParams());
			if (MapUtils.isEmpty(tparameter)) {
                throw new RuntimeException("CMPP 参数信息为空");
            }

			CmppManageProxy cmppManageProxy = getCmppManageProxy(parameter);
			if (cmppManageProxy == null) {
				logger.error("CMPP代理获取失败，手机号码：{}， 短信内容：{}，扩展号码：{}", mobile, content, extNumber);

				return null;
			}

			// 接入号码（如果扩展号码不为空，则加入扩展号码）
			String srcTerminalId = tparameter.getString("src_terminal_id")
					+ (StringUtils.isEmpty(extNumber) ? "" : extNumber);

			// 获取发送回执信息
			CMPPSubmitRepMessage submitRepMsg = getCMPPSubmitResponseMessage(tparameter.getString("service_id"),
					tparameter.getString("spid"), StringUtils.isEmpty(tparameter.getString("msg_fmt")) ? CmppConstant.MSG_FMT_GBK : Integer.parseInt(tparameter.getString("msg_fmt")),
					StringUtils.isEmpty(tparameter.getString("mobile")) ? "000" : tparameter.getString("mobile"),
					srcTerminalId, mobile, content, "", cmppManageProxy, parameter.getFeeByWords());
			
			if(submitRepMsg == null) {
				logger.error("CMPPSubmitRepMessage 网关提交信息为空");
				smsProxyManageService.plusSendErrorTimes(parameter.getPassageId());
				return null;
			}

			List<ProviderSendResponse> list = new ArrayList<>();
			ProviderSendResponse response = new ProviderSendResponse();
			
			if (submitRepMsg.getResult() == MessageSubmitStatus.SUCCESS.getCode()) {
				// 发送成功清空
				smsProxyManageService.clearSendErrorTimes(parameter.getPassageId());
				
				response.setMobile(mobile);
				response.setStatusCode(submitRepMsg.getResult() + "");
				response.setSid(getMsgId(submitRepMsg.getMsgId()));
				response.setSuccess(true);
				response.setRemark(String.format("{msgId:%s, sequenceId:%d, commandId:%d}", response.getSid(),
						submitRepMsg.getSequenceId(), submitRepMsg.getCommandId()));

				list.add(response);
			} else {
				response.setMobile(mobile);
				response.setStatusCode(submitRepMsg.getResult() + "");
				response.setSid(getMsgId(submitRepMsg.getMsgId()));
				response.setSuccess(false);
				response.setRemark(submitRepMsg.getSequenceId() + "");

				list.add(response);
			}

			return list;
		} catch (Exception e) {
			// 累加发送错误次数
			smsProxyManageService.plusSendErrorTimes(parameter.getPassageId());
			
			logger.error("CMPP发送失败", e);
			throw new RuntimeException("CMCP发送失败");
		}
	}

	public static void main(String[] args) throws Exception {
		RetryTemplate template = new RetryTemplate();

		SimpleRetryPolicy policy = new SimpleRetryPolicy();
		policy.setMaxAttempts(2);

		template.setRetryPolicy(policy);

		String result = template.execute(new RetryCallback<String, Exception>() {
			@Override
            public String doWithRetry(RetryContext arg0) throws Exception {
				System.out.println(arg0.getRetryCount());
				throw new NullPointerException("nullPointerException");
			}
		}, new RecoveryCallback<String>() {
			@Override
            public String recover(RetryContext context) throws Exception {
				return "recovery callback";
			}
		});
		System.out.println(result);
	}

	/**
	 *
	 * TODO 组装待提交短信CMPP结构体数据
	 * 
	 * @param serviceId
	 *            业务类型
	 * @param spid
	 *            企业代码
	 * @param chargeNumber
	 *            计费号码
	 * @param msgFmt
	 *            消息编码方式（默认GBK）
	 * @param srcTerminalId
	 *            接入号（包含用于配置的扩展号码）
	 * @param mobile
	 *            接收短信手机号码
	 * @param content
	 *            短信内容
	 * @param reserve
	 *            扩展项，用于自定义内容
	 * @param feeByWords
	 *            首条短信计费字数（专用于一客一签判断）
	 * @return
	 * @throws IOException
	 */
	private CMPPSubmitRepMessage getCMPPSubmitResponseMessage(String serviceId, String spid, int msgFmt,
			String chargeNumber, String srcTerminalId, String mobile, String content, String reserve,
			CmppManageProxy cmppManageProxy, Integer feeByWords) throws IOException {

		// 存活有效期（2天）
		Date validTime = new Date(System.currentTimeMillis() + (long) 0xa4cb800);
		// 定时发送时间
		Date atTime = null;// new Date(System.currentTimeMillis() + (long)//
							// 0xa4cb800); //new Date();

		int tpUdhi = CmppConstant.TP_UDHI;
		// 全都改成以 UTF 16 格式提交
		List<byte[]> contentList = getLongByte(content, feeByWords);
		if (contentList.size() > 1) {
			// 长短信格式发送 增加协议头 改变编码方式
			tpUdhi = 1;
			msgFmt = CmppConstant.MSG_FMT_UCS2;
		}

		CMPPSubmitMessage submitMsg = null;
		CMPPSubmitRepMessage submitRepMsg = null;
		for (int index = 1; index <= contentList.size(); index++) {
			submitMsg = getCMPPSubmitMessage(contentList.size(), index, serviceId, tpUdhi, msgFmt, spid, validTime,
					atTime, chargeNumber, srcTerminalId, mobile.split(MobileNumberCatagoryUtil.DATA_SPLIT_CHARCATOR),
					contentList.get(index - 1), reserve);

			CMPPSubmitRepMessage repMsg = (CMPPSubmitRepMessage) cmppManageProxy.send(submitMsg);
			if (index == 1) {
				// 以长短信 拆分发送时，目前状态报告的 msgid 是 拆分后第一条的 msgid
				submitRepMsg = repMsg;
			} else if (submitRepMsg == null) {
				submitRepMsg = repMsg;
			}

			if (submitRepMsg == null) {
				logger.error(" CMPPSubmitRepMessage null, submitMsg : {}", submitMsg);
			} else if (submitRepMsg.getResult() != 0) {
				logger.error(" submitRepMsg result :{}, index : {}, content: {}, mobile : {}", submitRepMsg.getResult(),
						index, content, mobile);
			}
		}

		return submitRepMsg;
	}

	/**
	 * 
	 * TODO 组装CMPP提交信息
	 * 
	 * @param pkTotal
	 *            相同msg_Id消息总条数
	 * @param pkNumber
	 *            相同msg_Id的消息序号
	 * @param serviceId
	 *            业务类型
	 * @param tpUdhi
	 *            GSM协议类型
	 * @param msgFmt
	 *            消息格式
	 * @param spid
	 * @param validTime
	 * @param atTime
	 * @param chargeNumber
	 *            计费号码
	 * @param srcTerminalId
	 *            源终端MSISDN号码（包含扩展号码）
	 * @param mobiles
	 *            手机号码
	 * @param msgContent
	 *            短信内容
	 * @param reserve
	 *            保留
	 * @return
	 */
	private CMPPSubmitMessage getCMPPSubmitMessage(int pkTotal, int pkNumber, String serviceId, int tpUdhi, int msgFmt,
			String spid, Date validTime, Date atTime, String chargeNumber, String srcTerminalId, String[] mobiles,
			byte[] msgContent, String reserve) {
		return new CMPPSubmitMessage(pkTotal, pkNumber, CmppConstant.REGISTERED_DELIVERY, CmppConstant.MSG_LEVEL,
				serviceId, CmppConstant.FEE_USERTYPE, chargeNumber, CmppConstant.TP_PID, tpUdhi, msgFmt, spid,
				CmppConstant.FEE_TYPE, CmppConstant.FEE_CODE, validTime, atTime, srcTerminalId, mobiles, msgContent,
				reserve);
	}

	/**
	 * 
	 * TODO 获取CMPP 代理类
	 * 
	 * @param parameter
	 * @param extNumber
	 * @return
	 */
	public CmppManageProxy getCmppManageProxy(SmsPassageParameter parameter) {
	   synchronized (lock) {
	       if (smsProxyManageService.isProxyAvaiable(parameter.getPassageId())) {
               return (CmppManageProxy) SmsProxyManageService.getManageProxy(parameter.getPassageId());
           }

           boolean isOk = smsProxyManageService.startProxy(parameter);
           if (!isOk) {
               return null;
           }
           
           // 重新初始化后将错误计数器归零
           smsProxyManageService.clearSendErrorTimes(parameter.getPassageId());

           return (CmppManageProxy) SmsProxyManageService.getManageProxy(parameter.getPassageId());
	   }
	}

	/**
	 * 
	 * TODO CMPP下行状态报文
	 * 
	 * @param report
	 * @return
	 */
	public void mtDeliver(CMPPDeliverMessage report) {
		if (report == null) {
            return;
        }

		try {
//			logger.info("CMPP状态报告数据: {}", report);

			// 发送时手机号码拼接86，回执需去掉86前缀
			String mobile = report.getSrcterminalId();
			if (mobile != null && mobile.startsWith("86")) {
				mobile = mobile.substring(2);
			}

			List<SmsMtMessageDeliver> list = new ArrayList<>();

			SmsMtMessageDeliver response = new SmsMtMessageDeliver();
			response.setMsgId(getMsgId(report.getStatusMsgId()));
			response.setMobile(mobile);
			response.setCmcp(CMCP.local(response.getMobile()).getCode());
			response.setStatusCode(report.getStat());
			response.setStatus((StringUtils.isNotEmpty(report.getStat())
					&& CmppConstant.COMMON_MT_STATUS_SUCCESS_CODE.equalsIgnoreCase(report.getStat())
							? DeliverStatus.SUCCESS.getValue() : DeliverStatus.FAILED.getValue()));
			response.setDeliverTime(DateUtil.getNow());
			response.setCreateTime(new Date());
			response.setRemark(
					String.format("DestnationId:%s,ServiceId:%s", report.getDestnationId(), report.getServiceId()));

			list.add(response);

			if (CollectionUtils.isNotEmpty(list)) {
                smsMtDeliverService.doFinishDeliver(list);
            }

			// 解析返回结果并返回
		} catch (Exception e) {
			logger.error("CMPP状态回执解析失败：{}", e);
			throw new RuntimeException("CMPP状态回执解析失败");
		}
	}

	/**
	 * 
	 * TODO CMPP上行报文回执
	 * 
	 * @param report
	 */
	public void moReceive(CMPPDeliverMessage report) {
		if (report == null) {
            return;
        }

		try {

			logger.info("CMPP上行报告数据: {}", report);

			List<SmsMoMessageReceive> list = new ArrayList<>();

			// 发送时手机号码拼接86，回执需去掉86前缀
			String mobile = report.getSrcterminalId();
			if (mobile != null && mobile.startsWith("86")) {
				mobile = mobile.substring(2);
			}

			SmsMoMessageReceive response = new SmsMoMessageReceive();
			response.setPassageId(null);
			response.setMsgId(getMsgId(report.getMsgId()));
			response.setMobile(mobile);
			response.setDestnationNo(report.getDestnationId());
			response.setReceiveTime(DateUtil.getNow());
			response.setCreateTime(new Date());
			response.setCreateUnixtime(response.getCreateTime().getTime());
			// 编号方式
			if (CmppConstant.MSG_FMT_UCS2 == report.getMsgFmt()) {
				response.setContent(new String(report.getMsgContent(), "UTF-16"));
			} else {
				response.setContent(new String(report.getMsgContent(), "GBK"));
			}

			list.add(response);

			if (CollectionUtils.isNotEmpty(list)) {
                smsMoMessageService.doFinishReceive(list);
            }

		} catch (Exception e) {
			logger.error("CMPP上行解析失败 {}", JSON.toJSONString(report), e);
			throw new RuntimeException("CMPP上行解析失败");
		}
	}

	/***
	 * 
	   * TODO 网关断开下发通知
	   * 
	   * @param passageId
	 */
	public void onTerminate(Integer passageId) {
		logger.info("Cmpp onTerminate, passageId : {} ", passageId);
		Object myProxy = SmsProxyManageService.getManageProxy(passageId);
		if(myProxy != null){
			((CmppManageProxy)myProxy).close();
			logger.info("myProxy.getConnState() : {}, passageId : {} ", ((CmppManageProxy)myProxy).getConnState(), passageId);
			SmsProxyManageService.GLOBAL_PROXIES.remove(passageId);
		}
	}
}
