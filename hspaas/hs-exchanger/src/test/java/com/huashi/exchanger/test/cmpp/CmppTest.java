package com.huashi.exchanger.test.cmpp;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.huashi.common.util.RandomUtil;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.constants.CommonContext.ProtocolType;
import com.huashi.exchanger.HsExchangerApplication;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.service.ISmsProviderService;
import com.huashi.sms.passage.domain.SmsPassageParameter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HsExchangerApplication.class)
public class CmppTest {

    private static int                 threadNum;
    private static SmsPassageParameter smsPassageParameter;
    private static String              mobile;
    private static String              content;
    private static Integer             fee       = 1;
    private static String              extNumber = null;

    @Autowired
    private ISmsProviderService        smsProviderService;

    @Before
    public void init() {
        threadNum = 20;
        mobile = getMobiles(10);
        fee = 1;
        content = "【测试】您的短信验证码为";
        setSmsPassageParameter();
    }

    /**
     * TODO 获取响应数量的手机号码
     * 
     * @param count 号码个数
     * @return
     */
    private String getMobiles(int count) {
        String[] mobiles = new String[count];
        for (int i = 0; i < count; i++) {
            mobiles[i] = "158" + RandomUtil.getRandomNum(8);
        }

        return StringUtils.join(mobiles);
    }

    /**
     * TODO 设置通道信息
     */
    private static void setSmsPassageParameter() {
        smsPassageParameter = new SmsPassageParameter();
        smsPassageParameter.setUrl("n/a");
        smsPassageParameter.setProtocol(ProtocolType.CMPP2.name());
        smsPassageParameter.setCallType(PassageCallType.DATA_SEND.getCode());
        smsPassageParameter.setPassageId(1);
        smsPassageParameter.setPacketsSize(5000);
        smsPassageParameter.setFeeByWords(68);
        smsPassageParameter.setSuccessCode("DELIVRD");

        JSONObject params = new JSONObject();
        params.put("ip", "localhost");
        params.put("port", "58081");
        params.put("username", "111");
        params.put("password", "111");

        params.put("spid", "1111");
        params.put("src_terminal_id", "1111");

        smsPassageParameter.setParams(params.toJSONString());
    }

    @Test
    public void test() {
        CountDownLatch cdl = new CountDownLatch(threadNum);

        // CyclicBarrier cyclicBarrier = new CyclicBarrier(1);

        // ExecutorService executorService = Executors.newFixedThreadPool(20);
        // for(int i=0; i< threadNum;i++) {
        // executorService.submit(new SendThread(cdl, smsProviderService));
        // }
        //
        // long startTime = System.currentTimeMillis();
        // try {
        // cdl.await();
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        long startTime = System.currentTimeMillis();
        List<ProviderSendResponse> list = smsProviderService.doTransport(smsPassageParameter, mobile, content, fee,
                                                                         extNumber);
        System.out.println("当前线程：" + Thread.currentThread().getName() + " 返回值：" + list);
        System.out.println("共耗时：" + (System.currentTimeMillis() - startTime));
    }

    private static class SendThread implements Runnable {

        private CountDownLatch      cdl;
        private ISmsProviderService smsProviderService;

        public SendThread(CountDownLatch cdl, ISmsProviderService smsProviderService) {
            super();
            this.cdl = cdl;
            this.smsProviderService = smsProviderService;
        }

        @Override
        public void run() {
            content += RandomUtil.getRandomNum(6);
            List<ProviderSendResponse> list = smsProviderService.doTransport(smsPassageParameter, mobile, content, fee,
                                                                             extNumber);
            System.out.println("当前线程：" + Thread.currentThread().getName() + " 返回值：" + list);

            cdl.countDown();
        }
    }

}
