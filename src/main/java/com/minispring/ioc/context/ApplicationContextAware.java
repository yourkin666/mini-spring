package com.minispring.ioc.context;

/**
 * ApplicationContext感知接口
 * 实现此接口的Bean能够获得对ApplicationContext的引用
 * 体现Spring的容器感知设计模式
 */
public interface ApplicationContextAware {
    
    /**
     * 设置运行此对象的ApplicationContext
     * 在填充普通bean属性之后但在初始化回调之前调用
     * 
     * @param applicationContext 此对象要使用的ApplicationContext对象
     */
    void setApplicationContext(ApplicationContext applicationContext);
}
