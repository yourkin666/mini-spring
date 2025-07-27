package com.minispring.ioc.beans;

/**
 * Bean后置处理器接口 - Spring核心扩展机制
 * 允许在Bean实例化前后进行自定义处理
 */
public interface BeanPostProcessor {
    
    /**
     * 在Bean初始化前调用
     * @param bean Bean实例
     * @param beanName Bean名称
     * @return 处理后的Bean实例（可以是原实例或代理）
     * @throws BeansException 处理异常
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
    
    /**
     * 在Bean初始化后调用
     * @param bean Bean实例
     * @param beanName Bean名称  
     * @return 处理后的Bean实例（可以是原实例或代理）
     * @throws BeansException 处理异常
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}