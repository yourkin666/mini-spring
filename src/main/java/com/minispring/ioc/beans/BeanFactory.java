package com.minispring.ioc.beans;

/**
 * Bean工厂接口 - IoC容器的核心接口
 * 定义了获取Bean实例的基本方法
 */
public interface BeanFactory {
    
    /**
     * 根据名称获取Bean实例
     * @param name Bean名称
     * @return Bean实例
     * @throws BeansException Bean获取失败时抛出
     */
    Object getBean(String name) throws BeansException;
    
    /**
     * 根据类型获取Bean实例
     * @param type Bean类型
     * @param <T> 泛型类型
     * @return Bean实例
     * @throws BeansException Bean获取失败时抛出
     */
    <T> T getBean(Class<T> type) throws BeansException;
    
    /**
     * 根据名称和类型获取Bean实例
     * @param name Bean名称
     * @param type Bean类型
     * @param <T> 泛型类型
     * @return Bean实例
     * @throws BeansException Bean获取失败时抛出
     */
    <T> T getBean(String name, Class<T> type) throws BeansException;
    
    /**
     * 检查是否包含指定名称的Bean
     * @param name Bean名称
     * @return 是否包含
     */
    boolean containsBean(String name);
    
    /**
     * 检查指定名称的Bean是否为单例
     * @param name Bean名称
     * @return 是否为单例
     */
    boolean isSingleton(String name);
    
    /**
     * 获取指定名称Bean的类型
     * @param name Bean名称
     * @return Bean类型
     */
    Class<?> getType(String name);
}