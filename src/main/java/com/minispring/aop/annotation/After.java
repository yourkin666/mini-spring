package com.minispring.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 后置通知注解
 * 在目标方法执行后执行通知方法（无论是否抛出异常）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface After {
    
    /**
     * 切点表达式或切点方法名
     */
    String value();
}