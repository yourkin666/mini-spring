package com.minispring.example;

import com.minispring.ioc.annotation.Autowired;
import com.minispring.ioc.annotation.Component;
import com.minispring.ioc.annotation.Value;

@Component
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${app.name:Mini Spring}")
    private String appName;
    
    @Value("${max.users:100}")
    private int maxUsers;
    
    public String createUser(String username) {
        if (userRepository != null) {
            String result = userRepository.save(username);
            System.out.println("UserService: Created user in " + appName + " (max: " + maxUsers + ")");
            return result;
        }
        return "Failed to create user: repository not available";
    }
    
    public String getAppName() {
        return appName;
    }
    
    public int getMaxUsers() {
        return maxUsers;
    }
    
    public UserRepository getUserRepository() {
        return userRepository;
    }
    
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        System.out.println("UserService: UserRepository injected via setter");
    }
}