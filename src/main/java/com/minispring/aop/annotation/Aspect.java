package com.minispring.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 切面注解，用于标识一个类为切面类
 * 模仿Spring的@Aspect注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    
    /**
     * 切面的名称，默认为类名
     */
    String value() default "";
}