package com.minispring.webmvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * View接口
 * MVC View的契约
 * 实现负责呈现内容，并公开模型
 * 单个视图公开多个模型属性
 * 体现Spring MVC的策略模式设计
 */
public interface View {
    
    /**
     * 响应的内容类型的名称。用于声明性配置
     */
    String RESPONSE_STATUS_ATTRIBUTE = View.class.getName() + ".responseStatus";
    
    /**
     * 包含路径变量的Map的名称。保留用于内部框架使用
     */
    String PATH_VARIABLES = View.class.getName() + ".pathVariables";
    
    /**
     * 选定的内容类型的名称，用于选择性内容协商
     */
    String SELECTED_CONTENT_TYPE = View.class.getName() + ".selectedContentType";
    
    /**
     * 返回视图的内容类型
     * 可用于检查View是否支持请求的内容类型
     * @return 内容类型，如果确定失败则为null
     */
    default String getContentType() {
        return null;
    }
    
    /**
     * 渲染给定模型的视图
     * 第一步将是准备请求：在JSP的情况下，这将意味着将模型对象设置为请求属性
     * 第二步将是视图的实际渲染，例如通过RequestDispatcher包含JSP
     * 
     * @param model 名称String和模型对象Object的组合映射
     *              (Map可以为null，以防没有模型可用)
     * @param request 当前HTTP请求
     * @param response 构建的HTTP响应
     * @throws Exception 如果渲染失败
     */
    void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
