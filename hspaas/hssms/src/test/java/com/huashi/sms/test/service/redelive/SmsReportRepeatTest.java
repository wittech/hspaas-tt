package com.huashi.sms.test.service.redelive;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.service.ISmsMtDeliverService;
import com.huashi.sms.test.util.CsvUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-dubbo-consumer.xml" })
public class SmsReportRepeatTest {

    private static final String  COMMON_MT_STATUS_SUCCESS_CODE = "DELIVRD";

    private String               successCode;

    private ISmsMtDeliverService smsMtDeliverService;
    List<SmsMtMessageDeliver>    list                          = null;
    List<String[]>               csvData                       = null;
    String                       filename                      = null;

    @Before
    public void init() {
        // 任务ID 手机号 抵达状态
        filename = "C:\\Users\\tenx\\Desktop\\kuaiyu.csv";
        csvData = CsvUtil.csvAnalysis(filename);

        successCode = COMMON_MT_STATUS_SUCCESS_CODE;
    }
    
    private void flushService() {
        String url = "dubbo://106.14.37.153:20882/com.huashi.sms.record.service.ISmsMtDeliverService";

        ReferenceBean<ISmsMtDeliverService> referenceBean = new ReferenceBean<ISmsMtDeliverService>();
        referenceBean.setApplicationContext(applicationContext);
        referenceBean.setInterface(ISmsMtDeliverService.class);
        referenceBean.setUrl(url);
        referenceBean.setTimeout(1000000);

        try {
            referenceBean.afterPropertiesSet();
            smsMtDeliverService = referenceBean.get();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 用于将bean关系注入到当前的context中
    @Autowired
    private ApplicationContext applicationContext;

    // @Test
    public void test() {
        Assert.assertTrue("解析数据为空", CollectionUtils.isNotEmpty(list));

    }

    @Test
    public void batchPush() {
        try {
            list = new ArrayList<SmsMtMessageDeliver>();

            if (CollectionUtils.isEmpty(csvData)) {
                throw new RuntimeException("数据为空");
            }

            int counter = 0;
            for (String[] d : csvData) {

                SmsMtMessageDeliver response = new SmsMtMessageDeliver();
                // EXCEL前1行为总标题和列标题，因此在地2行开始读取数据
                // String taskId = ExcelUtil.getCellValue(row.getCell(0)); // 任务号码
                // String mobile = ExcelUtil.getCellValue(row.getCell(1)); // 手机号码
                // String status = ExcelUtil.getCellValue(row.getCell(2)); // 状态
                
                if(d.length < 5)
                    continue;
               
                String mobile = d[1]; // 手机号码
                String status = d[2]; // 状态
                String deliverTime = d[3]; // 回执时间
                String taskId = d[4]; // 任务号码
                
                if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(status) ||
                        StringUtils.isEmpty(deliverTime) || StringUtils.isEmpty(taskId)) {
                    continue;
                }

                response.setMsgId(taskId);
                response.setMobile(mobile);
                response.setCmcp(CMCP.local(response.getMobile()).getCode());
                if (status.equalsIgnoreCase(successCode)) {
                    response.setStatusCode(COMMON_MT_STATUS_SUCCESS_CODE);
                    response.setStatus(DeliverStatus.SUCCESS.getValue());
                } else {
                    response.setStatusCode(getStatusDes(status));
                    response.setStatus(DeliverStatus.FAILED.getValue());
                }
                // response.setDeliverTime(DateUtil.getNow());
                response.setDeliverTime(deliverTime);

                response.setRemark(JSON.toJSONString(response));

                response.setCreateTime(new Date());
                counter ++;
                
                list.add(response);
                
                if(counter % 1000 == 0) {
                    flushService();
                    smsMtDeliverService.doFinishDeliver(list);
                    System.out.println("共处理："+ counter);
                    list.clear();
                    Thread.sleep(2000);
                }
            }
            
            if(CollectionUtils.isNotEmpty(list)) {
                flushService();
                smsMtDeliverService.doFinishDeliver(list);
                System.out.println("最后共处理："+ list.size());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

    }

    /**
     * TODO 获取状态错误描述信息
     * 
     * @param code
     * @return
     */
    private static String getStatusDes(String code) {
        if (StringUtils.isEmpty(code)) return "99：其他";

        switch (code) {
            case "1":
                return "1:空号";
            case "2":
                return "2：关机停机";
            case "3":
                return "3：发送频率过高";
            case "4":
                return "4：签名无效";
            case "5":
                return "5：黑词";
            case "6":
                return "6：黑名单";
            case "7":
                return "7：短信内容有误";
            case "8":
                return "8：必须包含退订";
            default:
                return "99：其他";
        }
    }
}
