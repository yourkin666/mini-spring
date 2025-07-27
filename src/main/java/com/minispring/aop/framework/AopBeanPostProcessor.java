package com.minispring.aop.framework;

import com.minispring.aop.annotation.Aspect;
import com.minispring.ioc.beans.BeanFactory;
import com.minispring.ioc.beans.BeanPostProcessor;
import com.minispring.ioc.beans.BeansException;
import com.minispring.ioc.beans.DefaultBeanFactory;
import com.minispring.ioc.core.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AOP Bean后置处理器
 * 这是Spring AOP的核心集成点：在Bean初始化后检查是否需要创建代理
 * 实现BeanPostProcessor接口，集成到IoC容器的Bean生命周期中
 */
public class AopBeanPostProcessor implements BeanPostProcessor, DefaultBeanFactory.SmartInstantiationAwareBeanPostProcessor {
    
    private final BeanFactory beanFactory;
    private final List<Object> aspectInstances = new ArrayList<>();
    private final Map<String, Object> proxyCache = new ConcurrentHashMap<>();
    
    public AopBeanPostProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    /**
     * Bean初始化前的处理逻辑
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean; // 初始化前不做特殊处理
    }
    
    /**
     * Bean初始化后的处理逻辑 - BeanPostProcessor接口实现
     * 检查是否是切面类或需要被代理的类
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 检查是否已经是代理对象
        if (isProxyObject(bean)) {
            return bean;
        }
        
        // 检查是否是切面类
        if (isAspectClass(bean.getClass())) {
            registerAspect(bean);
            return bean; // 切面类本身不需要被代理
        }
        
        // 检查是否需要为此Bean创建代理
        if (shouldCreateProxy(bean.getClass())) {
            return createProxy(bean, beanName);
        }
        
        return bean;
    }
    
    /**
     * 检查是否是切面类
     */
    private boolean isAspectClass(Class<?> clazz) {
        return ReflectionUtils.hasAnnotation(clazz, Aspect.class);
    }
    
    /**
     * 注册切面实例
     */
    private void registerAspect(Object aspectInstance) {
        if (!aspectInstances.contains(aspectInstance)) {
            aspectInstances.add(aspectInstance);
            System.out.println("注册切面: " + aspectInstance.getClass().getSimpleName());
        }
    }
    
    /**
     * 判断是否需要为Bean创建代理
     * 检查是否有任何切面匹配此Bean的方法
     */
    private boolean shouldCreateProxy(Class<?> targetClass) {
        // 避免为基础设施类创建代理
        if (isInfrastructureClass(targetClass)) {
            return false;
        }
        
        // 检查是否有切面匹配此类
        return hasMatchingAspect(targetClass);
    }
    
    /**
     * 检查是否是基础设施类（不需要代理）
     */
    private boolean isInfrastructureClass(Class<?> clazz) {
        String className = clazz.getName();
        return className.startsWith("com.minispring.ioc.beans.") ||
               className.startsWith("com.minispring.ioc.context.") ||
               className.startsWith("com.minispring.aop.") ||
               className.startsWith("java.") ||
               className.startsWith("javax.") ||
               className.contains("CGLIB");
    }
    
    /**
     * 检查是否有匹配的切面
     */
    private boolean hasMatchingAspect(Class<?> targetClass) {
        if (aspectInstances.isEmpty()) {
            return false;
        }
        
        // 简化检查：如果有任何切面，就认为可能需要代理
        // 实际匹配会在代理执行时进行
        return true;
    }
    
    /**
     * 创建代理对象 - 使用简化的ProxyFactory
     */
    private Object createProxy(Object bean, String beanName) {
        // 检查缓存
        Object cachedProxy = proxyCache.get(beanName);
        if (cachedProxy != null) {
            return cachedProxy;
        }
        
        try {
            // 解析所有切面信息
            List<AspectInfo> allAspectInfos = new ArrayList<>();
            for (Object aspectInstance : aspectInstances) {
                List<AspectInfo> aspectInfos = AspectParser.parseAspect(aspectInstance);
                allAspectInfos.addAll(aspectInfos);
            }
            
            // 使用ProxyFactory创建CGLIB代理
            ProxyFactory proxyFactory = new ProxyFactory(bean, allAspectInfos);
            Object proxy = proxyFactory.getProxy();
            
            // 缓存代理对象
            proxyCache.put(beanName, proxy);
            
            System.out.println("为Bean创建CGLIB代理: " + beanName + " -> " + proxy.getClass().getSimpleName());
            return proxy;
            
        } catch (Exception e) {
            System.err.println("创建CGLIB代理失败 for bean: " + beanName + ", 错误: " + e.getMessage());
            return bean; // 代理创建失败时返回原对象
        }
    }
    
    /**
     * 检查是否是代理对象
     */
    private boolean isProxyObject(Object bean) {
        return bean.getClass().getName().contains("EnhancerByCGLIB");
    }
    
    /**
     * 获取所有已注册的切面实例
     */
    public List<Object> getAspectInstances() {
        return new ArrayList<>(aspectInstances);
    }
    
    /**
     * 早期Bean引用处理 - 解决循环依赖中的AOP代理问题
     * SmartInstantiationAwareBeanPostProcessor接口实现
     */
    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        // 如果需要代理，则提前创建代理（用于循环依赖解决）
        if (shouldCreateProxy(bean.getClass())) {
            return createProxy(bean, beanName + "_early");
        }
        return bean;
    }
    
    /**
     * 清除缓存（用于测试）
     */
    public void clearCache() {
        proxyCache.clear();
        aspectInstances.clear();
    }
}