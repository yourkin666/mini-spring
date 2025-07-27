package com.minispring.example.ioc;

import com.minispring.ioc.annotation.Component;
import com.minispring.ioc.annotation.PostConstruct;
import com.minispring.ioc.annotation.PreDestroy;
import com.minispring.ioc.beans.DisposableBean;
import com.minispring.ioc.beans.InitializingBean;

/**
 * 生命周期Bean - 演示Bean生命周期管理
 */
@Component
public class LifecycleBean implements InitializingBean, DisposableBean {
    
    private boolean initialized = false;
    
    @PostConstruct
    public void postConstruct() {
        System.out.println("LifecycleBean: @PostConstruct方法被调用");
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("LifecycleBean: InitializingBean.afterPropertiesSet()被调用");
        this.initialized = true;
    }
    
    public void doSomething() {
        if (initialized) {
            System.out.println("LifecycleBean: 执行业务逻辑（已完成初始化）");
        } else {
            System.out.println("LifecycleBean: Bean尚未初始化完成");
        }
    }
    
    @PreDestroy
    public void preDestroy() {
        System.out.println("LifecycleBean: @PreDestroy方法被调用");
    }
    
    @Override
    public void destroy() throws Exception {
        System.out.println("LifecycleBean: DisposableBean.destroy()被调用");
        this.initialized = false;
    }
}