package com.huashi.listener.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@ComponentScan
@Configuration
//@ImportResource(value = { "classpath:spring-dubbo-consumer.xml"})
public class ApplicationConfiguration extends WebMvcConfigurerAdapter {
//    @Autowired
//    private HttpMessageConverters httpMessageConverters;
//
//    @Bean
//    public FilterRegistrationBean someFilterRegistration() {
//
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(xssFilter());
//        registration.addUrlPatterns("/*");
//        registration.setName("xssFilter");
//        registration.setOrder(1);
//        return registration;
//    }
//
//    @Bean
//    public XssFilter xssFilter() {
//        return new XssFilter();
//    }
//
//    @Bean
//    public StringHttpMessageConverter stringHttpMessageConverter() {
//        return new StringHttpMessageConverter(Charset.forName("utf-8"));
//    }
//
////    @Bean
////    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
////        return new MappingJackson2HttpMessageConverter(new CustomObjectMapper());
////    }
//    
////    public MappingF
//
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        for (HttpMessageConverter<?> httpMessageConverter : httpMessageConverters.getConverters()) {
//            converters.add(httpMessageConverter);
//        }
////        converters.add(mappingJackson2HttpMessageConverter());
//    }
//
//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        factory.setMaxFileSize("10MB");
//        factory.setMaxRequestSize("10MB");
//        return factory.createMultipartConfig();
//    }
//
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticateInterceptor()).addPathPatterns("/sms/**", "/flux/**", "/voice/**");
        super.addInterceptors(registry);
    }
//
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("login/index_v2.2.0");
//        registry.addViewController("/500").setViewName("error/error");
//        registry.addViewController("/404").setViewName("error/error");
//    }
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/img/**").addResourceLocations("/img/");
//        registry.addResourceHandler("/imagers/**").addResourceLocations("/imgers/");
//        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
//        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
//        registry.addResourceHandler("/jsp/**").addResourceLocations("/jsp/");
//    }
//
//    @Bean
//    public InternalResourceViewResolver viewResolver() {
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        viewResolver.setPrefix("/jsp/");
//        viewResolver.setSuffix(".jsp");
//        return viewResolver;
//    }
//
//    @Bean
//    // Only used when running in embedded servlet
//    public DispatcherServlet dispatcherServlet() {
//        return new DispatcherServlet();
//    }
//
//    @Override
//    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//        configurer.enable();
//    }
}