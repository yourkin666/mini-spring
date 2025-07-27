package com.minispring.ioc.beans;

/**
 * Bean循环依赖异常
 */
public class BeanCurrentlyInCreationException extends BeanCreationException {
    
    public BeanCurrentlyInCreationException(String beanName) {
        super(beanName, "Requested bean is currently in creation: Is there an unresolvable circular reference?");
    }
    
    public BeanCurrentlyInCreationException(String beanName, String message) {
        super(beanName, message);
    }
}