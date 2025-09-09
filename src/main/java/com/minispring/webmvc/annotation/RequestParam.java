package com.minispring.webmvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @RequestParam注解
 * 指示方法参数应绑定到Web请求参数
 * 体现Spring MVC的声明式参数绑定设计
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    
    /**
     * 要绑定到的请求参数名称
     */
    String value() default "";
    
    /**
     * 要绑定到的请求参数名称
     * @since 4.2
     */
    String name() default "";
    
    /**
     * 参数是否必需
     * 
     * 默认为true，如果请求中缺少参数，则导致抛出异常
     * 如果您希望在请求中不存在参数时使用null值，请将此项切换为false
     * 
     * 或者，提供一个defaultValue，它隐含地将此标志设置为false
     */
    boolean required() default true;
    
    /**
     * 作为后备使用的默认值
     * 
     * 提供默认值隐含地将required设置为false
     */
    String defaultValue() default "\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n";
}
