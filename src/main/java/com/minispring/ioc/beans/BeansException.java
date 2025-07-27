package com.minispring.ioc.beans;

/**
 * Bean相关异常的基类
 * 所有Bean处理过程中的异常都继承自此类
 */
public class BeansException extends RuntimeException {
    
    public BeansException() {
        super();
    }
    
    public BeansException(String message) {
        super(message);
    }
    
    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public BeansException(Throwable cause) {
        super(cause);
    }
}