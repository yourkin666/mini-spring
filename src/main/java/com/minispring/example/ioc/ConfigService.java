package com.minispring.example.ioc;

import com.minispring.ioc.annotation.Component;
import com.minispring.ioc.annotation.Value;

/**
 * 配置服务 - 演示属性注入
 */
@Component
public class ConfigService {
    
    @Value("${app.name:Mini-Spring}")
    private String appName;
    
    @Value("${app.version:1.0}")
    private String appVersion;
    
    public void showConfig() {
        System.out.println("应用名称: " + appName);
        System.out.println("应用版本: " + appVersion);
    }
}"