package com.huashi.sms.test.redis;

import org.junit.Test;

import com.alibaba.druid.util.StringUtils;

import redis.clients.jedis.Jedis;


public class DbBakupRemoveTest {

    
    @Test
    public void test() throws InterruptedException {
        
        int i = 0;
        Jedis jedis = RedisUtil.getJedis();
        
        while(true) {
           String value = jedis.lpop("bak_db_message_submit_list");
           if(StringUtils.isEmpty(value)) {
               System.out.println("共处理 " + i + "行");
               break;
           }
           
           i++;
           jedis.rpush("db_message_submit_list", value);
           System.out.println(i + ". 数据： [" + value + "]");
        }
        
    }
}
