package com.minispring.example.integration;

import com.minispring.aop.JoinPoint;
import com.minispring.aop.ProceedingJoinPoint;
import com.minispring.aop.annotation.*;
import com.minispring.ioc.annotation.Component;

/**
 * 日志切面 - 演示AOP功能
 */
@Aspect
@Component
public class LoggingAspect {
    
    @Before("execution(* com.minispring.example.integration.BusinessService.*(..))")
    public void beforeMethod(JoinPoint joinPoint) {
        System.out.println("[前置通知] 方法调用前: " + joinPoint.getMethod().getName());
    }
    
    @After("execution(* com.minispring.example.integration.BusinessService.*(..))")
    public void afterMethod(JoinPoint joinPoint) {
        System.out.println("[后置通知] 方法调用后: " + joinPoint.getMethod().getName());
    }
    
    @AfterReturning("execution(* com.minispring.example.integration.BusinessService.calculate*(..))")
    public void afterReturning(JoinPoint joinPoint) {
        System.out.println("[返回后通知] 方法正常返回: " + joinPoint.getMethod().getName());
    }
    
    @AfterThrowing("execution(* com.minispring.example.integration.BusinessService.performRisky*(..))")
    public void afterThrowing(JoinPoint joinPoint) {
        System.out.println("[异常后通知] 方法抛出异常: " + joinPoint.getMethod().getName());
    }
    
    @Around("execution(* com.minispring.example.integration.BusinessService.calculate*(..))")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("[环绕通知-前] 开始执行: " + joinPoint.getMethod().getName());
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            System.out.println("[环绕通知-后] 执行完成，耗时: " + (endTime - startTime) + "ms");
            return result;
        } catch (Throwable throwable) {
            System.out.println("[环绕通知-异常] 执行出现异常: " + throwable.getMessage());
            throw throwable;
        }
    }
}