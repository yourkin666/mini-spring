package com.minispring.webmvc.handler;

import javax.servlet.http.HttpServletRequest;

/**
 * Handler映射接口
 * 负责将HTTP请求映射到相应的处理器
 * 体现Spring MVC的策略模式设计 - 不同的映射策略可以有不同的实现
 */
public interface HandlerMapping {
    
    /**
     * 返回给定请求的处理器和拦截器（如果有）
     * 选择基于请求URL、会话状态或实现类选择的任何因素
     * 
     * @param request 当前HTTP请求
     * @return HandlerExecutionChain实例（包含处理器对象和任何拦截器）
     *         如果没有找到匹配的处理器，则为null
     * @throws Exception 如果发生错误
     */
    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
