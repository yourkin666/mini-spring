package com.minispring.ioc.beans;

/**
 * Bean定义不存在异常
 */
public class NoSuchBeanDefinitionException extends BeansException {
    
    private final String beanName;
    private final Class<?> beanType;
    
    public NoSuchBeanDefinitionException(String beanName) {
        super("No bean named '" + beanName + "' available");
        this.beanName = beanName;
        this.beanType = null;
    }
    
    public NoSuchBeanDefinitionException(Class<?> beanType) {
        super("No qualifying bean of type '" + beanType.getName() + "' available");
        this.beanName = null;
        this.beanType = beanType;
    }
    
    public NoSuchBeanDefinitionException(String beanName, String message) {
        super(message);
        this.beanName = beanName;
        this.beanType = null;
    }
    
    public String getBeanName() {
        return beanName;
    }
    
    public Class<?> getBeanType() {
        return beanType;
    }
}