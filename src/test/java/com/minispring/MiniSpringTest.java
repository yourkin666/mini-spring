package com.minispring;

import com.minispring.ioc.beans.AnnotationBeanFactory;
import com.minispring.ioc.beans.BeanDefinition;
import com.minispring.ioc.beans.DefaultBeanFactory;
import com.minispring.ioc.context.AnnotationConfigApplicationContext;
import com.minispring.example.AppConfig;
import com.minispring.example.UserRepository;
import com.minispring.example.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mini Spring 框架测试类
 * 演示IoC容器、依赖注入、注解驱动等核心功能
 */
public class MiniSpringTest {
    
    @Test
    public void testBasicBeanFactory() {
        System.out.println("=== 测试基础BeanFactory功能 ===");
        
        DefaultBeanFactory beanFactory = new DefaultBeanFactory();
        
        // 手动注册Bean定义
        BeanDefinition userRepositoryDef = new BeanDefinition("userRepository", UserRepository.class);
        beanFactory.registerBeanDefinition("userRepository", userRepositoryDef);
        
        // 获取Bean
        UserRepository userRepository = (UserRepository) beanFactory.getBean("userRepository");
        assertNotNull(userRepository);
        
        // 测试单例
        UserRepository userRepository2 = (UserRepository) beanFactory.getBean("userRepository");
        assertSame(userRepository, userRepository2);
        
        System.out.println("✓ 基础BeanFactory测试通过");
    }
    
    @Test
    public void testAnnotationConfigApplicationContext() {
        System.out.println("\n=== 测试注解驱动的ApplicationContext ===");
        
        // 使用配置类创建应用上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        
        // 测试Bean是否被正确扫描和注册
        assertTrue(context.containsBean("userService"));
        assertTrue(context.containsBean("userRepository"));
        
        // 获取Bean并测试依赖注入
        UserService userService = context.getBean("userService", UserService.class);
        assertNotNull(userService);
        assertNotNull(userService.getUserRepository());
        
        UserRepository userRepository = context.getBean("userRepository", UserRepository.class);
        assertNotNull(userRepository);
        
        // 测试依赖注入是否正确
        assertSame(userRepository, userService.getUserRepository());
        
        System.out.println("✓ 注解驱动的ApplicationContext测试通过");
        
        context.close();
    }
    
    @Test
    public void testValueInjection() {
        System.out.println("\n=== 测试@Value注解值注入 ===");
        
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        
        UserService userService = context.getBean(UserService.class);
        UserRepository userRepository = context.getBean(UserRepository.class);
        
        // 测试默认值注入
        assertEquals("Mini Spring", userService.getAppName());
        assertEquals(100, userService.getMaxUsers());
        assertEquals("jdbc:h2:mem:test", userRepository.getDatabaseUrl());
        
        System.out.println("App Name: " + userService.getAppName());
        System.out.println("Max Users: " + userService.getMaxUsers());
        System.out.println("Database URL: " + userRepository.getDatabaseUrl());
        
        System.out.println("✓ @Value注解值注入测试通过");
        
        context.close();
    }
    
    @Test
    public void testServiceIntegration() {
        System.out.println("\n=== 测试服务集成 ===");
        
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        
        UserService userService = context.getBean(UserService.class);
        
        // 测试业务方法
        String result = userService.createUser("testUser");
        assertNotNull(result);
        assertTrue(result.contains("testUser"));
        
        System.out.println("Create user result: " + result);
        System.out.println("✓ 服务集成测试通过");
        
        context.close();
    }
    
    @Test
    public void testPackageScanning() {
        System.out.println("\n=== 测试包扫描功能 ===");
        
        // 直接指定包路径进行扫描
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.minispring.example");
        
        // 验证扫描结果
        assertTrue(context.containsBean("userService"));
        assertTrue(context.containsBean("userRepository"));
        
        // 测试获取Bean
        UserService userService = context.getBean(UserService.class);
        UserRepository userRepository = context.getBean(UserRepository.class);
        
        assertNotNull(userService);
        assertNotNull(userRepository);
        assertNotNull(userService.getUserRepository());
        
        System.out.println("✓ 包扫描功能测试通过");
        
        context.close();
    }
    
    @Test
    public void testBeanLifecycle() {
        System.out.println("\n=== 测试Bean生命周期 ===");
        
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        
        // 手动扫描
        context.scan("com.minispring.example");
        context.refresh();
        
        // 测试上下文状态
        assertTrue(context.isActive());
        assertTrue(context.getStartupDate() > 0);
        assertNotNull(context.getId());
        
        System.out.println("Context ID: " + context.getId());
        System.out.println("Display Name: " + context.getDisplayName());
        System.out.println("Startup Date: " + context.getStartupDate());
        System.out.println("Is Active: " + context.isActive());
        
        // 关闭上下文
        context.close();
        assertFalse(context.isActive());
        
        System.out.println("✓ Bean生命周期测试通过");
    }
}