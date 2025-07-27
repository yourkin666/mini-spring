package com.minispring.example.integration;

import com.minispring.ioc.context.AnnotationConfigApplicationContext;

/**
 * IoC + AOP 集成演示
 * 展示完整的Spring功能：依赖注入 + 面向切面编程
 */
public class IntegratedDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Mini-Spring IoC+AOP 集成演示 ===");
        
        // 创建集成容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(IntegratedConfig.class);
        
        try {
            // 演示AOP + IoC集成
            System.out.println("\n1. AOP切面拦截演示:");
            BusinessService businessService = context.getBean(BusinessService.class);
            
            // 调用方法，观察AOP拦截
            businessService.performBusiness("用户注册");
            
            System.out.println("\n2. 异常处理切面演示:");
            try {
                businessService.performRiskyOperation();
            } catch (Exception e) {
                System.out.println("捕获到异常: " + e.getMessage());
            }
            
            System.out.println("\n3. 环绕通知演示:");
            String result = businessService.calculateResult(10, 20);
            System.out.println("计算结果: " + result);
            
            System.out.println("\n=== IoC+AOP集成演示完成 ===");
            
        } finally {
            context.close();
        }
    }
}