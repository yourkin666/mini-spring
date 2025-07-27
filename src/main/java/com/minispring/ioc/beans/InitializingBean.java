package com.minispring.ioc.beans;

/**
 * 初始化Bean接口 - Spring生命周期回调
 * 在Bean属性设置完成后调用
 */
public interface InitializingBean {
    
    /**
     * Bean属性设置完成后的初始化回调
     * 在@PostConstruct注解方法执行后调用
     * @throws Exception 初始化异常
     */
    void afterPropertiesSet() throws Exception;
}