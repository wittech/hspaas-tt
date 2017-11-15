package com.huashi.common.test.util;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class DataSourceUtil {

	/**
	 * 根据类型获取数据源
	 * 
	 * @param sourceType
	 *            数据源类型
	 * @return druid或者dbcp数据源
	 * @throws Exception
	 *             the exception
	 */
	public static final DataSource getDataSource(String filename) throws Exception {
		return DruidDataSourceFactory.createDataSource(PropKit.use(filename).getProperties());
	}
}