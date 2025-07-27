package com.minispring.aop.framework;

import com.minispring.aop.proxy.CglibAopProxy;
import java.util.List;

/**
 * 代理工厂 - 统一使用CGLIB代理
 * 简化的AOP代理创建机制，专注于CGLIB字节码增强
 */
public class ProxyFactory {
    
    private Object target;
    private List<AspectInfo> aspects;
    
    public ProxyFactory() {
    }
    
    public ProxyFactory(Object target) {
        this.target = target;
    }
    
    public ProxyFactory(Object target, List<AspectInfo> aspects) {
        this.target = target;
        this.aspects = aspects;
    }
    
    public void setTarget(Object target) {
        this.target = target;
    }
    
    public void setAspects(List<AspectInfo> aspects) {
        this.aspects = aspects;
    }
    
    /**
     * 创建CGLIB代理对象
     * 统一使用CGLIB技术，适用于所有类型的Bean
     */
    public Object getProxy() {
        return getProxy(null);
    }
    
    /**
     * 使用指定类加载器创建CGLIB代理对象
     */
    public Object getProxy(ClassLoader classLoader) {
        if (target == null) {
            throw new IllegalStateException("Target object cannot be null");
        }
        
        CglibAopProxy cglibProxy = new CglibAopProxy(target, aspects);
        return cglibProxy.getProxy(classLoader);
    }
    
    /**
     * 静态工厂方法 - 快速创建代理
     */
    public static Object createProxy(Object target, List<AspectInfo> aspects) {
        ProxyFactory factory = new ProxyFactory(target, aspects);
        return factory.getProxy();
    }
    
    /**
     * 检查目标对象是否需要代理
     */
    public boolean shouldProxy() {
        return target != null && aspects != null && !aspects.isEmpty();
    }
}