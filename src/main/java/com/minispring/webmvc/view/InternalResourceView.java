package com.minispring.webmvc.view;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * InternalResourceView类
 * 用于JSP和其他内部资源的视图实现
 * 体现Spring MVC的视图实现策略
 */
public class InternalResourceView implements View {
    
    private String url;
    private boolean exposePathVariables = true;
    private boolean exposeContextBeansAsAttributes = false;
    private String contentType = "text/html;charset=UTF-8";
    
    /**
     * 创建新的InternalResourceView
     */
    public InternalResourceView() {
    }
    
    /**
     * 创建新的InternalResourceView
     * @param url 转发到的URL
     */
    public InternalResourceView(String url) {
        this.url = url;
    }
    
    /**
     * 设置此视图包装的资源的URL
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /**
     * 返回此视图包装的资源的URL
     */
    public String getUrl() {
        return this.url;
    }
    
    /**
     * 设置是否公开路径变量
     */
    public void setExposePathVariables(boolean exposePathVariables) {
        this.exposePathVariables = exposePathVariables;
    }
    
    /**
     * 设置内容类型
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    @Override
    public String getContentType() {
        return this.contentType;
    }
    
    /**
     * 检查资源是否存在
     */
    public boolean checkResource(HttpServletRequest request) throws Exception {
        // 简化实现：假设资源总是存在
        return true;
    }
    
    @Override
    public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // 1. 确定要公开的对象
        Map<String, Object> mergedModel = createMergedOutputModel(model, request, response);
        
        // 2. 准备响应
        prepareResponse(request, response);
        
        // 3. 将模型对象公开为请求属性
        exposeModelAsRequestAttributes(mergedModel, request);
        
        // 4. 确定要包含的资源的路径
        String dispatcherPath = prepareForRendering(request, response);
        
        // 5. 获取目标资源的RequestDispatcher（通常是JSP）
        RequestDispatcher rd = getRequestDispatcher(request, dispatcherPath);
        if (rd == null) {
            throw new Exception("Could not get RequestDispatcher for [" + dispatcherPath + 
                    "]: Check that the corresponding file exists within your web application archive!");
        }
        
        // 6. 如果已经包含或响应已经提交，则执行包含，否则转发
        if (useInclude(request, response)) {
            response.setContentType(getContentType());
            rd.include(request, response);
        } else {
            // 注意：转发应该设置页面内容类型，不是在这里
            rd.forward(request, response);
        }
    }
    
    /**
     * 创建合并的输出模型
     */
    protected Map<String, Object> createMergedOutputModel(Map<String, Object> model, 
                                                         HttpServletRequest request, 
                                                         HttpServletResponse response) {
        
        Map<String, Object> pathVars = (Map<String, Object>) request.getAttribute(View.PATH_VARIABLES);
        if (pathVars != null) {
            pathVars.putAll(model);
            return pathVars;
        } else {
            return model;
        }
    }
    
    /**
     * 准备给定的响应以进行渲染
     */
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        if (generatesDownloadContent()) {
            response.setHeader("Pragma", "private");
            response.setHeader("Cache-Control", "private, must-revalidate");
        }
    }
    
    /**
     * 确定此视图是否生成下载内容
     * 默认实现返回false
     */
    protected boolean generatesDownloadContent() {
        return false;
    }
    
    /**
     * 将模型对象公开为请求属性
     */
    protected void exposeModelAsRequestAttributes(Map<String, Object> model, HttpServletRequest request) throws Exception {
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            String modelName = entry.getKey();
            Object modelValue = entry.getValue();
            if (modelValue != null) {
                request.setAttribute(modelName, modelValue);
            } else {
                request.removeAttribute(modelName);
            }
        }
    }
    
    /**
     * 准备进行渲染，并确定RequestDispatcher路径
     */
    protected String prepareForRendering(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = getUrl();
        if (path == null) {
            throw new IllegalStateException("InternalResourceView is not configured: URL is required");
        }
        return path;
    }
    
    /**
     * 获取指定资源的RequestDispatcher
     */
    protected RequestDispatcher getRequestDispatcher(HttpServletRequest request, String path) {
        return request.getRequestDispatcher(path);
    }
    
    /**
     * 确定是否使用RequestDispatcher的include方法而不是forward方法
     */
    protected boolean useInclude(HttpServletRequest request, HttpServletResponse response) {
        return (isIncludeRequest(request) || response.isCommitted());
    }
    
    /**
     * 确定给定的请求是否是包含请求
     */
    protected boolean isIncludeRequest(HttpServletRequest request) {
        return (request.getAttribute("javax.servlet.include.request_uri") != null);
    }
    
    @Override
    public String toString() {
        return "InternalResourceView: " + getUrl();
    }
}
