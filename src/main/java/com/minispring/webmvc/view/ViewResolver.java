package com.minispring.webmvc.view;

import java.util.Locale;

/**
 * ViewResolver接口
 * 将视图名称解析为View实例的策略接口
 * 体现Spring MVC的策略模式设计 - 支持不同的视图解析策略
 */
public interface ViewResolver {
    
    /**
     * 按名称解析给定的视图
     * 
     * 注意：为了允许ViewResolver链接，如果定义的视图名称无法解析，ViewResolver应该返回null。
     * 但是，这不是必需的：某些ViewResolver将始终尝试使用给定名称构建View对象，无法返回null（而不是抛出异常时遇到错误）。
     * 
     * @param viewName 要解析的视图名称
     * @param locale 解析视图的语言环境。ViewResolver可能支持国际化的不同视图版本。
     * @return View实例，如果未找到则为null（可选，允许ViewResolver链接）
     * @throws Exception 如果解析过程出错
     */
    View resolveViewName(String viewName, Locale locale) throws Exception;
}
