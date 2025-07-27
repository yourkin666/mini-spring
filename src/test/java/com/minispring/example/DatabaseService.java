package com.minispring.example;

import com.minispring.ioc.annotation.Component;
import com.minispring.ioc.annotation.PostConstruct;
import com.minispring.ioc.annotation.PreDestroy;
import com.minispring.ioc.annotation.Value;
import com.minispring.ioc.beans.DisposableBean;
import com.minispring.ioc.beans.InitializingBean;

/**
 * 演示Bean生命周期的数据库服务类
 */
@Component
public class DatabaseService implements InitializingBean, DisposableBean {
    
    @Value("${db.connection.pool.size:10}")
    private int connectionPoolSize;
    
    @Value("${db.timeout:30}")
    private int timeout;
    
    private boolean connected = false;
    
    @PostConstruct
    public void initConnection() {
        System.out.println("DatabaseService: @PostConstruct - Initializing database connection...");
        System.out.println("DatabaseService: Connection pool size: " + connectionPoolSize + ", timeout: " + timeout);
        connected = true;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("DatabaseService: InitializingBean.afterPropertiesSet() - Additional setup completed");
        // 这里可以执行额外的初始化逻辑
        validateConfiguration();
    }
    
    @PreDestroy
    public void cleanup() {
        System.out.println("DatabaseService: @PreDestroy - Cleaning up resources...");
        // 关闭连接池等清理工作
        connected = false;
    }
    
    @Override
    public void destroy() throws Exception {
        System.out.println("DatabaseService: DisposableBean.destroy() - Final cleanup");
        // 最终的清理工作
    }
    
    private void validateConfiguration() {
        if (connectionPoolSize <= 0) {
            throw new IllegalStateException("Connection pool size must be positive");
        }
        if (timeout <= 0) {
            throw new IllegalStateException("Timeout must be positive");
        }
        System.out.println("DatabaseService: Configuration validated successfully");
    }
    
    public void executeQuery(String sql) {
        if (!connected) {
            throw new IllegalStateException("Database not connected");
        }
        System.out.println("DatabaseService: Executing query: " + sql);
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }
    
    public int getTimeout() {
        return timeout;
    }
}