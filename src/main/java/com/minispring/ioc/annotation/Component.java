package com.minispring.ioc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个类为Spring组件，会被自动扫描并注册为Bean
 * IoC模块核心注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    
    /**
     * Bean的名称，如果不指定则使用类名的首字母小写形式
     */
    String value() default "";
}