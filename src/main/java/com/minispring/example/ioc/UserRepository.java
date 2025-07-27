package com.minispring.example.ioc;

import com.minispring.ioc.annotation.Component;

/**
 * 用户数据访问 - 被注入的依赖
 */
@Component
public class UserRepository {
    
    public void findUser() {
        System.out.println("UserRepository查询用户数据");
    }
}