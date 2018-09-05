package com.huashi.hsboss.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import com.jfinal.render.FreeMarkerRender;
import freemarker.template.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import com.huashi.hsboss.config.handler.ViewConstantHandler;
import com.huashi.hsboss.config.plugin.spring.IocInterceptor;
import com.huashi.hsboss.config.plugin.spring.SpringPlugin;
import com.huashi.hsboss.model.base.BaseModel;
import com.huashi.hsboss.web.interceptor.AuthInterceptor;
import com.huashi.hsboss.web.interceptor.ViewContextInterceptor;
import com.huashi.hsboss.web.listener.SpringBeanManager;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.handler.RenderingTimeHandler;
import com.jfinal.ext.handler.UrlSkipHandler;
import com.jfinal.ext.plugin.tablebind.AutoTableBindPlugin;
import com.jfinal.ext.route.AutoBindRoutes;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.render.ViewType;

/**
 * Jfinal系统配置
 * @author ym
 * @created_at 2016年6月22日下午2:10:05
 */
public class SystemConfig extends JFinalConfig {

	
	public static final String BASE_VIEW_PATH = "/WEB-INF/views";   //视图加载路径
	@Override
	public void configConstant(Constants constants) {
		constants.setViewType(ViewType.FREE_MARKER);
		constants.setFreeMarkerViewExtension(".ftl");
        constants.setError404View("/resources/html/404.html");
		constants.setError500View("/resources/html/500.html");
		constants.setBaseViewPath(BASE_VIEW_PATH);
		Configuration configuration = FreeMarkerRender.getConfiguration();
		configuration.addAutoImport("macro","/WEB-INF/views/common/macro.ftl");
		constants.setDevMode(true);
	}


	@Override
	public void configRoute(Routes routes) {
//		routes.setBaseViewPath(BASE_VIEW_PATH);
//		routes.add("/", AccountController.class, "/account");
		routes.add(new AutoBindRoutes());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void configPlugin(Plugins plugins) {
		SpringPlugin springPlugin = new SpringPlugin(SpringBeanManager.getContext());
		plugins.add(springPlugin);
		
//		Prop prop = PropKit.use("config.properties");
		
//		DruidPlugin druidPlugin = new DruidPlugin(prop.get("jdbc.connection.url"), prop.get("jdbc.connection.username"), prop.get("jdbc.connection.password"));
//		druidPlugin.setMaxPoolPreparedStatementPerConnectionSize(prop.getInt("maxPoolPreparedStatementPerConnectionSize"));
//		plugins.add(druidPlugin);
		
		DataSource dataSource = (DataSource)SpringBeanManager.getBean("proxyDataSource",TransactionAwareDataSourceProxy.class);
		
		AutoTableBindPlugin autoTable = new AutoTableBindPlugin(dataSource);
		
		autoTable.setDialect(new MysqlDialect());
		autoTable.setShowSql(true);
		autoTable.autoScan(true);
		autoTable.addExcludeClasses(BaseModel.class);
		plugins.add(autoTable);
		
	}

	@Override
	public void configInterceptor(Interceptors interceptors) {
		interceptors.add(new IocInterceptor());
		interceptors.add(new AuthInterceptor());
		interceptors.add(new ViewContextInterceptor());
	}

	@SuppressWarnings("static-access")
	@Override
	public void configHandler(Handlers handlers) {
		handlers.add(new ContextPathHandler("BASE_PATH"));
		
		InitConfig config = SpringBeanManager.getBean("initConfig", InitConfig.class);
		Map<String, Object> constantMap = new HashMap<String,Object>();
		constantMap.put("STATIC_VISIT_ADDR", config.getFileStaticAddr());
		handlers.add(new ViewConstantHandler(constantMap));
		
		handlers.add(new RenderingTimeHandler());
		handlers.add(new UrlSkipHandler("/resources", false));
	}


//	@Override
//	public void configEngine(Engine arg0) {
//		// TODO Auto-generated method stub
//		
//	}

}
