package com.huashi.common.test.excel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huashi.common.test.util.DataSourceUtil;
import com.huashi.common.user.util.ExcelUtil;
import com.huashi.constants.CommonContext.CMCP;

public class MobileLocalImportTest {

	public static DataSource getDataSource() throws Exception {
		return DataSourceUtil.getDataSource("datasource.properties");
	}

	public static AtomicInteger COUNTER = new AtomicInteger(0);

	public static void exchange(){
		String sql = "INSERT INTO hspaas_province_local (number_area,province_code, cmcp, city, create_time) VALUES (?,?,?,?,NOW())";

		final int batchSize = 1000;
		try {
			Connection conn = getDataSource().getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			LOGGER.info("EXCEL数据读取中...");
			List<String[]> list = ExcelUtil.readExcel("C:\\Users\\tenody\\Desktop\\1484824855636RTF32\\", "手机号段-20170101-357213-全新版.xlsx");
			LOGGER.info("EXCEL文件共 {} 条数据", list.size());
			
			for(String[] rowData : list) {
				stmt.setString(1, rowData[1]);
				stmt.setInt(2, getProvinceCode(rowData[2]));
				stmt.setInt(3, getCmcp(rowData[3]));
				stmt.setString(4, rowData[4]);
				stmt.addBatch();
				if (COUNTER.incrementAndGet() % batchSize == 0) {
					stmt.executeBatch();
				}
				LOGGER.info("处理条数:{}", COUNTER.get());
			}
			
			stmt.execute();
			stmt.close();
			conn.close();
			
		} catch (Exception e) {
			LOGGER.error("数据处理失败，当前计数器：{}", COUNTER.get());
			e.printStackTrace();
		}
	}
	
	static Logger LOGGER = LoggerFactory.getLogger(MobileLocalImportTest.class);

	public static void main(String[] args) {
		LOGGER.info("开始执行....");
		long start = System.currentTimeMillis();
		exchange();
		LOGGER.info("执行完成，共处理：{}， 耗时：{} ms", COUNTER.get(), System.currentTimeMillis() - start);
	}
	
	public static int getProvinceCode(String provinceName) {
		Map<String, Integer> provinceMap = new HashMap<String, Integer>();
		provinceMap.put("北京", 11);
		provinceMap.put("天津", 12);
		provinceMap.put("河北", 13);
		provinceMap.put("山西", 14);
		provinceMap.put("内蒙古", 15);
		provinceMap.put("辽宁", 21);
		provinceMap.put("吉林", 22);
		provinceMap.put("黑龙江", 23);
		provinceMap.put("上海", 21);
		provinceMap.put("江苏", 32);
		provinceMap.put("浙江", 33);
		provinceMap.put("安徽", 34);
		provinceMap.put("福建", 35);
		provinceMap.put("江西", 36);
		provinceMap.put("山东", 37);
		provinceMap.put("河南", 41);
		provinceMap.put("湖北", 42);
		provinceMap.put("湖南", 43);
		provinceMap.put("广东", 44);
		provinceMap.put("海南", 46);
		provinceMap.put("重庆", 50);
		provinceMap.put("广西", 45);
		provinceMap.put("四川", 51);
		provinceMap.put("贵州", 52);
		provinceMap.put("云南", 53);
		provinceMap.put("西藏", 54);
		provinceMap.put("陕西", 61);
		provinceMap.put("甘肃", 62);
		provinceMap.put("青海", 63);
		provinceMap.put("宁夏", 64);
		provinceMap.put("新疆", 65);
		provinceMap.put("台湾", 71);
		provinceMap.put("香港", 81);
		provinceMap.put("澳门", 91);
		
		return provinceMap.get(provinceName);
	}
	
	public static int getCmcp(String cmcpName) {
		
		if(cmcpName.contains(CMCP.CHINA_MOBILE.getTitle()))
			return CMCP.CHINA_MOBILE.getCode();
		
		if(cmcpName.contains(CMCP.CHINA_TELECOM.getTitle()))
			return CMCP.CHINA_TELECOM.getCode();
		
		if(cmcpName.contains(CMCP.CHINA_UNICOM.getTitle()))
			return CMCP.CHINA_UNICOM.getCode();
		
		return CMCP.UNRECOGNIZED.getCode();
	}

	
}
