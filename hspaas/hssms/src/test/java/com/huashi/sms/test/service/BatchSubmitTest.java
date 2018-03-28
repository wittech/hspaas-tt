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
    private ISmsMtSubmitService    smsMtSubmitService;

    private List<SmsMtMessageSubmit> list;
    private int                       size;
    CountDownLatch cdl = new CountDownLatch(1);

    @Before
    public void setup() {
        size = 4000;
        list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            SmsMtMessageSubmit submit = new SmsMtMessageSubmit();
            submit.setUserId(0);
            submit.setContent("【华时】测试数据");
            submit.setCmcp(CMCP.CHINA_MOBILE.getCode());
            submit.setMobile("158" + RandomUtil.getRandomNum(8));
            submit.setCreateUnixtime(System.currentTimeMillis());
            submit.setCreateTime(new Date(System.currentTimeMillis()));
            submit.setPassageId(0);
            submit.setMsgId((100000000 + i) + "");
            submit.setSid(100000000L + i);
            submit.setProvinceCode(13);
            submit.setStatus(DeliverStatus.SUCCESS.getValue());
            submit.setRemark(JSON.toJSONString(submit));
            list.add(submit);
        }
    }

    @Test
    public void test() {
        long start = System.currentTimeMillis();
        smsMtSubmitService.batchInsertSubmit(list);
        System.out.println("耗时：" + (System.currentTimeMillis() -start));
        
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
