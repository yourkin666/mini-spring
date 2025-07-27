package com.minispring.example.integration;

import com.minispring.ioc.annotation.Autowired;
import com.minispring.ioc.annotation.Component;

/**
 * 业务服务 - 同时演示IoC和AOP
 */
@Component
public class BusinessService {
    
    @Autowired
    private DataService dataService;
    
    public void performBusiness(String operation) {
        System.out.println("BusinessService执行业务: " + operation);
        dataService.saveData(operation + "的数据");
    }
    
    public void performRiskyOperation() {
        System.out.println("BusinessService执行风险操作");
        throw new RuntimeException("模拟业务异常");
    }
    
    public String calculateResult(int a, int b) {
        System.out.println("BusinessService计算: " + a + " + " + b);
        return String.valueOf(a + b);
    }
}