package com.minispring.example;

import com.minispring.ioc.context.AnnotationConfigApplicationContext;
import com.minispring.example.integration.IntegratedConfig;
import com.minispring.example.integration.BusinessService;

/**
 * CGLIB统一代理机制演示
 * 验证所有Bean都使用CGLIB代理，无论是否实现接口
 */
public class CglibProxyDemo {
    
    public static void main(String[] args) {
        System.out.println("=== CGLIB统一代理机制演示 ===");
        
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(IntegratedConfig.class);
        
        try {
            // 获取业务服务Bean
            BusinessService businessService = context.getBean(BusinessService.class);
            
            // 检查代理类型
            System.out.println("\n📋 代理信息分析:");
            System.out.println("BusinessService实际类型: " + businessService.getClass().getName());
            System.out.println("是否CGLIB代理: " + businessService.getClass().getName().contains("CGLIB"));
            System.out.println("父类: " + businessService.getClass().getSuperclass().getName());
            
            // 执行业务方法，观察AOP拦截
            System.out.println("\n🚀 执行业务方法:");
            businessService.performBusiness("CGLIB代理测试");
            
            // 测试所有通知类型
            System.out.println("\n🔄 测试环绕通知:");
            String result = businessService.calculateResult(5, 3);
            System.out.println("计算结果: " + result);
            
            System.out.println("\n⚠️ 测试异常通知:");
            try {
                businessService.performRiskyOperation();
            } catch (Exception e) {
                System.out.println("捕获异常: " + e.getMessage());
            }
            
            System.out.println("\n✅ CGLIB统一代理机制验证完成!");
            
        } finally {
            context.close();
        }
    }
}