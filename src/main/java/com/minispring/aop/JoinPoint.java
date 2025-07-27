package com.minispring.aop;

import java.lang.reflect.Method;

/**
 * 连接点接口，提供访问当前被通知方法的反射信息
 * 模仿Spring AOP的JoinPoint接口
 */
public interface JoinPoint {
    
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
    
    /**
     * 获取连接点的签名信息
     */
    String getSignature();
}