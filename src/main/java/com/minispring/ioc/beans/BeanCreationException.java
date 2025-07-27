package com.minispring.ioc.beans;

/**
 * Bean创建异常
 */
public class BeanCreationException extends BeansException {
    
    private final String beanName;
    
    public BeanCreationException(String beanName, String message) {
        super("Error creating bean with name '" + beanName + "': " + message);
        this.beanName = beanName;
    }
    
    public BeanCreationException(String beanName, String message, Throwable cause) {
        super("Error creating bean with name '" + beanName + "': " + message, cause);
        this.beanName = beanName;
    }
    
    public String getBeanName() {
        return beanName;
    }
}