package com.minispring.webmvc.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理器执行链
 * 包含处理器对象和应用于处理器的拦截器列表
 * 体现Spring MVC的责任链模式设计
 */
public class HandlerExecutionChain {
    
    private final Object handler;
    
    private HandlerInterceptor[] interceptors;
    
    private List<HandlerInterceptor> interceptorList;
    
    private int interceptorIndex = -1;
    
    /**
     * 创建新的HandlerExecutionChain
     * @param handler 处理器对象
     */
    public HandlerExecutionChain(Object handler) {
        this.handler = handler;
    }
    
    /**
     * 创建新的HandlerExecutionChain
     * @param handler 处理器对象
     * @param interceptors 拦截器数组
     */
    public HandlerExecutionChain(Object handler, HandlerInterceptor... interceptors) {
        this.handler = handler;
        this.interceptors = interceptors;
    }
    
    /**
     * 返回处理器对象
     */
    public Object getHandler() {
        return this.handler;
    }
    
    /**
     * 添加拦截器到链的末尾
     */
    public void addInterceptor(HandlerInterceptor interceptor) {
        initInterceptorList().add(interceptor);
    }
    
    /**
     * 在指定索引处添加拦截器
     */
    public void addInterceptor(int index, HandlerInterceptor interceptor) {
        initInterceptorList().add(index, interceptor);
    }
    
    /**
     * 获取配置的拦截器数组
     */
    public HandlerInterceptor[] getInterceptors() {
        if (this.interceptors == null && this.interceptorList != null) {
            this.interceptors = this.interceptorList.toArray(new HandlerInterceptor[0]);
        }
        return this.interceptors;
    }
    
    /**
     * 应用注册的拦截器的preHandle方法
     * @return 如果执行链应该继续进行下一个拦截器或处理器本身，则为true；否则，DispatcherServlet假定此拦截器已经处理了响应本身
     */
    boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HandlerInterceptor[] interceptors = getInterceptors();
        if (interceptors != null) {
            for (int i = 0; i < interceptors.length; i++) {
                HandlerInterceptor interceptor = interceptors[i];
                if (!interceptor.preHandle(request, response, this.handler)) {
                    triggerAfterCompletion(request, response, null);
                    return false;
                }
                this.interceptorIndex = i;
            }
        }
        return true;
    }
    
    /**
     * 应用注册的拦截器的postHandle方法
     */
    void applyPostHandle(HttpServletRequest request, HttpServletResponse response, Object modelAndView) throws Exception {
        HandlerInterceptor[] interceptors = getInterceptors();
        if (interceptors != null) {
            for (int i = interceptors.length - 1; i >= 0; i--) {
                HandlerInterceptor interceptor = interceptors[i];
                interceptor.postHandle(request, response, this.handler, modelAndView);
            }
        }
    }
    
    /**
     * 触发afterCompletion回调
     */
    void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, Exception ex) throws Exception {
        HandlerInterceptor[] interceptors = getInterceptors();
        if (interceptors != null) {
            for (int i = this.interceptorIndex; i >= 0; i--) {
                HandlerInterceptor interceptor = interceptors[i];
                try {
                    interceptor.afterCompletion(request, response, this.handler, ex);
                } catch (Throwable ex2) {
                    System.err.println("HandlerInterceptor.afterCompletion threw exception: " + ex2);
                }
            }
        }
    }
    
    private List<HandlerInterceptor> initInterceptorList() {
        if (this.interceptorList == null) {
            this.interceptorList = new ArrayList<>();
            if (this.interceptors != null) {
                // 如果从构造函数传入了拦截器数组，将其添加到list中
                for (HandlerInterceptor interceptor : this.interceptors) {
                    this.interceptorList.add(interceptor);
                }
            }
        }
        return this.interceptorList;
    }
    
    @Override
    public String toString() {
        Object handler = getHandler();
        if (handler == null) {
            return "HandlerExecutionChain with no handler";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("HandlerExecutionChain with handler [").append(handler).append("]");
        HandlerInterceptor[] interceptors = getInterceptors();
        if (interceptors != null && interceptors.length > 0) {
            sb.append(" and ").append(interceptors.length).append(" interceptor");
            if (interceptors.length > 1) {
                sb.append("s");
            }
        }
        return sb.toString();
    }
}
