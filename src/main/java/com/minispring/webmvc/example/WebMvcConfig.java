package com.minispring.webmvc.example;

import com.minispring.ioc.annotation.ComponentScan;

/**
 * Web MVC配置类
 * 启用组件扫描，自动发现和注册Spring MVC组件
 * 体现Spring的声明式配置设计理念
 */
@ComponentScan(basePackages = {
    "com.minispring.webmvc.handler",
    "com.minispring.webmvc.view", 
    "com.minispring.webmvc.context",
    "com.minispring.webmvc.example"
})
public class WebMvcConfig {
    
    // 这个类主要用于配置组件扫描
    // 实际的Bean配置在WebMvcConfigurationSupport中完成
}
