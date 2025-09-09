package com.minispring.webmvc.handler;

import com.minispring.webmvc.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 简化版请求映射信息
 * 只保留核心的路径和HTTP方法匹配
 * 体现Spring MVC的核心设计，避免过度复杂
 */
public class SimpleRequestMappingInfo {
    
    private final Set<String> paths;
    private final Set<RequestMethod> methods;
    
    public SimpleRequestMappingInfo(String[] paths, RequestMethod[] methods) {
        this.paths = new HashSet<>(Arrays.asList(paths));
        this.methods = new HashSet<>(Arrays.asList(methods));
    }
    
    /**
     * 检查是否匹配请求
     */
    public boolean matches(HttpServletRequest request) {
        return matchesPath(request) && matchesMethod(request);
    }
    
    private boolean matchesPath(HttpServletRequest request) {
        if (paths.isEmpty()) {
            return true;
        }
        
        String requestPath = getRequestPath(request);
        for (String pattern : paths) {
            if (pathMatches(pattern, requestPath)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean matchesMethod(HttpServletRequest request) {
        if (methods.isEmpty()) {
            return true;
        }
        
        String requestMethod = request.getMethod();
        for (RequestMethod method : methods) {
            if (method.name().equals(requestMethod)) {
                return true;
            }
        }
        return false;
    }
    
    private String getRequestPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        return path;
    }
    
    private boolean pathMatches(String pattern, String path) {
        // 精确匹配
        if (pattern.equals(path)) {
            return true;
        }
        
        // 简单的路径变量支持 {id}
        if (pattern.contains("{") && pattern.contains("}")) {
            String regex = pattern.replaceAll("\\{[^}]+\\}", "[^/]+");
            return path.matches(regex);
        }
        
        return false;
    }
    
    public Set<String> getPaths() {
        return paths;
    }
    
    public Set<RequestMethod> getMethods() {
        return methods;
    }
    
    @Override
    public String toString() {
        return "SimpleRequestMappingInfo{paths=" + paths + ", methods=" + methods + '}';
    }
}
