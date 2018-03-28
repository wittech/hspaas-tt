package com.huashi.sms.test.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.huashi.common.util.DateUtil;
import com.huashi.common.util.RandomUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.sms.HsSmsApplication;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.service.ISmsMtDeliverService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HsSmsApplication.class)
// @WebAppConfiguration
public class BatchDeliverTest {

    @Autowired
    private ISmsMtDeliverService      smsMtDeliverService;

    private List<SmsMtMessageDeliver> list;
    private int                       size;
    CountDownLatch cdl = new CountDownLatch(1);

    @Before
    public void setup() {
        size = 4000;
        list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            SmsMtMessageDeliver deliver = new SmsMtMessageDeliver();
            deliver.setCmcp(CMCP.CHINA_MOBILE.getCode());
            deliver.setCreateTime(new Date(System.currentTimeMillis()));
            deliver.setDeliverTime(DateUtil.getSecondDateStr(System.currentTimeMillis()));
            deliver.setMobile("158" + RandomUtil.getRandomNum(8));
            deliver.setMsgId((100000000 + i) + "");
            deliver.setStatus(DeliverStatus.SUCCESS.getValue());
            deliver.setStatusCode("DELIVED");
            deliver.setRemark(JSON.toJSONString(deliver));
            list.add(deliver);
        }
    }

    @Test
    public void test() {
        long start = System.currentTimeMillis();
        smsMtDeliverService.doFinishDeliver(list);
        System.out.println("耗时：" + (System.currentTimeMillis() -start));
        
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
