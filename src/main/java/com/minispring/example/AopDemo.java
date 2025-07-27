package com.minispring.example;

import com.minispring.ioc.context.AnnotationConfigApplicationContext;
import com.minispring.example.aop.AopConfig;
import com.minispring.example.aop.BusinessService;

/**
 * AOP功能演示类
 * 展示CGLIB-based AOP的各种通知类型
 */
public class AopDemo {
    
    public static void main(String[] args) {
        System.out.println("========== Mini Spring AOP Demo 开始 ==========");
        
        // 创建应用上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AopConfig.class);
        
        try {
            // 获取业务服务Bean（这应该是被AOP代理的对象）
            BusinessService businessService = context.getBean(BusinessService.class);
            
            System.out.println("\n代理对象类型: " + businessService.getClass().getName());
            System.out.println("是否为CGLIB代理: " + businessService.getClass().getName().contains("EnhancerByCGLIB"));
            
            // 测试1: 普通方法调用（前置、后置、返回后通知）
            System.out.println("\n" + "=".repeat(50));
            System.out.println("测试1: 普通方法调用");
            System.out.println("=".repeat(50));
            String result1 = businessService.processData("test data");
            System.out.println("最终返回结果: " + result1);
            
            // 测试2: 性能监控方法（环绕通知）
            System.out.println("\n" + "=".repeat(50));
            System.out.println("测试2: 性能监控方法（环绕通知）");
            System.out.println("=".repeat(50));
            int result2 = businessService.performOperation("计算任务");
            System.out.println("最终返回结果: " + result2);
            
            // 测试3: void方法调用
            System.out.println("\n" + "=".repeat(50));
            System.out.println("测试3: Void方法调用");
            System.out.println("=".repeat(50));
            businessService.voidMethod("Hello AOP");
            
            // 测试4: 正常的风险操作
            System.out.println("\n" + "=".repeat(50));
            System.out.println("测试4: 正常的风险操作");
            System.out.println("=".repeat(50));
            businessService.riskyOperation(false);
            
            // 测试5: 异常情况（异常后通知）
            System.out.println("\n" + "=".repeat(50));
            System.out.println("测试5: 异常情况（异常后通知）");
            System.out.println("=".repeat(50));
            try {
                businessService.riskyOperation(true);
            } catch (RuntimeException e) {
                System.out.println("捕获到业务异常: " + e.getMessage());
            }
            
        } finally {
            // 关闭上下文
            context.close();
        }
        
        System.out.println("\n========== Mini Spring AOP Demo 结束 ==========");
    }
}