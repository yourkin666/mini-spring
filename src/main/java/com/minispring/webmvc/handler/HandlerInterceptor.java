package com.minispring.webmvc.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handler拦截器接口
 * 允许自定义处理器执行链的工作流
 * 体现Spring MVC的责任链模式和模板方法模式设计
 */
public interface HandlerInterceptor {
    
    /**
     * 拦截处理器的执行
     * 在HandlerMapping确定适当的处理器对象之后，但在HandlerAdapter调用处理器之前调用
     * 
     * @param request 当前HTTP请求
     * @param response 当前HTTP响应
     * @param handler 选择要执行的处理器，用于类型和/或实例评估
     * @return 如果执行链应该继续进行下一个拦截器或处理器本身，则为true；否则，DispatcherServlet假定此拦截器已经处理了响应本身
     * @throws Exception 如果发生错误
     */
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }
    
    /**
     * 拦截处理器的执行
     * 在HandlerAdapter实际调用处理器之后但在DispatcherServlet呈现视图之前调用
     * 可以通过给定的ModelAndView公开其他模型对象到视图
     * 
     * @param request 当前HTTP请求
     * @param response 当前HTTP响应
     * @param handler 启动异步执行的处理器（或HandlerMethod）
     * @param modelAndView 处理器返回的ModelAndView（也可以是null）
     * @throws Exception 如果发生错误
     */
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, Object modelAndView) throws Exception {
    }
    
    /**
     * 请求完成后的回调，即呈现视图后
     * 将在处理器执行的任何结果上调用，从而允许适当的资源清理
     * 注意：只有在此拦截器的preHandle方法已经成功完成并返回true时才会被调用！
     * 
     * @param request 当前HTTP请求
     * @param response 当前HTTP响应
     * @param handler 启动异步执行的处理器（或HandlerMethod）
     * @param ex 处理器执行时抛出的异常（如果有的话）
     * @throws Exception 如果发生错误
     */
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
