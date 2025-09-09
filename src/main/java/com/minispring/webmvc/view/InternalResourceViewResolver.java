package com.minispring.webmvc.view;

import com.minispring.ioc.annotation.Component;

import java.util.Locale;

/**
 * InternalResourceViewResolver类
 * 将视图名称解析为InternalResourceView实例
 * 通常用于JSP视图的解析
 * 体现Spring MVC的视图解析器策略模式
 */
@Component
public class InternalResourceViewResolver implements ViewResolver {
    
    private String prefix = "";
    private String suffix = "";
    private String contentType;
    private boolean exposePathVariables = true;
    private boolean exposeContextBeansAsAttributes = false;
    private String[] exposedContextBeanNames;
    private String requestContextAttribute;
    
    /**
     * 设置视图名称的前缀
     * 例如: "/WEB-INF/jsp/"
     */
    public void setPrefix(String prefix) {
        this.prefix = (prefix != null ? prefix : "");
    }
    
    /**
     * 返回视图名称的前缀
     */
    protected String getPrefix() {
        return this.prefix;
    }
    
    /**
     * 设置视图名称的后缀
     * 例如: ".jsp"
     */
    public void setSuffix(String suffix) {
        this.suffix = (suffix != null ? suffix : "");
    }
    
    /**
     * 返回视图名称的后缀
     */
    protected String getSuffix() {
        return this.suffix;
    }
    
    /**
     * 设置所有视图的内容类型
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    /**
     * 返回所有视图的内容类型
     */
    protected String getContentType() {
        return this.contentType;
    }
    
    /**
     * 设置是否公开路径变量
     */
    public void setExposePathVariables(boolean exposePathVariables) {
        this.exposePathVariables = exposePathVariables;
    }
    
    /**
     * 返回是否公开路径变量
     */
    protected boolean getExposePathVariables() {
        return this.exposePathVariables;
    }
    
    /**
     * 设置是否将上下文中的所有Spring Bean公开为请求属性
     */
    public void setExposeContextBeansAsAttributes(boolean exposeContextBeansAsAttributes) {
        this.exposeContextBeansAsAttributes = exposeContextBeansAsAttributes;
    }
    
    /**
     * 设置要公开为请求属性的上下文Bean名称
     */
    public void setExposedContextBeanNames(String... exposedContextBeanNames) {
        this.exposedContextBeanNames = exposedContextBeanNames;
    }
    
    /**
     * 设置RequestContext实例在请求中的属性名称
     */
    public void setRequestContextAttribute(String requestContextAttribute) {
        this.requestContextAttribute = requestContextAttribute;
    }
    
    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        // 检查是否可以处理该视图名称
        if (!canHandle(viewName, locale)) {
            return null;
        }
        
        // 检查是否是重定向视图
        if (viewName.startsWith("redirect:")) {
            String redirectUrl = viewName.substring(9);
            return new RedirectView(redirectUrl);
        }
        
        // 检查是否是转发视图
        if (viewName.startsWith("forward:")) {
            String forwardUrl = viewName.substring(8);
            return new InternalResourceView(forwardUrl);
        }
        
        // 创建标准的InternalResourceView
        return buildView(viewName);
    }
    
    /**
     * 指示此ViewResolver是否可以处理给定的视图名称
     * 默认实现返回true
     */
    protected boolean canHandle(String viewName, Locale locale) {
        return true;
    }
    
    /**
     * 构建View实例
     */
    protected View buildView(String viewName) throws Exception {
        String url = getPrefix() + viewName + getSuffix();
        InternalResourceView view = new InternalResourceView(url);
        
        String contentType = getContentType();
        if (contentType != null) {
            view.setContentType(contentType);
        }
        
        view.setExposePathVariables(getExposePathVariables());
        
        return view;
    }
    
    /**
     * 简化的重定向视图实现
     */
    private static class RedirectView implements View {
        private final String url;
        
        public RedirectView(String url) {
            this.url = url;
        }
        
        @Override
        public String getContentType() {
            return null;
        }
        
        @Override
        public void render(java.util.Map<String, Object> model, 
                         javax.servlet.http.HttpServletRequest request, 
                         javax.servlet.http.HttpServletResponse response) throws Exception {
            
            String targetUrl = createTargetUrl(model, request);
            response.sendRedirect(response.encodeRedirectURL(targetUrl));
        }
        
        private String createTargetUrl(java.util.Map<String, Object> model, 
                                     javax.servlet.http.HttpServletRequest request) {
            
            String targetUrl = this.url;
            
            // 简化实现：如果是相对URL，添加上下文路径
            if (!targetUrl.startsWith("http") && !targetUrl.startsWith("/")) {
                String contextPath = request.getContextPath();
                targetUrl = contextPath + "/" + targetUrl;
            }
            
            return targetUrl;
        }
        
        @Override
        public String toString() {
            return "RedirectView: " + this.url;
        }
    }
}
