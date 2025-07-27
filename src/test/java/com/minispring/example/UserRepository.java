package com.minispring.example;

import com.minispring.ioc.annotation.Component;
import com.minispring.ioc.annotation.Value;

@Component
public class UserRepository {
    
    @Value("${db.url:jdbc:h2:mem:test}")
    private String databaseUrl;
    
    public String save(String username) {
        System.out.println("UserRepository: Saving user '" + username + "' to database: " + databaseUrl);
        return "User '" + username + "' saved successfully";
    }
    
    public String getDatabaseUrl() {
        return databaseUrl;
    }
}