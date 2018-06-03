package com.huashi.sms.test.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.huashi.common.util.RandomUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.sms.HsSmsApplication;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.service.ISmsMtSubmitService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HsSmsApplication.class)
// @WebAppConfiguration
public class BatchSubmitTest {

    @Autowired
    private ISmsMtSubmitService   smsMtSubmitService;
    private static int            size;
    private static int            round;
    private int                   threadNum;
    private static CountDownLatch cdl    = new CountDownLatch(1);
    private static Logger         LOGGER = LoggerFactory.getLogger(BatchSubmitTest.class);

    @Before
    public void setup() {
        threadNum = 40;
        size = 4000;
        round = 10;
    }

    @Test
    public void test() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(64);
        for (int i = 0; i < threadNum; i++) {
            pool.submit(new Thread(new RunThread(smsMtSubmitService)));
        }

        cdl.await();
    }

    private static class RunThread implements Runnable {

        private ISmsMtSubmitService smsMtSubmitService;

        public RunThread(ISmsMtSubmitService smsMtSubmitService) {
            super();
            this.smsMtSubmitService = smsMtSubmitService;
        }

        @Override
        public void run() {
            for (int i = 0; i < round; i++) {
                List<SmsMtMessageSubmit> list = getList(Thread.currentThread().getId(), i);
                long start = System.currentTimeMillis();
                smsMtSubmitService.batchInsertSubmit(list);
                LOGGER.info("耗时：" + (System.currentTimeMillis() - start));
            }

        }
    }

    public static List<SmsMtMessageSubmit> getList(long threadId, int round) {
        List<SmsMtMessageSubmit> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            SmsMtMessageSubmit submit = new SmsMtMessageSubmit();
            submit.setUserId(0);
            submit.setContent("【华时】测试数据");
            submit.setCmcp(CMCP.CHINA_MOBILE.getCode());
            submit.setMobile("158" + RandomUtil.getRandomNum(8));
            submit.setCreateUnixtime(System.currentTimeMillis());
            submit.setCreateTime(new Date(System.currentTimeMillis()));
            submit.setPassageId(0);
            submit.setMsgId((100000000 + i) + threadId + "" + round);
            submit.setSid(100000000L + i);
            submit.setProvinceCode(13);
            submit.setStatus(DeliverStatus.SUCCESS.getValue());
            submit.setRemark(JSON.toJSONString(submit));
            list.add(submit);
        }

        return list;
    }
}
