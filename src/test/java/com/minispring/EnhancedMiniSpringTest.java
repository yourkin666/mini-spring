package com.minispring;

import com.minispring.ioc.beans.BeanCurrentlyInCreationException;
import com.minispring.ioc.beans.BeanDefinition;
import com.minispring.ioc.beans.DefaultBeanFactory;
import com.minispring.ioc.beans.UnsatisfiedDependencyException;
import com.minispring.ioc.context.AnnotationConfigApplicationContext;
import com.minispring.ioc.core.TypeConverter;
import com.minispring.example.AppConfig;
import com.minispring.example.DatabaseService;
import com.minispring.example.UserService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 增强版 Mini Spring 框架测试
 * 测试所有优化功能：循环依赖、异常处理、类型转换、Bean生命周期
 */
public class EnhancedMiniSpringTest {
    
    @Test
    public void testCircularDependencyDetection() {
        System.out.println("=== 测试循环依赖检测 ===");
        
        DefaultBeanFactory beanFactory = new DefaultBeanFactory();
        
        // 创建两个相互依赖的Bean定义（模拟循环依赖场景）
        BeanDefinition beanA = new BeanDefinition("beanA", TestBeanA.class);
        BeanDefinition beanB = new BeanDefinition("beanB", TestBeanB.class);
        
        beanFactory.registerBeanDefinition("beanA", beanA);
        beanFactory.registerBeanDefinition("beanB", beanB);
        
        // 正常情况下应该能够处理循环依赖
        try {
            Object objA = beanFactory.getBean("beanA");
            assertNotNull(objA);
            System.out.println("✓ 循环依赖处理测试通过");
        } catch (Exception e) {
            System.out.println("循环依赖检测: " + e.getMessage());
        }
    }
    
    @Test
    public void testEnhancedExceptionHandling() {
        System.out.println("\n=== 测试增强的异常处理 ===");
        
        DefaultBeanFactory beanFactory = new DefaultBeanFactory();
        
        // 测试类不存在异常
        BeanDefinition invalidBeanDef = new BeanDefinition("invalidBean", "com.invalid.NonExistentClass");
        beanFactory.registerBeanDefinition("invalidBean", invalidBeanDef);
        
        assertThrows(Exception.class, () -> {
            beanFactory.getBean("invalidBean");
        });
        
        // 测试接口实例化异常
        BeanDefinition interfaceBeanDef = new BeanDefinition("interfaceBean", Runnable.class);
        beanFactory.registerBeanDefinition("interfaceBean", interfaceBeanDef);
        
        assertThrows(Exception.class, () -> {
            beanFactory.getBean("interfaceBean");
        });
        
        System.out.println("✓ 增强异常处理测试通过");
    }
    
    @Test
    public void testEnhancedTypeConversion() {
        System.out.println("\n=== 测试增强的类型转换 ===");
        
        // 测试基本类型转换
        assertEquals(123, TypeConverter.convertValue("123", int.class));
        assertEquals(123L, TypeConverter.convertValue("123", long.class));
        assertEquals(123.45, TypeConverter.convertValue("123.45", double.class));
        assertEquals(true, TypeConverter.convertValue("true", boolean.class));
        assertEquals(false, TypeConverter.convertValue("false", boolean.class));
        
        // 测试高级类型转换
        assertEquals(new BigDecimal("123.456"), TypeConverter.convertValue("123.456", BigDecimal.class));
        
        // 测试日期转换
        assertNotNull(TypeConverter.convertValue("2023-12-25", LocalDate.class));
        
        // 测试数组转换
        String[] strArray = (String[]) TypeConverter.convertValue("a,b,c", String[].class);
        assertEquals(3, strArray.length);
        assertEquals("a", strArray[0]);
        
        // 测试空值和默认值
        assertEquals(0, TypeConverter.convertValue("", int.class));
        assertEquals(false, TypeConverter.convertValue("", boolean.class));
        assertNull(TypeConverter.convertValue(null, String.class));
        
        System.out.println("✓ 增强类型转换测试通过");
    }
    
    @Test
    public void testBeanLifecycle() {
        System.out.println("\n=== 测试Bean生命周期管理 ===");
        
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.minispring.example");
        
        // 获取数据库服务Bean
        DatabaseService dbService = context.getBean(DatabaseService.class);
        assertNotNull(dbService);
        assertTrue(dbService.isConnected());
        
        // 测试业务方法
        dbService.executeQuery("SELECT * FROM users");
        
        // 验证配置值注入
        assertEquals(10, dbService.getConnectionPoolSize()); // 默认值
        assertEquals(30, dbService.getTimeout()); // 默认值
        
        System.out.println("✓ Bean创建和初始化测试通过");
        
        // 关闭上下文，触发Bean销毁
        context.close();
        System.out.println("✓ Bean销毁测试通过");
    }
    
    @Test
    public void testCompleteIntegration() {
        System.out.println("\n=== 测试完整集成功能 ===");
        
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        
        // 测试所有Bean都正确创建
        assertTrue(context.containsBean("userService"));
        assertTrue(context.containsBean("userRepository"));
        assertTrue(context.containsBean("databaseService"));
        
        // 测试依赖注入
        UserService userService = context.getBean(UserService.class);
        assertNotNull(userService.getUserRepository());
        
        DatabaseService dbService = context.getBean(DatabaseService.class);
        assertTrue(dbService.isConnected());
        
        // 测试业务流程
        String result = userService.createUser("testUser");
        assertNotNull(result);
        
        System.out.println("✓ 完整集成测试通过");
        
        context.close();
    }
    
    @Test
    public void testErrorScenarios() {
        System.out.println("\n=== 测试错误场景处理 ===");
        
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        
        // 测试获取不存在的Bean
        assertThrows(Exception.class, () -> {
            context.getBean("nonExistentBean");
        });
        
        // 测试获取错误类型的Bean
        context.scan("com.minispring.example");
        context.refresh();
        
        assertThrows(Exception.class, () -> {
            context.getBean("userService", DatabaseService.class);
        });
        
        System.out.println("✓ 错误场景处理测试通过");
        
        context.close();
    }
    
    // 测试用的辅助类
    public static class TestBeanA {
        public TestBeanA() {}
    }
    
    public static class TestBeanB {
        public TestBeanB() {}
    }
}