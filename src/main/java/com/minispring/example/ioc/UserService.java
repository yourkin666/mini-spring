package com.minispring.example.ioc;

import com.minispring.ioc.annotation.Autowired;
import com.minispring.ioc.annotation.Component;

/**
 * 用户服务 - 演示依赖注入
 */
@Component
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public void performAction() {
        System.out.println("UserService执行业务逻辑");
        userRepository.findUser();
    }
}