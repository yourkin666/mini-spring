package com.minispring.example.aop;

import com.minispring.ioc.annotation.Component;

import java.util.Random;

/**
 * 业务服务类，用于演示AOP功能
 */
@Component
public class BusinessService {
    
    private final Random random = new Random();
    
    /**
     * 普通业务方法
     */
    public String processData(String data) {
        System.out.println("  >>> [业务方法] 正在处理数据: " + data);
        
        // 模拟处理耗时
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return "处理完成: " + data.toUpperCase();
    }
    
    /**
     * 带性能监控的业务方法（会被环绕通知拦截）
     */
    public int performOperation(String operation) {
        System.out.println("  >>> [业务方法] 执行操作: " + operation);
        
        // 模拟复杂计算
        int result = 0;
        for (int i = 0; i < 1000000; i++) {
            result += i;
        }
        
        return result;
    }
    
    /**
     * 可能抛出异常的方法
     */
    public void riskyOperation(boolean shouldFail) {
        System.out.println("  >>> [业务方法] 执行风险操作，shouldFail: " + shouldFail);
        
        if (shouldFail) {
            throw new RuntimeException("模拟业务异常");
        }
        
        System.out.println("  >>> [业务方法] 风险操作执行成功");
    }
    
    /**
     * 返回值为void的方法
     */
    public void voidMethod(String message) {
        System.out.println("  >>> [业务方法] Void方法执行: " + message);
    }
}