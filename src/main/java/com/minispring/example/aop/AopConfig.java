package com.minispring.example.aop;

import com.minispring.ioc.annotation.ComponentScan;

/**
 * AOP配置类
 * 扫描包含切面和业务类的包
 */
@ComponentScan(basePackages = "com.minispring.example.aop")
public class AopConfig {
}