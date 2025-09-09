package com.minispring.webmvc.annotation;

import com.minispring.ioc.annotation.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Controller注解
 * 指示被注解的类是"控制器"（例如Web控制器）
 * 此注解充当专门化的@Component，允许通过类路径扫描自动检测实现类
 * 体现Spring MVC的声明式编程设计理念
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Controller {
    
    /**
     * 此控制器组件的值可能指示一个建议的逻辑组件名称
     * 在自动检测的组件中要转换为Spring bean
     * @return 建议的组件名称，如果有的话（或空字符串，否则）
     */
    String value() default "";
}
