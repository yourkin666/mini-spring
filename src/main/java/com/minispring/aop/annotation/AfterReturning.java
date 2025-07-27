package com.minispring.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 后置返回通知注解
 * 在目标方法正常执行完成并返回后执行通知方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterReturning {
    
    /**
     * 切点表达式或切点方法名
     */
    String value();
    
    /**
     * 用于接收目标方法返回值的参数名
     */
    String returning() default "";
}