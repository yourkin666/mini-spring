package com.minispring.webmvc.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handler适配器接口
 * 定义了MVC框架SPI，允许核心MVC工作流的参数化
 * 体现Spring MVC的适配器模式设计 - 使得不同类型的处理器都能被统一处理
 */
public interface HandlerAdapter {
    
    /**
     * 给定一个处理器实例，返回此HandlerAdapter是否可以支持它
     * 典型的HandlerAdapter将基于处理器类型做出决定
     * HandlerAdapter通常只支持一个处理器类型
     * 
     * 典型的实现：
     * return (handler instanceof MyHandler);
     * 
     * @param handler 要检查的处理器对象
     * @return 此适配器是否可以适配给定的处理器
     */
    boolean supports(Object handler);
    
    /**
     * 使用给定的处理器来处理此请求
     * 所需的工作流程可能会有很大差异
     * 
     * @param request 当前HTTP请求
     * @param response 当前HTTP响应
     * @param handler 要使用的处理器。此对象必须先前已由此接口的supports方法传递，该方法必须已返回true
     * @return ModelAndView对象，包含模型和视图名称，或者在直接处理的情况下为null
     * @throws Exception 如果发生错误
     */
    Object handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;
    
    /**
     * 与HttpServlet的getLastModified方法相同的契约
     * 如果处理器类不支持，可以简单地返回-1
     * 
     * @param request 当前HTTP请求
     * @param handler 要使用的处理器
     * @return 给定处理器的lastModified值
     */
    long getLastModified(HttpServletRequest request, Object handler);
}
