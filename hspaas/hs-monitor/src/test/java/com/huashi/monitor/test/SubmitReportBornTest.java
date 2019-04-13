package com.huashi.monitor.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.huashi.monitor.job.sms.SmsSubmitReportBornJob;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubmitReportBornTest {

    @Autowired
    private SmsSubmitReportBornJob smsSubmitReportBornJob;

    @Test
    public void test() {
        try {
            smsSubmitReportBornJob.run(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
