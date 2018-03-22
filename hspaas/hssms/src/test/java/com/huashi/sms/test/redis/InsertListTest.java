package com.huashi.sms.test.redis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.huashi.common.util.DateUtil;
import com.huashi.common.util.RandomUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

public class InsertListTest extends RedisBasicTest {

    private List<SmsMtMessageDeliver> list;
    private int                       size;
    private String                    report;
    private String[]                  stringList;

    @Before
    public void init() {
        size = 40000;
        list = new ArrayList<>(size);
        stringList = new String[size];
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
            stringList[i] = JSON.toJSONString(deliver);
        }

        report = JSON.toJSONString(list);
    }

    @Test
    public void test() {
        jedis.select(10);
        
        for(int i=0;i <100; i++){
            long start = System.currentTimeMillis();
            report = JSON.toJSONString(list);
            jedis.rpush("zy_test_string:" + System.currentTimeMillis(), report);
            System.out.println("String push耗时：" + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            report = JSON.toJSONString(list);
            jedis.rpush("zy_test_list:" + System.currentTimeMillis(), stringList);
            System.out.println("list push耗时：" + (System.currentTimeMillis() - start));
        }
    }

}
