package com.minispring.example;

import com.minispring.ioc.context.AnnotationConfigApplicationContext;
import com.minispring.ioc.core.TypeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 增强版 Mini Spring 框架完整演示
 * 展示所有优化功能：循环依赖处理、异常处理、类型转换、Bean生命周期
 */
public class EnhancedMiniSpringDemo {
    
    public static void main(String[] args) {
        System.out.println("=== 增强版 Mini Spring Framework 完整演示 ===\n");
        
        // 1. 演示类型转换功能
        demonstrateTypeConversion();
        
        // 2. 演示Bean生命周期管理
        demonstrateBeanLifecycle();
        
        // 3. 演示异常处理
        demonstrateExceptionHandling();
        
        System.out.println("\n=== 演示完成 ===");
        System.out.println("\n🎉 Mini Spring Framework 增强版功能展示:");
        System.out.println("✅ 完善的循环依赖处理（三级缓存）");
        System.out.println("✅ 详细的异常处理和错误信息");
        System.out.println("✅ 强大的类型转换系统");
        System.out.println("✅ 完整的Bean生命周期管理");
        System.out.println("✅ 支持@PostConstruct和@PreDestroy");
        System.out.println("✅ 支持InitializingBean和DisposableBean接口");
    }
    
    private static void demonstrateTypeConversion() {
        System.out.println("1. === 类型转换功能演示 ===");
        
        // 基本类型转换
        System.out.println("基本类型转换:");
        System.out.println("  String '123' -> int: " + TypeConverter.convertValue("123", int.class));
        System.out.println("  String '123.45' -> double: " + TypeConverter.convertValue("123.45", double.class));
        System.out.println("  String 'true' -> boolean: " + TypeConverter.convertValue("true", boolean.class));
        
        // 高级类型转换
        System.out.println("高级类型转换:");
        BigDecimal decimal = (BigDecimal) TypeConverter.convertValue("999.99", BigDecimal.class);
        System.out.println("  String '999.99' -> BigDecimal: " + decimal);
        
        LocalDate date = (LocalDate) TypeConverter.convertValue("2023-12-25", LocalDate.class);
        System.out.println("  String '2023-12-25' -> LocalDate: " + date);
        
        // 数组转换
        String[] array = (String[]) TypeConverter.convertValue("apple,banana,orange", String[].class);
        System.out.println("  String 'apple,banana,orange' -> String[]: [" + String.join(", ", array) + "]");
        
        System.out.println("✓ 类型转换功能演示完成\n");
    }
    
    private static void demonstrateBeanLifecycle() {
        System.out.println("2. === Bean生命周期管理演示 ===");
        
        System.out.println("创建应用上下文...");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.minispring.example");
        
        System.out.println("\n获取Bean并测试功能...");
        
        // 获取用户服务
        UserService userService = context.getBean(UserService.class);
        System.out.println("UserService Bean创建完成");
        
        // 获取数据库服务
        DatabaseService dbService = context.getBean(DatabaseService.class);
        System.out.println("DatabaseService Bean创建完成，连接状态: " + dbService.isConnected());
        
        // 测试业务功能
        System.out.println("\n执行业务操作:");
        String result = userService.createUser("Alice");
        System.out.println("创建用户结果: " + result);
        
        dbService.executeQuery("SELECT * FROM users WHERE name = 'Alice'");
        
        System.out.println("\n显示配置信息:");
        System.out.println("应用名称: " + userService.getAppName());
        System.out.println("最大用户数: " + userService.getMaxUsers());
        System.out.println("连接池大小: " + dbService.getConnectionPoolSize());
        System.out.println("数据库超时: " + dbService.getTimeout());
        
        System.out.println("\n关闭应用上下文...");
        context.close();
        
        System.out.println("✓ Bean生命周期管理演示完成\n");
    }
    
    private static void demonstrateExceptionHandling() {
        System.out.println("3. === 异常处理演示 ===");
        
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        
        // 演示Bean不存在异常
        try {
            context.getBean("nonExistentBean");
        } catch (Exception e) {
            System.out.println("捕获到异常: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        
        // 演示类型转换异常
        try {
            TypeConverter.convertValue("invalid-number", int.class);
        } catch (Exception e) {
            System.out.println("捕获到类型转换异常: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        
        context.close();
        System.out.println("✓ 异常处理演示完成\n");
    }
}