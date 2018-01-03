package com.huashi.common.test.excel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.fastjson.JSON;
import com.huashi.common.user.util.ExcelUtil;
import com.huashi.common.util.DateUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.service.ISmsMtDeliverService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-dubbo-consumer.xml"})
public class SmsReportRepeatTest {

	private static final String COMMON_MT_STATUS_SUCCESS_CODE = "DELIVRD";
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String successCode;
	
	private ISmsMtDeliverService smsMtDeliverService;
	List<SmsMtMessageDeliver> list = null;
	String filename = null;
	
	@Before
	public void init() {
//		任务ID	手机号	抵达状态
		filename = "C:\\Users\\tenody\\Desktop\\mt_report.xlsx";
//		list = readExcelReport(filename);
		
		successCode = "0";
		
		String url = "dubbo://106.14.37.153:20882/com.huashi.sms.record.service.ISmsMtDeliverService?anyhost=true&application=hssms-provider&default.retries=0&default.timeout=100000&dubbo=2.8.4&generic=false&interface=com.huashi.sms.record.service.ISmsMtDeliverService&logger=slf4j&methods=doFinishDeliver,batchInsert,doDeliverToException,saveDeliverLog,findByMobileAndMsgid&pid=21708&serialization=kryo&side=provider×tamp=1514741539828";
		  
        ReferenceBean<ISmsMtDeliverService> referenceBean = new ReferenceBean<ISmsMtDeliverService>();  
        referenceBean.setApplicationContext(applicationContext);  
        referenceBean.setInterface(ISmsMtDeliverService.class);  
        referenceBean.setUrl(url);  
  
        try {  
            referenceBean.afterPropertiesSet();  
            smsMtDeliverService = referenceBean.get();  
           
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
	
	
	 //用于将bean关系注入到当前的context中  
    @Autowired
    private ApplicationContext applicationContext;  
  
//    @Test
    public void test() {
    	
    	Assert.assertTrue("解析数据为空", CollectionUtils.isNotEmpty(list));
    	
    }
    
    @Test
    public void pipelineTest() {
    	try {
    		list = new ArrayList<SmsMtMessageDeliver>();
			Workbook workBook = ExcelUtil.createWorkBook(filename);
			Sheet sheet = workBook.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows(); // 获得行数
			if (rows > 0) {
				sheet.getMargin(Sheet.TopMargin);
				SmsMtMessageDeliver response = null;
				// EXCEL前1行为总标题和列标题，因此在地2行开始读取数据
				for (int r = 1; r < rows; r++) { // 行循环
//					list = new ArrayList<SmsMtMessageDeliver>();
					
					Row row = sheet.getRow(r);
					if (row != null) {
						String taskId = ExcelUtil.getCellValue(row.getCell(0)); // 任务号码
						String mobile = ExcelUtil.getCellValue(row.getCell(1)); // 手机号码
						String status = ExcelUtil.getCellValue(row.getCell(2)); // 状态
						
						response = new SmsMtMessageDeliver();
						response.setMsgId(taskId);
						response.setMobile(mobile);
						response.setCmcp(CMCP.local(response.getMobile()).getCode());
						if(status.equalsIgnoreCase(successCode)){
							response.setStatusCode(COMMON_MT_STATUS_SUCCESS_CODE);
							response.setStatus(DeliverStatus.SUCCESS.getValue());
						} else {
							response.setStatusCode(getStatusDes(status));
							response.setStatus(DeliverStatus.FAILED.getValue());
						}
						response.setDeliverTime(DateUtil.getNow());
						response.setRemark(JSON.toJSONString(response));
						
						response.setCreateTime(new Date());

						list.add(response);
						
						if(r % 1000 == 0) {
							smsMtDeliverService.doFinishDeliver(list);
							logger.info("处理行号：{}， mobile : {}", r, mobile);
							list = new ArrayList<SmsMtMessageDeliver>();
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    	
    	if(list.size() > 0) {
    		smsMtDeliverService.doFinishDeliver(list);
			logger.info("处理大小：{}， mobile : {}", list.size(), list.get(0).getMobile());
    	}
    	
    }
	
	/**
	 * 
	   * TODO 获取状态错误描述信息
	   *  
	   * @param code
	   * @return
	 */
	private static String getStatusDes(String code) {
		if(StringUtils.isEmpty(code))
			return "99：其他";
		
		switch(code) {
			case "1" : return "1:空号";
			case "2" : return "2：关机停机";
			case "3" : return "3：发送频率过高";
			case "4" : return "4：签名无效";
			case "5" : return "5：黑词";
			case "6" : return "6：黑名单";
			case "7" : return "7：短信内容有误";
			case "8" : return "8：必须包含退订";
			default : return "99：其他";
		}
	}
	
	/**
	 * 
	 * TODO 读取第一列数据
	 * 
	 * @param filePath
	 * @param serialNo
	 * 			批号
	 * @return
	 */
	public List<SmsMtMessageDeliver> readExcelReport(String filePath) {
		int rows = 0;
		List<SmsMtMessageDeliver> list = new ArrayList<SmsMtMessageDeliver>();
		try {
			Workbook workBook = ExcelUtil.createWorkBook(filePath);
			Sheet sheet = workBook.getSheetAt(0);
			rows = sheet.getPhysicalNumberOfRows(); // 获得行数
			if (rows > 0) {
				sheet.getMargin(Sheet.TopMargin);
				SmsMtMessageDeliver response = null;
				// EXCEL前1行为总标题和列标题，因此在地2行开始读取数据
				for (int r = 1; r < rows; r++) { // 行循环
					Row row = sheet.getRow(r);
					if (row != null) {
						String taskId = ExcelUtil.getCellValue(row.getCell(0)); // 任务号码
						String mobile = ExcelUtil.getCellValue(row.getCell(1)); // 手机号码
						String status = ExcelUtil.getCellValue(row.getCell(2)); // 状态
						
						response = new SmsMtMessageDeliver();
						response.setMsgId(taskId);
						response.setMobile(mobile);
						response.setCmcp(CMCP.local(response.getMobile()).getCode());
						if(status.equalsIgnoreCase(successCode)){
							response.setStatusCode(COMMON_MT_STATUS_SUCCESS_CODE);
							response.setStatus(DeliverStatus.SUCCESS.getValue());
						} else {
							response.setStatusCode(getStatusDes(status));
							response.setStatus(DeliverStatus.FAILED.getValue());
						}
						response.setDeliverTime(DateUtil.getNow());
						response.setRemark(JSON.toJSONString(response));
						
						response.setCreateTime(new Date());

						list.add(response);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		logger.info("解析EXCEL总量为：{}" , list.size());
		return list;
	}
}
