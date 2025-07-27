package com.minispring.ioc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动装配注解，用于标识需要自动注入的字段或方法
 * IoC模块依赖注入核心注解
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    
    /**
     * 是否必须找到对应的Bean，默认为true
     * 如果为true且找不到对应Bean，将抛出异常
     */
    boolean required() default true;
}