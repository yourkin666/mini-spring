package com.minispring.webmvc.handler;

import java.lang.reflect.Method;

/**
 * HandlerMethod类
 * 封装处理器bean实例和处理器方法
 * 体现Spring MVC的方法级处理器设计
 */
public class HandlerMethod {
    
    private final Object bean;
    private final Method method;
    private final Class<?> beanType;
    
    /**
     * 创建HandlerMethod实例
     * @param bean 处理器bean实例
     * @param method 处理器方法
     */
    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        this.beanType = bean.getClass();
    }
    
    /**
     * 创建HandlerMethod实例
     * @param bean 处理器bean实例
     * @param methodName 方法名称
     * @param parameterTypes 参数类型数组
     */
    public HandlerMethod(Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        this.bean = bean;
        this.beanType = bean.getClass();
        this.method = beanType.getMethod(methodName, parameterTypes);
    }
    
    /**
     * 获取处理器bean实例
     */
    public Object getBean() {
        return this.bean;
    }
    
    /**
     * 获取处理器方法
     */
    public Method getMethod() {
        return this.method;
    }
    
    /**
     * 获取处理器bean的类型
     */
    public Class<?> getBeanType() {
        return this.beanType;
    }
    
    /**
     * 获取方法的参数类型
     */
    public Class<?>[] getParameterTypes() {
        return this.method.getParameterTypes();
    }
    
    /**
     * 获取方法的返回类型
     */
    public Class<?> getReturnType() {
        return this.method.getReturnType();
    }
    
    /**
     * 检查方法是否为void返回类型
     */
    public boolean isVoid() {
        return Void.TYPE.equals(getReturnType());
    }
    
    /**
     * 获取短格式的描述信息
     */
    public String getShortLogMessage() {
        return getBeanType().getName() + "#" + this.method.getName() +
                "[" + this.method.getParameterCount() + " args]";
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HandlerMethod)) {
            return false;
        }
        HandlerMethod otherMethod = (HandlerMethod) other;
        return (this.bean.equals(otherMethod.bean) && this.method.equals(otherMethod.method));
    }
    
    @Override
    public int hashCode() {
        return (this.bean.hashCode() * 31 + this.method.hashCode());
    }
    
    @Override
    public String toString() {
        return this.method.toGenericString();
    }
}
