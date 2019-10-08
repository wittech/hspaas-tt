package com.huashi.sms.test.redis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.huashi.sms.test.util.CsvUtil;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.huashi.common.util.DateUtil;
import com.huashi.common.util.RandomUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

public class InsertSetTest extends RedisBasicTest {

    List<String> mobiles = new ArrayList<>();

    List<String[]>               csvData                       = null;

    private void csvAnalysis(String localPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(localPath))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                // 第一行不做处理
//                lineNo++;
//                if (lineNo == 1) {
//                    continue;
//                }

                // app_no,app_userid,app_ip,app_name,app_address,app_email,app_idcode,app_linkman,app_iccode,app_orgcode,app_taxcode,app_cerno,app_createtime

                String[] data = line.split(",");

                mobiles.add(data[0]);
            }

            System.out.println("数据共：" + mobiles.size());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void init() {
        csvAnalysis("/Users/tenx/Downloads/1123.csv");
    }

    @Test
    public void test() {
        jedis.select(1);

        jedis.sadd("red_passage_black_mobile", mobiles.toArray(new String[]{}));
    }

}
