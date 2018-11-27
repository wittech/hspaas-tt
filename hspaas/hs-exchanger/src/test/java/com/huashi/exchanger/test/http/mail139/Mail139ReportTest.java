package com.huashi.exchanger.test.http.mail139;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.huashi.exchanger.resolver.http.custom.mail139.Mail139PassageResolver;

public class Mail139ReportTest {

    private Mail139PassageResolver resolver = null;
    private String                 report   = null;

    @Before
    public void init() {
        resolver = new Mail139PassageResolver();
    }

    @Test
    public void mtReportTest() {
        report = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><seqno>44192333</seqno><smsnum>1</smsnum><responseData><MsgId>44192333</MsgId><SendStatus>0</SendStatus><SmsMsg>DELIVRD</SmsMsg><ReceiveNum>18368031231</ReceiveNum><ReceiveTime>2018-11-27 13:58:45</ReceiveTime></responseData></root>";
        System.out.println(JSON.toJSONString(resolver.mtDeliver(report, "DELIVRD")));
    }

    @Test
    public void moReportTest() {
        report = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><BaseNumber>10658139990074</BaseNumber><responseData><SpNumber>10658139990074</SpNumber><UserNumber>18368031231</UserNumber><MoMsg>你好</MoMsg><MoTime>2018-11-27 11:42:12</MoTime><timestamp>1543290132211</timestamp></responseData></root>";
        System.out.println(JSON.toJSONString(resolver.moReceive(report, 1)));
    }

}
