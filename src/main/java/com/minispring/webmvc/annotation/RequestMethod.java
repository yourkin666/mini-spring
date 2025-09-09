package com.minispring.webmvc.annotation;

/**
 * HTTP请求方法枚举
 * 在RequestMapping注解中用于指定HTTP方法约束
 */
public enum RequestMethod {
    
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
}
