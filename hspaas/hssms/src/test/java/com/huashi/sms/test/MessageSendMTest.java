package com.huashi.sms.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.support.CorrelationData;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.util.IdGenerator;
import com.huashi.common.util.RandomUtil;
import com.huashi.constants.CommonContext.AppType;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.task.context.MQConstant;
import com.huashi.sms.task.domain.SmsMtTask;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({ "classpath:spring-dubbo-consumer.xml" })
public class MessageSendMTest extends BaseTest {
	
	protected static Logger logger = LoggerFactory.getLogger(MessageSendMTest.class);

	private static SmsMtTask smsMtTask;
	private static ThreadLocal<IdGenerator> idBD = new ThreadLocal<IdGenerator>();
	
	public static long getSid() {
		if(idBD.get() == null) {
			IdGenerator idGenerator = new IdGenerator(1);
			idBD.set(idGenerator);
			return idGenerator.generate();
		}
			
		return idBD.get().generate();
	}
	
	static {
		smsMtTask = new SmsMtTask();
		smsMtTask.setUserId(46);
		smsMtTask.setMobile("138" + RandomUtil.getRandomNum(8));
		smsMtTask.setContent(String.format("【华时科技】您的短信验证码为%s，请尽快完成后续操作。",
				RandomUtil.getRandomNum()));
		smsMtTask.setAttach("test889");
		smsMtTask.setCallback("http://localhost:9999/sms/status_report");
		smsMtTask.setSid(getSid());
		smsMtTask.setAppType(AppType.DEVELOPER.getCode());
		smsMtTask.setExtNumber("21");
		smsMtTask.setCreateTime(new Date());
		smsMtTask.setIp("127.0.0.1");
		smsMtTask.setFee(1);
	}
	
	public static void send() {
		BaseTest base = new BaseTest();

		base.rabbitTemplate().convertAndSend(MQConstant.EXCHANGE_SMS,
				MQConstant.MQ_SMS_MT_WAIT_PROCESS, smsMtTask,
				new MessagePostProcessor() {

					@Override
					public Message postProcessMessage(Message message)
							throws AmqpException {
						message.getMessageProperties().setPriority(10);
						return message;
					}
				}, new CorrelationData(smsMtTask.getSid().toString()));

	}
	
	static AtomicInteger COUNTER = new AtomicInteger(0);
	
	static int counter = 0;
	
	static class SendThread implements Runnable {

		@Override
		public void run() {
			while(true) {
				logger.warn(String.format("计数器值为：%d, 线程：%s", counter, Thread.currentThread().getName()));
				if(COUNTER.incrementAndGet() == 1000) {
					logger.warn("结束.....");
					break;
				}
				
				send();
				counter ++;
			}
			
		}
		
	}
	
	
	
	public static void main(String[] args) {
		
		String aa = "[{\"clearRemark\":\"{\\n&quot;Msg_Id&quot;:&quot;114882387757180532&quot;,\\n&quot;Dest_Id&quot;:&quot;10691231&quot;,\\n&quot;Mobile&quot;:&quot;15387611776&quot;,\\n&quot;Status&quot;:&quot;DELIVRD&quot;\\n}\",\"cmcp\":2,\"createTime\":1488238797632,\"deliverTime\":\"2017-02-28 07:39:57\",\"mobile\":\"15387611776\",\"msgId\":\"114882387757180532\",\"remark\":\"{\\n&quot;Msg_Id&quot;:&quot;114882387757180532&quot;,\\n&quot;Dest_Id&quot;:&quot;10691231&quot;,\\n&quot;Mobile&quot;:&quot;15387611776&quot;,\\n&quot;Status&quot;:&quot;DELIVRD&quot;\\n}\",\"status\":0,\"statusCode\":\"DELIVRD\"}]";
		
//		String ss = "[\"[{\"clearRemark\":\"{\\n&quot;Msg_Id&quot;:&quot;114882387757180532&quot;,\\n&quot;Dest_Id&quot;:&quot;10691231&quot;,\\n&quot;Mobile&quot;:&quot;15387611776&quot;,\\n&quot;Status&quot;:&quot;DELIVRD&quot;\\n}\",\"cmcp\":2,\"createTime\":1488238797632,\"deliverTime\":\"2017-02-28 07:39:57\",\"mobile\":\"15387611776\",\"msgId\":\"114882387757180532\",\"remark\":\"{\\n&quot;Msg_Id&quot;:&quot;114882387757180532&quot;,\\n&quot;Dest_Id&quot;:&quot;10691231&quot;,\\n&quot;Mobile&quot;:&quot;15387611776&quot;,\\n&quot;Status&quot;:&quot;DELIVRD&quot;\\n}\",\"status\":0,\"statusCode\":\"DELIVRD\"}]\"]";
		
//		String[] ls = JSON.parseObject(ss, new TypeReference<String[]>(){});
//		
//		for(String l : ls) {
//			
//		}
		
		List<SmsMtMessageDeliver> delivers = new ArrayList<>();
		delivers.addAll(JSON.parseObject(aa, new TypeReference<List<SmsMtMessageDeliver>>(){}));
		
		for(SmsMtMessageDeliver d : delivers)
			System.out.println(d.getMsgId());
	}
}
