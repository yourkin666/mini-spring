package com.minispring.ioc.beans;

/**
 * 可销毁Bean接口 - Spring生命周期回调
 * 在容器关闭时调用
 */
public interface DisposableBean {
    
    /**
     * Bean销毁前的清理回调
     * 在@PreDestroy注解方法执行前调用
     * @throws Exception 销毁异常
     */
    void destroy() throws Exception;
}