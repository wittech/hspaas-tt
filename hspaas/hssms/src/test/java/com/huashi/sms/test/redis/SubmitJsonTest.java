package com.huashi.sms.test.redis;

import java.util.Date;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;

/**
 * TODO 请在此处添加注释
 *
 * @author zhengying
 * @version V3.19
 * @date ${date} ${time}
 */
public class SubmitJsonTest {

//    private  static Logger logger = LoggerFactory.getLogger(SubmitJsonTest.class);


    @Test
    public void test() {
        long start = System.currentTimeMillis();
        for(int i=0; i<100000; i++) {
            SmsMtMessageSubmit smsMtMessageSubmit = new SmsMtMessageSubmit();
            smsMtMessageSubmit.setAppKey("test"+i);
            smsMtMessageSubmit.setContent("【上上签】测试软件");
            smsMtMessageSubmit.setMobile("15868193450");
            smsMtMessageSubmit.setAttach("test");
            smsMtMessageSubmit.setCallback("http://aaaaaa:000222/232323");
            smsMtMessageSubmit.setCreateTime(new Date());
            smsMtMessageSubmit.setMsgId("22929883223"+i);
            smsMtMessageSubmit.setSid(System.currentTimeMillis() + i);
            smsMtMessageSubmit.setNeedPush(true);

            JSON.toJSONString(smsMtMessageSubmit);
//            System.out.println(JSON.toJSONString(smsMtMessageSubmit));
//            logger.info(i + "");
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - start));


    }
}
