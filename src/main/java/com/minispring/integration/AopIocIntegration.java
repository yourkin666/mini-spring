package com.minispring.integration;

import com.minispring.aop.framework.AopBeanPostProcessor;
import com.minispring.ioc.beans.BeanFactory;

/**
 * AOP与IoC集成桥梁
 * 负责将AOP功能无缝集成到IoC容器中
 */
public class AopIocIntegration {
    
    private final BeanFactory beanFactory;
    private final AopBeanPostProcessor aopBeanPostProcessor;
    
    public AopIocIntegration(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.aopBeanPostProcessor = new AopBeanPostProcessor(beanFactory);
    }
    
    /**
     * 获取AOP后置处理器
     * 供IoC容器在Bean初始化后调用
     */
    public AopBeanPostProcessor getAopBeanPostProcessor() {
        return aopBeanPostProcessor;
    }
    
    /**
     * 检查是否启用AOP
     */
    public boolean isAopEnabled() {
        return aopBeanPostProcessor != null;
    }
}