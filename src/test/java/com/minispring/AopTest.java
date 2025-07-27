package com.minispring;

import com.minispring.ioc.context.AnnotationConfigApplicationContext;
import com.minispring.example.aop.AopConfig;
import com.minispring.example.aop.BusinessService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AOP功能测试类
 */
public class AopTest {
    
    private AnnotationConfigApplicationContext context;
    private BusinessService businessService;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    @BeforeEach
    void setUp() {
        // 创建应用上下文
        context = new AnnotationConfigApplicationContext(AopConfig.class);
        businessService = context.getBean(BusinessService.class);
        
        // 捕获系统输出用于验证AOP是否生效
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }
    
    @AfterEach
    void tearDown() {
        // 恢复系统输出
        System.setOut(originalOut);
        
        if (context != null) {
            context.close();
        }
    }
    
    @Test
    void testProxyCreation() {
        // 验证是否创建了CGLIB代理
        assertNotNull(businessService);
        assertTrue(businessService.getClass().getName().contains("EnhancerByCGLIB"), 
                "BusinessService should be proxied by CGLIB");
    }
    
    @Test
    void testBeforeAfterAdvice() {
        // 执行业务方法
        String result = businessService.processData("test");
        
        // 验证返回值
        assertEquals("处理完成: TEST", result);
        
        // 验证AOP通知是否执行
        String output = outputStream.toString();
        assertTrue(output.contains("[前置通知]"), "Before advice should be executed");
        assertTrue(output.contains("[后置通知]"), "After advice should be executed");
        assertTrue(output.contains("[返回后通知]"), "AfterReturning advice should be executed");
    }
    
    @Test
    void testAroundAdvice() {
        // 执行带环绕通知的方法
        int result = businessService.performOperation("测试操作");
        
        // 验证返回值
        assertTrue(result > 0);
        
        // 验证环绕通知是否执行
        String output = outputStream.toString();
        assertTrue(output.contains("[环绕通知-前]"), "Around advice (before) should be executed");
        assertTrue(output.contains("[环绕通知-后]"), "Around advice (after) should be executed");
        assertTrue(output.contains("耗时"), "Performance logging should be present");
    }
    
    @Test
    void testAfterThrowingAdvice() {
        // 执行会抛异常的方法
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            businessService.riskyOperation(true);
        });
        
        // 验证异常消息
        assertEquals("模拟业务异常", exception.getMessage());
        
        // 验证异常后通知是否执行
        String output = outputStream.toString();
        assertTrue(output.contains("[前置通知]"), "Before advice should be executed even when exception occurs");
        assertTrue(output.contains("[后置通知]"), "After advice should be executed even when exception occurs");
        assertTrue(output.contains("[异常后通知]"), "AfterThrowing advice should be executed");
        assertFalse(output.contains("[返回后通知]"), "AfterReturning advice should NOT be executed when exception occurs");
    }
    
    @Test
    void testSuccessfulRiskyOperation() {
        // 执行成功的风险操作
        assertDoesNotThrow(() -> {
            businessService.riskyOperation(false);
        });
        
        // 验证所有正常通知都执行了
        String output = outputStream.toString();
        assertTrue(output.contains("[前置通知]"), "Before advice should be executed");
        assertTrue(output.contains("[后置通知]"), "After advice should be executed");
        assertTrue(output.contains("[返回后通知]"), "AfterReturning advice should be executed");
        assertFalse(output.contains("[异常后通知]"), "AfterThrowing advice should NOT be executed on success");
    }
    
    @Test
    void testVoidMethodAdvice() {
        // 执行void方法
        assertDoesNotThrow(() -> {
            businessService.voidMethod("test message");
        });
        
        // 验证通知是否正确执行
        String output = outputStream.toString();
        assertTrue(output.contains("[前置通知]"), "Before advice should work with void methods");
        assertTrue(output.contains("[后置通知]"), "After advice should work with void methods");
        assertTrue(output.contains("[返回后通知]"), "AfterReturning advice should work with void methods");
    }
    
    @Test
    void testMultipleMethodCalls() {
        // 多次调用方法，验证AOP每次都生效
        businessService.processData("data1");
        businessService.processData("data2");
        
        String output = outputStream.toString();
        
        // 计算前置通知的出现次数
        int beforeCount = output.split("\\[前置通知\\]").length - 1;
        int afterCount = output.split("\\[后置通知\\]").length - 1;
        
        assertEquals(2, beforeCount, "Before advice should be executed twice");
        assertEquals(2, afterCount, "After advice should be executed twice");
    }
}