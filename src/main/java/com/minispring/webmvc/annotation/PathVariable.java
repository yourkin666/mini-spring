package com.minispring.webmvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @PathVariable注解
 * 指示方法参数应绑定到URI模板变量
 * 支持在@RequestMapping注解的URL中使用的URI模板
 * 体现Spring MVC的RESTful URL设计理念
 * 
 * 例如：
 * @RequestMapping("/users/{id}")
 * public String showUser(@PathVariable("id") long id) { ... }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {
    
    /**
     * 要绑定到的路径变量的名称
     */
    String value() default "";
    
    /**
     * 要绑定到的路径变量的名称
     * @since 4.3.3
     */
    String name() default "";
    
    /**
     * 路径变量是否必需
     * 
     * 默认为true，如果路径中没有匹配的路径变量，则导致抛出异常
     * 如果您希望在路径变量不存在时使用null值，请将此项切换为false
     * 
     * @since 4.3.3
     */
    boolean required() default true;
}
