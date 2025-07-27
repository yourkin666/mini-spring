package com.minispring.ioc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 属性值注入注解，支持从配置文件中读取值
 * IoC模块配置注入核心注解
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {
    
    /**
     * 属性表达式，支持占位符格式：${property.name:defaultValue}
     */
    String value();
}