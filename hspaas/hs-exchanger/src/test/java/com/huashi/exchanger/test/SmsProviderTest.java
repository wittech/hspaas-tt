package com.huashi.exchanger.test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.util.IdGenerator;
import com.huashi.common.util.RandomUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.constants.CommonContext.ProtocolType;
import com.huashi.exchanger.service.ISmsProviderService;
import com.huashi.sms.task.domain.SmsMtTaskPackets;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:spring-dubbo-consume.xml")
public class SmsProviderTest {

	@Reference
	private ISmsProviderService smsProviderService;
	SmsMtTaskPackets smsMtTaskPackets;
	
//	@Before
	public void before() {
		smsMtTaskPackets = new SmsMtTaskPackets();
		smsMtTaskPackets.setMobile("15868193450");
		smsMtTaskPackets.setContent(String.format("【华时】尊敬的用户您好，您的本次验证码为%s，有效期为3分钟，请尽快完成后续操作。", RandomUtil.getRandomNum()));
		smsMtTaskPackets.setSid(IdGenerator.generatex());
		smsMtTaskPackets.setCmcp(CMCP.local(smsMtTaskPackets.getMobile()).getCode());
		smsMtTaskPackets.setPassageId(19);
		smsMtTaskPackets.setPassageProtocol(ProtocolType.HTTP.name());
		smsMtTaskPackets.setPassageUrl("http://sms.hspaas.com:8080/msgHttp/json/mt");
		smsMtTaskPackets.setPassageParameter("{\"password\":\"888888\",\"account\":\"13190905202995989\",\"custom\":\"DIANJI\"}");
		smsMtTaskPackets.setResultFormat("(\"Rspcode\":)(.*?)(,)(\"Msg_Id\":\")(.*?)(\",)(\"Mobile\":\")(.*?)(\")");
		smsMtTaskPackets.setSuccessCode("0");
		smsMtTaskPackets.setPosition("{\"statusCode\":\"2\",\"sid\":\"5\",\"mobile\":\"8\"}");
		
		smsMtTaskPackets.setRetryTimes(0);
		
//		private String pushUrl;
		
		System.out.println(smsMtTaskPackets.getContent());
		
		
//		PushConfig config = pushConfigService.getUrlConfig(submitModel.getUserId(), PlatformType.SEND_MESSAGE_SERVICE.getCode(), callback);
//		if(config != null) {
//			submitModel.setPushUrl(config.getUrl());
//			submitModel.setRetryTimes(config.getRetryTimes());
//			submitModel.setNeedPush(true);
//		}
	}

//	@Test
	public void test() {
		
//		List<ProviderSendResponse> list = smsProviderService.doTransport(smsMtTaskPackets);
//		// 解析调用上家接口结果
//		if(CollectionUtils.isEmpty(list))
//			throw new RuntimeException("调用上家接口回执数据为空");
//		
//		for(ProviderSendResponse p : list) {
//			System.out.println("---------------" + p.isSuccess());
//			
//			org.junit.Assert.assertTrue(p.isSuccess());
////			doPersistenceSubmitMessage(model, p.isSuccess(), p.getStatusCode());
//		}
		
	}

}
