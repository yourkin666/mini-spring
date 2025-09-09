package com.minispring.webmvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ResponseBody注解
 * 指示方法返回值应绑定到Web响应正文
 * 支持的方法返回类型由使用HttpMessageConverter实例解析
 * 体现Spring MVC的内容协商和序列化设计
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {
    // 标记注解，无需属性
}
