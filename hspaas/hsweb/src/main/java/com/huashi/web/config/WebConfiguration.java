package com.huashi.web.config;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.huashi.web.filter.AuthenticationInterceptor;
import com.huashi.web.filter.XssFilter;

import freemarker.template.utility.XmlEscape;

//@Configuration
//@EnableWebMvc
public class WebConfiguration extends WebMvcConfigurerAdapter {

	@Autowired
	private HttpMessageConverters httpMessageConverters;

	@Bean
	public FilterRegistrationBean someFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(xssFilter());
		registration.addUrlPatterns("/*");
		registration.setName("xssFilter");
		registration.setOrder(1);
		return registration;
	}
	
	@Bean
	public FilterRegistrationBean characterEncodingFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(characterEncodingFilter());
		registration.addUrlPatterns("/*");
		registration.setName("characterEncodingFilter");
		registration.setOrder(2);
		return registration;
	}

	@Bean
	public StringHttpMessageConverter stringHttpMessageConverter() {
		return new StringHttpMessageConverter(Charset.forName("UTF-8"));
	}

	@Bean
	public XssFilter xssFilter() {
		return new XssFilter();
	}

	@Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		return characterEncodingFilter;
	}

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		return new MappingJackson2HttpMessageConverter(new JsonMessageMapper());
	}

	@Override
	public void configureMessageConverters(
			List<HttpMessageConverter<?>> converters) {
		for (HttpMessageConverter<?> httpMessageConverter : httpMessageConverters
				.getConverters()) {
			converters.add(httpMessageConverter);
		}
		converters.add(mappingJackson2HttpMessageConverter());
	}

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize("10MB");
		factory.setMaxRequestSize("10MB");
		return factory.createMultipartConfig();
	}

	@Bean
	public PutAwareCommonsMultipartResolver multipartResolver() {
		PutAwareCommonsMultipartResolver putAwareCommonsMultipartResolver = new PutAwareCommonsMultipartResolver();
		return putAwareCommonsMultipartResolver;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AuthenticationInterceptor()).addPathPatterns("/**")
			.excludePathPatterns("/login/**", "/register/**", "/verify_code/**", "/pay_result/**", "/api/**");
		super.addInterceptors(registry);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("/index");
		registry.addViewController("/500").setViewName("error/error");
		registry.addViewController("/404").setViewName("error/error");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/**")
				.addResourceLocations("/assets/images/")
				.setCachePeriod(31556926);
		registry.addResourceHandler("/js/**")
				.addResourceLocations("/assets/js/").setCachePeriod(31556926);
		registry.addResourceHandler("/css/**")
				.addResourceLocations("/assets/css/").setCachePeriod(31556926);
	}

	@Bean
	public FreeMarkerConfig freemarkerConfig() {
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		configurer.setConfigLocation(new DefaultResourceLoader().getResource(ResourceLoader.CLASSPATH_URL_PREFIX + "freemarker.properties"));
		
		configurer.setTemplateLoaderPaths("/WEB-INF/pages/");
		configurer.setDefaultEncoding("UTF-8");
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("xml_escape", new XmlEscape());
		
		configurer.setFreemarkerVariables(variables);
		
		return configurer;
	}

	@Bean
	public FreeMarkerViewResolver viewResolver() {
		FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
		viewResolver.setContentType("text/html; charset=utf-8");
		viewResolver.setViewClass(FreeMarkerView.class);
		viewResolver.setSuffix(".ftl");
		viewResolver.setCache(true);
		viewResolver.setRequestContextAttribute("rc");
		viewResolver.setExposePathVariables(true);
		viewResolver.setExposeSessionAttributes(true);
		viewResolver.setExposeRequestAttributes(true);
		viewResolver.setExposeSpringMacroHelpers(true);
		return viewResolver;
	}

	@Bean
	// Only used when running in embedded servlet
	public DispatcherServlet dispatcherServlet() {
		return new DispatcherServlet();
	}

	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
	public Validator getValidator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setUseCodeAsDefaultMessage(false);
		messageSource.setCacheSeconds(60);
		validator.setValidationMessageSource(messageSource);
		return validator;
	}
	
	
}