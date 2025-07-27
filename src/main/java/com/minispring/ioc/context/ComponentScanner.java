package com.minispring.ioc.context;

import com.minispring.ioc.annotation.Component;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

/**
 * 组件扫描器 - Spring风格的包扫描实现
 * 扫描指定包路径下的@Component注解类
 */
public class ComponentScanner {
    
    /**
     * 扫描指定包路径下的组件
     * @param basePackage 基础包路径
     * @return 扫描到的组件类集合
     */
    public Set<Class<?>> scan(String basePackage) {
        Set<Class<?>> components = new HashSet<>();
        
        try {
            // 使用Reflections库进行包扫描
            Reflections reflections = new Reflections(basePackage);
            
            // 扫描所有带@Component注解的类
            Set<Class<?>> componentClasses = reflections.getTypesAnnotatedWith(Component.class);
            components.addAll(componentClasses);
            
            System.out.println("扫描包 '" + basePackage + "' 发现 " + components.size() + " 个组件");
            
        } catch (Exception e) {
            System.err.println("扫描包失败: " + basePackage + ", 错误: " + e.getMessage());
        }
        
        return components;
    }
    
    /**
     * 扫描多个包路径
     * @param basePackages 基础包路径数组
     * @return 扫描到的组件类集合
     */
    public Set<Class<?>> scan(String... basePackages) {
        Set<Class<?>> allComponents = new HashSet<>();
        
        for (String basePackage : basePackages) {
            allComponents.addAll(scan(basePackage));
        }
        
        return allComponents;
    }
}