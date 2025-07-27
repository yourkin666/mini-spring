package com.minispring.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 切点注解，用于定义切点表达式
 * 模仿Spring的@Pointcut注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Pointcut {
    
    /**
     * 切点表达式
     * 支持execution表达式，如：execution(* com.minispring.example..*(..))
     */
    String value();
}