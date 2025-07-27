package com.minispring.example.ioc;

import com.minispring.ioc.context.AnnotationConfigApplicationContext;

/**
 * IoC独立功能演示
 * 展示纯IoC容器功能：依赖注入、生命周期管理、组件扫描
 */
public class IocDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Mini-Spring IoC容器演示 ===");
        
        // 创建IoC容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(IocConfig.class);
        
        try {
            // 演示依赖注入
            System.out.println("\n1. 依赖注入演示:");
            UserService userService = context.getBean(UserService.class);
            userService.performAction();
            
            // 演示单例模式
            System.out.println("\n2. 单例模式验证:");
            UserService userService2 = context.getBean(UserService.class);
            System.out.println("两次获取的UserService是同一个实例: " + (userService == userService2));
            
            // 演示属性注入
            System.out.println("\n3. 属性注入演示:");
            ConfigService configService = context.getBean(ConfigService.class);
            configService.showConfig();
            
            // 演示生命周期管理
            System.out.println("\n4. Bean生命周期演示:");
            LifecycleBean lifecycleBean = context.getBean(LifecycleBean.class);
            lifecycleBean.doSomething();
            
            System.out.println("\n=== IoC容器演示完成 ===");
            
        } finally {
            // 关闭容器，触发销毁回调
            context.close();
        }
    }
}