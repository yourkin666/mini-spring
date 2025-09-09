package com.minispring.webmvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @RequestMapping注解
 * 用于将Web请求映射到请求处理类中的方法
 * 体现Spring MVC的声明式路由映射设计
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    
    /**
     * 主要映射表达式
     * 在类型级别：所有方法级映射继承此主要映射，将其缩小为特定的处理器方法
     * 在方法级别：如果指定了多个URL，则它们被视为等效的替代方案
     */
    String[] value() default {};
    
    /**
     * 路径映射URI（例如"/myPath.do"）
     * 也支持Ant样式的路径模式（例如"/myPath/*.do"）
     * 在方法级别，相对于类型级别表达的主要映射
     * @return 路径映射URI
     */
    String[] path() default {};
    
    /**
     * 映射的HTTP请求方法，将映射范围缩小：GET、POST、HEAD、OPTIONS、PUT、PATCH、DELETE、TRACE
     * 在类型级别支持：所有方法级映射继承此HTTP方法约束
     */
    RequestMethod[] method() default {};
    
    /**
     * 映射请求的参数，将映射范围缩小为那些具有相等参数值的请求
     * 
     * 支持的格式：
     * "myParam=myValue" - 参数值必须等于"myValue"
     * "myParam!=myValue" - 参数值不得等于"myValue"  
     * "myParam" - 参数必须存在
     * "!myParam" - 参数不得存在
     */
    String[] params() default {};
    
    /**
     * 映射请求的HTTP请求头，将映射范围缩小为那些具有相等头值的请求
     */
    String[] headers() default {};
    
    /**
     * 映射请求的可消费媒体类型，将映射范围缩小，例如"text/plain"，"application/json"
     */
    String[] consumes() default {};
    
    /**
     * 映射请求的可产生媒体类型，将映射范围缩小，例如"text/plain"，"application/json"
     */
    String[] produces() default {};
}
