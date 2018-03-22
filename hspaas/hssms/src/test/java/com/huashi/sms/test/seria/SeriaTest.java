package com.huashi.sms.test.seria;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.util.DateUtil;
import com.huashi.common.util.RandomUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

public class SeriaTest {

    private List<SmsMtMessageDeliver> list;
    private int                       size;
    private String                    report;

    @Before
    public void init() {
        size = 40000;
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
    public void fastjson() {
        for (int i = 0; i < 100; i++) {
            long start = System.currentTimeMillis();
            report = JSON.toJSONString(list);
            System.out.println("fastjson序列化耗时：" + (System.currentTimeMillis() - start));

            System.out.println(report);
            start = System.currentTimeMillis();
            JSON.parseObject(report, new TypeReference<List<SmsMtMessageDeliver>>() {
            });
            System.out.println("fastjson反序列化耗时：" + (System.currentTimeMillis() - start));
        }
    }

}
