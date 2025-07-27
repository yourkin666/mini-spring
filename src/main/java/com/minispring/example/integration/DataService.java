package com.minispring.example.integration;

import com.minispring.ioc.annotation.Component;

/**
 * 数据服务 - 被注入的依赖
 */
@Component
public class DataService {
    
    public void saveData(String data) {
        System.out.println("DataService保存数据: " + data);
    }
}