package com.minispring.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异常后通知注解
 * 在目标方法抛出异常后执行通知方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterThrowing {
    
    /**
     * 切点表达式或切点方法名
     */
    String value();
    
    /**
     * 用于接收异常的参数名
     */
    String throwing() default "";
}