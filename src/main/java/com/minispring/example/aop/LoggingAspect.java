package com.minispring.example.aop;

import com.minispring.ioc.annotation.Component;
import com.minispring.aop.JoinPoint;
import com.minispring.aop.ProceedingJoinPoint;
import com.minispring.aop.annotation.*;

/**
 * 日志切面示例
 * 演示各种通知类型的使用
 */
@Aspect
@Component
public class LoggingAspect {
    
    /**
     * 定义切点：拦截example包下所有类的所有方法
     */
    @Pointcut("execution(* com.minispring.example..*(..))")
    public void serviceLayer() {
    }
    
    /**
     * 前置通知：在方法执行前记录日志
     */
    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("=== [前置通知] 准备执行方法: " + joinPoint.getMethod().getName() + " ===");
        System.out.println("目标类: " + joinPoint.getTarget().getClass().getSimpleName());
        System.out.println("方法参数: " + java.util.Arrays.toString(joinPoint.getArgs()));
    }
    
    /**
     * 后置通知：在方法执行后记录日志（无论是否抛异常）
     */
    @After("serviceLayer()")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("=== [后置通知] 方法执行完毕: " + joinPoint.getMethod().getName() + " ===");
    }
    
    /**
     * 返回后通知：在方法正常返回后记录返回值
     */
    @AfterReturning(value = "serviceLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("=== [返回后通知] 方法正常返回: " + joinPoint.getMethod().getName() + " ===");
        System.out.println("返回值: " + result);
    }
    
    /**
     * 异常后通知：在方法抛出异常后记录异常信息
     */
    @AfterThrowing(value = "serviceLayer()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        System.out.println("=== [异常后通知] 方法抛出异常: " + joinPoint.getMethod().getName() + " ===");
        System.out.println("异常类型: " + exception.getClass().getSimpleName());
        System.out.println("异常消息: " + exception.getMessage());
    }
    
    /**
     * 环绕通知：最强大的通知类型，可以控制方法的执行
     */
    @Around("execution(* com.minispring.example..*.performOperation(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getMethod().getName();
        System.out.println("=== [环绕通知-前] 开始执行: " + methodName + " ===");
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            long endTime = System.currentTimeMillis();
            System.out.println("=== [环绕通知-后] 方法执行成功，耗时: " + (endTime - startTime) + "ms ===");
            
            return result;
        } catch (Throwable throwable) {
            long endTime = System.currentTimeMillis();
            System.out.println("=== [环绕通知-异常] 方法执行失败，耗时: " + (endTime - startTime) + "ms ===");
            throw throwable;
        }
    }
}