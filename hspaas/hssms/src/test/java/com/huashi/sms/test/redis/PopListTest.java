package com.huashi.sms.test.redis;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

public class PopListTest extends RedisBasicTest {

    private List<SmsMtMessageDeliver> list;
    private String key;

    @Before
    public void init() {
//        key = "zy_test_string:1521687280961";
        key = "zy_test_list:1521687281573";
        
    }

//    @Test
    public void listValuePop() {
        jedis.select(10);
        
        int size = 0;
        while(true) {
            long start = System.currentTimeMillis();
            String value = jedis.lpop(key);
            if(value == null) {
                System.out.println("共查询数据：" + size + "个");
                break;
            }
            size++;
            list = JSON.parseObject(value, new TypeReference<List<SmsMtMessageDeliver>>(){});
            System.out.println("lpop 耗时：" + (System.currentTimeMillis() - start) + "ms, list size：" + list.size());
        }
    }
    
    
    @Test
    public void popoValuePop() {
        jedis.select(10);
        
        int size = 0;
        long start = System.currentTimeMillis();
        while(true) {
            String value = jedis.lpop(key);
            if(value == null) {
                System.out.println("共查询数据：" + size + "个");
                System.out.println("lpop 耗时：" + (System.currentTimeMillis() - start) + "ms");
                break;
            }
            size++;
            JSON.parseObject(value, new TypeReference<SmsMtMessageDeliver>(){});
        }
    }

}
