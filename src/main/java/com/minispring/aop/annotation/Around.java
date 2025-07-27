package com.minispring.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 环绕通知注解
 * 在目标方法执行前后都可以执行自定义逻辑，是最强大的通知类型
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Around {
    
    /**
     * 切点表达式或切点方法名
     */
    String value();
}