package com.minispring.webmvc.context;

import com.minispring.ioc.annotation.Component;
import com.minispring.ioc.context.ApplicationContext;
import com.minispring.webmvc.handler.HandlerAdapter;
import com.minispring.webmvc.handler.HandlerMapping;
import com.minispring.webmvc.handler.RequestMappingHandlerAdapter;
import com.minispring.webmvc.handler.RequestMappingHandlerMapping;
import com.minispring.webmvc.view.InternalResourceViewResolver;
import com.minispring.webmvc.view.ViewResolver;

/**
 * Web MVC配置支持类
 * 提供Spring MVC的基础配置Bean
 * 体现Spring的自动配置和约定优于配置设计理念
 */
@Component
public class WebMvcConfigurationSupport {
    
    private ApplicationContext applicationContext;
    
    /**
     * 设置ApplicationContext
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    /**
     * 创建RequestMappingHandlerMapping
     * 负责将@RequestMapping注解的方法映射到URL
     */
    @Component("requestMappingHandlerMapping")
    public HandlerMapping createRequestMappingHandlerMapping() {
        RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping();
        handlerMapping.setApplicationContext(this.applicationContext);
        return handlerMapping;
    }
    
    /**
     * 创建RequestMappingHandlerAdapter  
     * 负责调用@RequestMapping注解的方法
     */
    @Component("requestMappingHandlerAdapter")
    public HandlerAdapter createRequestMappingHandlerAdapter() {
        return new RequestMappingHandlerAdapter();
    }
    
    /**
     * 创建InternalResourceViewResolver
     * 负责解析视图名称为JSP等内部资源
     */
    @Component("viewResolver")
    public ViewResolver createViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setContentType("text/html;charset=UTF-8");
        return viewResolver;
    }
}
