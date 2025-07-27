package com.minispring.example;

import com.minispring.ioc.context.AnnotationConfigApplicationContext;

/**
 * Mini Spring 框架使用示例
 * 演示如何使用注解驱动的IoC容器
 */
public class MiniSpringDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Mini Spring Framework Demo ===");
        
        // 创建应用上下文，自动扫描组件
        System.out.println("1. 创建应用上下文...");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.minispring.example");
        
        System.out.println("\n2. 获取Bean并测试依赖注入...");
        
        // 获取UserService Bean
        UserService userService = context.getBean(UserService.class);
        System.out.println("获取到UserService: " + userService.getClass().getSimpleName());
        
        // 获取UserRepository Bean
        UserRepository userRepository = context.getBean(UserRepository.class);
        System.out.println("获取到UserRepository: " + userRepository.getClass().getSimpleName());
        
        System.out.println("\n3. 验证依赖注入...");
        System.out.println("UserService中的UserRepository是否已注入: " + (userService.getUserRepository() != null));
        System.out.println("依赖注入是否正确: " + (userService.getUserRepository() == userRepository));
        
        System.out.println("\n4. 测试@Value注解值注入...");
        System.out.println("应用名称: " + userService.getAppName());
        System.out.println("最大用户数: " + userService.getMaxUsers());
        System.out.println("数据库URL: " + userRepository.getDatabaseUrl());
        
        System.out.println("\n5. 测试业务逻辑...");
        String result1 = userService.createUser("张三");
        String result2 = userService.createUser("李四");
        
        System.out.println("结果1: " + result1);
        System.out.println("结果2: " + result2);
        
        System.out.println("\n6. 展示应用上下文信息...");
        System.out.println("上下文ID: " + context.getId());
        System.out.println("显示名称: " + context.getDisplayName());
        System.out.println("启动时间: " + context.getStartupDate());
        System.out.println("是否活跃: " + context.isActive());
        
        System.out.println("\n7. 关闭应用上下文...");
        context.close();
        System.out.println("上下文已关闭，活跃状态: " + context.isActive());
        
        System.out.println("\n=== Demo 完成 ===");
        
        System.out.println("\n核心功能演示:");
        System.out.println("✓ IoC容器 - 自动管理Bean的创建和生命周期");
        System.out.println("✓ 依赖注入 - 通过@Autowired自动注入依赖");
        System.out.println("✓ 值注入 - 通过@Value注入配置值");
        System.out.println("✓ 组件扫描 - 通过@Component自动发现和注册Bean");
        System.out.println("✓ 注解驱动 - 基于注解的配置方式");
        System.out.println("✓ 应用上下文 - 提供高级的容器管理功能");
    }
}