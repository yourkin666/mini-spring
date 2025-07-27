package com.minispring.aop.framework;

import com.minispring.aop.JoinPoint;
import com.minispring.aop.ProceedingJoinPoint;
import com.minispring.aop.annotation.*;

import java.lang.reflect.Method;

/**
 * 切面信息封装类
 * 包含切面对象、通知方法和通知类型等信息
 */
public class AspectInfo {
    
    private final Object aspectInstance;
    private final Method adviceMethod;
    private final AdviceType adviceType;
    private final String pointcutExpression;
    
    public AspectInfo(Object aspectInstance, Method adviceMethod, AdviceType adviceType, String pointcutExpression) {
        this.aspectInstance = aspectInstance;
        this.adviceMethod = adviceMethod;
        this.adviceType = adviceType;
        this.pointcutExpression = pointcutExpression;
    }
    
    /**
     * 执行通知方法
     */
    public Object invoke(ProceedingJoinPoint proceedingJoinPoint, JoinPoint joinPoint) throws Throwable {
        switch (adviceType) {
            case BEFORE:
                return executeBefore(joinPoint, proceedingJoinPoint);
            case AFTER:
                return executeAfter(joinPoint, proceedingJoinPoint);
            case AROUND:
                return executeAround(proceedingJoinPoint);
            case AFTER_RETURNING:
                return executeAfterReturning(joinPoint, proceedingJoinPoint);
            case AFTER_THROWING:
                return executeAfterThrowing(joinPoint, proceedingJoinPoint);
            default:
                return proceedingJoinPoint.proceed();
        }
    }
    
    /**
     * 执行前置通知
     */
    private Object executeBefore(JoinPoint joinPoint, ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 先执行前置通知
        invokeAdviceMethod(joinPoint);
        // 再执行目标方法
        return proceedingJoinPoint.proceed();
    }
    
    /**
     * 执行后置通知
     */
    private Object executeAfter(JoinPoint joinPoint, ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            // 先执行目标方法
            return proceedingJoinPoint.proceed();
        } finally {
            // 无论是否抛异常都执行后置通知
            invokeAdviceMethod(joinPoint);
        }
    }
    
    /**
     * 执行环绕通知
     */
    private Object executeAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 环绕通知方法需要接收ProceedingJoinPoint参数
        return invokeAdviceMethod(proceedingJoinPoint);
    }
    
    /**
     * 执行返回后通知
     */
    private Object executeAfterReturning(JoinPoint joinPoint, ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        
        // 执行返回后通知，可能需要传递返回值
        AfterReturning afterReturning = adviceMethod.getAnnotation(AfterReturning.class);
        if (afterReturning != null && !afterReturning.returning().isEmpty()) {
            // 如果指定了returning参数，需要传递返回值
            invokeAdviceMethod(joinPoint, result);
        } else {
            invokeAdviceMethod(joinPoint);
        }
        
        return result;
    }
    
    /**
     * 执行异常后通知
     */
    private Object executeAfterThrowing(JoinPoint joinPoint, ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            // 执行异常后通知
            AfterThrowing afterThrowing = adviceMethod.getAnnotation(AfterThrowing.class);
            if (afterThrowing != null && !afterThrowing.throwing().isEmpty()) {
                // 如果指定了throwing参数，需要传递异常对象
                invokeAdviceMethod(joinPoint, throwable);
            } else {
                invokeAdviceMethod(joinPoint);
            }
            throw throwable;
        }
    }
    
    /**
     * 调用通知方法
     */
    private Object invokeAdviceMethod(Object... args) throws Throwable {
        try {
            adviceMethod.setAccessible(true);
            return adviceMethod.invoke(aspectInstance, args);
        } catch (Exception e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else if (e.getCause() instanceof Error) {
                throw (Error) e.getCause();
            } else {
                throw new RuntimeException("Failed to invoke advice method: " + adviceMethod.getName(), e);
            }
        }
    }
    
    // Getters
    public Object getAspectInstance() {
        return aspectInstance;
    }
    
    public Method getAdviceMethod() {
        return adviceMethod;
    }
    
    public AdviceType getAdviceType() {
        return adviceType;
    }
    
    public String getPointcutExpression() {
        return pointcutExpression;
    }
    
    /**
     * 通知类型枚举
     */
    public enum AdviceType {
        BEFORE,
        AFTER,
        AROUND,
        AFTER_RETURNING,
        AFTER_THROWING
    }
}