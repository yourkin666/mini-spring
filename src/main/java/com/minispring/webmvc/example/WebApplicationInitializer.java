package com.minispring.webmvc.example;

import com.minispring.ioc.context.AnnotationConfigApplicationContext;
import com.minispring.webmvc.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Web应用初始化器
 * 负责配置DispatcherServlet和Spring ApplicationContext
 * 体现Spring MVC的程序化配置方式（替代web.xml）
 */
public class WebApplicationInitializer {
    
    /**
     * 初始化Web应用上下文
     * 这个方法通常在Servlet容器启动时被调用
     */
    public void onStartup(ServletContext servletContext) throws ServletException {
        
        // 1. 创建Spring ApplicationContext
        AnnotationConfigApplicationContext applicationContext = 
                new AnnotationConfigApplicationContext(WebMvcConfig.class);
        
        // 2. 将ApplicationContext存储在ServletContext中
        servletContext.setAttribute("applicationContext", applicationContext);
        
        // 3. 创建并注册DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        
        ServletRegistration.Dynamic registration = 
                servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
        
        System.out.println("=== Mini Spring MVC 应用初始化完成 ===");
        System.out.println("ApplicationContext: " + applicationContext.getClass().getSimpleName());
        System.out.println("注册的Bean数量: " + applicationContext.getBeanDefinitionNames().length);
        System.out.println("DispatcherServlet映射: /*");
        
        // 4. 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("正在关闭Spring应用上下文...");
            applicationContext.close();
        }));
    }
    
    /**
     * 简化的启动方法，用于测试
     */
    public static AnnotationConfigApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(WebMvcConfig.class);
        
        System.out.println("\n=== Mini Spring MVC 测试环境启动 ===");
        printBeanInfo(context);
        
        return context;
    }
    
    /**
     * 打印Bean信息
     */
    private static void printBeanInfo(AnnotationConfigApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        System.out.println("注册的Bean列表：");
        
        for (String beanName : beanNames) {
            try {
                Object bean = context.getBean(beanName);
                System.out.println("  - " + beanName + " : " + bean.getClass().getSimpleName());
            } catch (Exception e) {
                System.out.println("  - " + beanName + " : [获取失败] " + e.getMessage());
            }
        }
        
        System.out.println("总计: " + beanNames.length + " 个Bean");
        System.out.println("=====================================\n");
    }
}
