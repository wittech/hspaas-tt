package com.huashi.sms.config.datasource;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;


@Configuration
@Order(1)
@MapperScan(basePackages = {"com.huashi.sms.**.dao"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class DruidConfiguration {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.driverClassName}")
	private String driverClassName;

	@Value("${spring.datasource.initialSize}")
	private int initialSize;

	@Value("${spring.datasource.minIdle}")
	private int minIdle;

	@Value("${spring.datasource.maxActive}")
	private int maxActive;

	@Value("${spring.datasource.maxWait}")
	private int maxWait;

	@Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
	private int timeBetweenEvictionRunsMillis;

	@Value("${spring.datasource.minEvictableIdleTimeMillis}")
	private int minEvictableIdleTimeMillis;

	@Value("${spring.datasource.validationQuery}")
	private String validationQuery;

	@Value("${spring.datasource.testWhileIdle}")
	private boolean testWhileIdle;

	@Value("${spring.datasource.testOnBorrow}")
	private boolean testOnBorrow;

	@Value("${spring.datasource.testOnReturn}")
	private boolean testOnReturn;

	@Value("${spring.datasource.filters}")
	private String filters;

	@Bean
	public DataSource dataSource() {
		DruidDataSource datasource = new DruidDataSource();
		datasource.setUrl(url);
		datasource.setUsername(username);
		datasource.setPassword(password);
		datasource.setDriverClassName(driverClassName);
		datasource.setInitialSize(initialSize);
		datasource.setMinIdle(minIdle);
		datasource.setMaxActive(maxActive);
		datasource.setMaxWait(maxWait);
		datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		datasource.setValidationQuery(validationQuery);
		datasource.setTestWhileIdle(testWhileIdle);
		datasource.setTestOnBorrow(testOnBorrow);
		datasource.setTestOnReturn(testOnReturn);
//		datasource.setConnectionInitSqls("set names utf8mb4;");
		
		try {
			datasource.setFilters(filters);
		} catch (SQLException e) {
			logger.error("druid configuration initialization filter", e);
		}
		return datasource;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate (DataSource dataSource) {
		return new JdbcTemplate(dataSource, true);
	}
	
	@Bean
	public PlatformTransactionManager dataSourceTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
	// 按照BeanId来拦截配置 用来bean的监控  
//    @Bean(value = "druid-stat-interceptor")  
//    public DruidStatInterceptor DruidStatInterceptor() {  
//        DruidStatInterceptor druidStatInterceptor = new DruidStatInterceptor();  
//        return druidStatInterceptor;  
//    }  
  
//    @Bean  
//    public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {  
//        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();  
//        beanNameAutoProxyCreator.setProxyTargetClass(true);  
//        // 设置要监控的bean的id  
//        //beanNameAutoProxyCreator.setBeanNames("sysRoleMapper","loginController");  
//        beanNameAutoProxyCreator.setInterceptorNames("druid-stat-interceptor");  
//        return beanNameAutoProxyCreator;  
//    }
	
	/**
	 * 
	   * TODO 连接池
	   * @param dataSource
	   * @return
	 */
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		
		try {
			return sqlSessionFactoryBean.getObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("initial mybatis sqlSessionFactory failed", e);
		}
	}
	
	/**
	 * 
	   * TODO mybatis 模板配置
	   * @param sqlSessionFactory
	   * @return
	 */
//	@Bean(name = "sqlSessionTemplate")
//	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
//		return new SqlSessionTemplate(sqlSessionFactory, ExecutorType.SIMPLE);
//	}
	
	
}
