package com.minispring.example;

import com.minispring.ioc.annotation.ComponentScan;

@ComponentScan(basePackages = "com.minispring.example")
public class AppConfig {
    // 配置类，使用@ComponentScan指定要扫描的包
}