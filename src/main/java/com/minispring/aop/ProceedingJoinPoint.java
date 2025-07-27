package com.minispring.aop;

import java.lang.reflect.Method;

/**
 * 执行连接点，用于环绕通知中控制目标方法的执行
 * 模仿Spring AOP的ProceedingJoinPoint接口
 */
public interface ProceedingJoinPoint {
    
    /**
     * 执行目标方法
     * @return 目标方法的返回值
     * @throws Throwable 目标方法可能抛出的异常
     */
    Object proceed() throws Throwable;
    
    /**
     * 使用指定的参数执行目标方法
     * @param args 方法参数
     * @return 目标方法的返回值
     * @throws Throwable 目标方法可能抛出的异常
     */
    Object proceed(Object[] args) throws Throwable;
    
    /**
     * 获取目标方法
     */
    Method getMethod();
    
    /**
     * 获取方法参数
     */
    Object[] getArgs();
    
    /**
     * 获取目标对象
     */
    Object getTarget();
    
    /**
     * 获取代理对象
     */
    Object getThis();
}