package com.minispring.aop.framework;

import com.minispring.aop.annotation.*;
import com.minispring.ioc.core.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 切面解析器
 * 负责解析@Aspect注解的类，提取切面信息
 */
public class AspectParser {
    
    /**
     * 解析切面类，提取所有的通知方法
     */
    public static List<AspectInfo> parseAspect(Object aspectInstance) {
        Class<?> aspectClass = aspectInstance.getClass();
        
        // 检查是否是切面类
        if (!ReflectionUtils.hasAnnotation(aspectClass, Aspect.class)) {
            throw new IllegalArgumentException("类 " + aspectClass.getName() + " 不是切面类（缺少@Aspect注解）");
        }
        
        List<AspectInfo> aspectInfos = new ArrayList<>();
        Map<String, String> pointcutMap = extractPointcuts(aspectClass);
        
        // 解析所有通知方法
        Method[] methods = ReflectionUtils.getAllMethods(aspectClass);
        for (Method method : methods) {
            AspectInfo aspectInfo = parseAdviceMethod(aspectInstance, method, pointcutMap);
            if (aspectInfo != null) {
                aspectInfos.add(aspectInfo);
            }
        }
        
        return aspectInfos;
    }
    
    /**
     * 提取所有@Pointcut方法定义的切点表达式
     */
    private static Map<String, String> extractPointcuts(Class<?> aspectClass) {
        Map<String, String> pointcutMap = new HashMap<>();
        
        Method[] methods = ReflectionUtils.getAllMethods(aspectClass);
        for (Method method : methods) {
            if (ReflectionUtils.hasAnnotation(method, Pointcut.class)) {
                Pointcut pointcut = ReflectionUtils.getAnnotation(method, Pointcut.class);
                pointcutMap.put(method.getName(), pointcut.value());
            }
        }
        
        return pointcutMap;
    }
    
    /**
     * 解析单个通知方法
     */
    private static AspectInfo parseAdviceMethod(Object aspectInstance, Method method, Map<String, String> pointcutMap) {
        // 检查各种通知注解
        if (ReflectionUtils.hasAnnotation(method, Before.class)) {
            Before before = ReflectionUtils.getAnnotation(method, Before.class);
            String pointcutExpression = resolvePointcutExpression(before.value(), pointcutMap);
            return new AspectInfo(aspectInstance, method, AspectInfo.AdviceType.BEFORE, pointcutExpression);
        }
        
        if (ReflectionUtils.hasAnnotation(method, After.class)) {
            After after = ReflectionUtils.getAnnotation(method, After.class);
            String pointcutExpression = resolvePointcutExpression(after.value(), pointcutMap);
            return new AspectInfo(aspectInstance, method, AspectInfo.AdviceType.AFTER, pointcutExpression);
        }
        
        if (ReflectionUtils.hasAnnotation(method, Around.class)) {
            Around around = ReflectionUtils.getAnnotation(method, Around.class);
            String pointcutExpression = resolvePointcutExpression(around.value(), pointcutMap);
            return new AspectInfo(aspectInstance, method, AspectInfo.AdviceType.AROUND, pointcutExpression);
        }
        
        if (ReflectionUtils.hasAnnotation(method, AfterReturning.class)) {
            AfterReturning afterReturning = ReflectionUtils.getAnnotation(method, AfterReturning.class);
            String pointcutExpression = resolvePointcutExpression(afterReturning.value(), pointcutMap);
            return new AspectInfo(aspectInstance, method, AspectInfo.AdviceType.AFTER_RETURNING, pointcutExpression);
        }
        
        if (ReflectionUtils.hasAnnotation(method, AfterThrowing.class)) {
            AfterThrowing afterThrowing = ReflectionUtils.getAnnotation(method, AfterThrowing.class);
            String pointcutExpression = resolvePointcutExpression(afterThrowing.value(), pointcutMap);
            return new AspectInfo(aspectInstance, method, AspectInfo.AdviceType.AFTER_THROWING, pointcutExpression);
        }
        
        return null; // 不是通知方法
    }
    
    /**
     * 解析切点表达式
     * 如果是方法名引用，则从pointcutMap中查找对应的表达式
     */
    private static String resolvePointcutExpression(String value, Map<String, String> pointcutMap) {
        // 如果value包含'('，说明是完整的表达式
        if (value.contains("(")) {
            return value;
        }
        
        // 否则认为是对@Pointcut方法的引用
        String expression = pointcutMap.get(value);
        if (expression == null) {
            throw new IllegalArgumentException("找不到切点方法: " + value);
        }
        
        return expression;
    }
}