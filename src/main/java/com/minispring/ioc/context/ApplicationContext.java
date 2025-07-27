package com.minispring.ioc.context;

import com.minispring.ioc.beans.BeanFactory;

/**
 * 应用上下文接口 - IoC容器的高级接口
 * 提供更丰富的功能，包括组件扫描、生命周期管理等
 */
public interface ApplicationContext extends BeanFactory {
    
    /**
     * 启动应用上下文
     * 完成Bean的扫描、注册和初始化
     */
    void refresh();
    
    /**
     * 关闭应用上下文
     * 清理资源并调用Bean的销毁方法
     */
    void close();
    
    /**
     * 获取所有Bean的名称
     */
    String[] getBeanDefinitionNames();
    
    /**
     * 获取指定类型的所有Bean实例
     */
    <T> java.util.Map<String, T> getBeansOfType(Class<T> type);
}