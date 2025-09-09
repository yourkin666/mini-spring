package com.minispring.webmvc.handler;

import com.minispring.webmvc.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 请求映射信息封装类
 * 包含@RequestMapping注解的所有映射条件
 * 体现Spring MVC的条件匹配设计理念
 */
public class RequestMappingInfo {
    
    private final Set<String> paths;
    private final Set<RequestMethod> methods;
    private final Set<String> params;
    private final Set<String> headers;
    private final Set<String> consumes;
    private final Set<String> produces;
    
    private RequestMappingInfo(Builder builder) {
        this.paths = builder.paths;
        this.methods = builder.methods;
        this.params = builder.params;
        this.headers = builder.headers;
        this.consumes = builder.consumes;
        this.produces = builder.produces;
    }
    
    /**
     * 检查此映射信息是否匹配给定的HTTP请求
     */
    public boolean matches(HttpServletRequest request) {
        // 1. 检查路径匹配
        if (!matchesPath(request)) {
            return false;
        }
        
        // 2. 检查HTTP方法匹配
        if (!matchesMethod(request)) {
            return false;
        }
        
        // 3. 检查参数匹配
        if (!matchesParams(request)) {
            return false;
        }
        
        // 4. 检查请求头匹配
        if (!matchesHeaders(request)) {
            return false;
        }
        
        // 5. 检查Content-Type匹配
        if (!matchesConsumes(request)) {
            return false;
        }
        
        // 6. 检查Accept匹配
        if (!matchesProduces(request)) {
            return false;
        }
        
        return true;
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
    
    private boolean matchesParams(HttpServletRequest request) {
        if (params.isEmpty()) {
            return true;
        }
        
        for (String param : params) {
            if (!matchesParam(param, request)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean matchesHeaders(HttpServletRequest request) {
        if (headers.isEmpty()) {
            return true;
        }
        
        for (String header : headers) {
            if (!matchesHeader(header, request)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean matchesConsumes(HttpServletRequest request) {
        if (consumes.isEmpty()) {
            return true;
        }
        
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        
        for (String mediaType : consumes) {
            if (contentType.contains(mediaType)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean matchesProduces(HttpServletRequest request) {
        if (produces.isEmpty()) {
            return true;
        }
        
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader == null) {
            return true; // 如果没有Accept头，认为匹配
        }
        
        for (String mediaType : produces) {
            if (acceptHeader.contains(mediaType) || acceptHeader.contains("*/*")) {
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
        // 简化的路径匹配实现
        if (pattern.equals(path)) {
            return true;
        }
        
        // 支持简单的通配符 *
        if (pattern.contains("*")) {
            String regex = pattern.replaceAll("\\*", ".*");
            return path.matches(regex);
        }
        
        // 支持路径变量 {id}
        if (pattern.contains("{") && pattern.contains("}")) {
            String regex = pattern.replaceAll("\\{[^}]+\\}", "[^/]+");
            return path.matches(regex);
        }
        
        return false;
    }
    
    private boolean matchesParam(String paramCondition, HttpServletRequest request) {
        if (paramCondition.startsWith("!")) {
            // 参数不存在条件
            String paramName = paramCondition.substring(1);
            return request.getParameter(paramName) == null;
        } else if (paramCondition.contains("=")) {
            // 参数值匹配条件
            String[] parts = paramCondition.split("=", 2);
            String paramName = parts[0];
            String expectedValue = parts[1];
            String actualValue = request.getParameter(paramName);
            return expectedValue.equals(actualValue);
        } else if (paramCondition.contains("!=")) {
            // 参数值不匹配条件
            String[] parts = paramCondition.split("!=", 2);
            String paramName = parts[0];
            String unexpectedValue = parts[1];
            String actualValue = request.getParameter(paramName);
            return !unexpectedValue.equals(actualValue);
        } else {
            // 参数存在条件
            return request.getParameter(paramCondition) != null;
        }
    }
    
    private boolean matchesHeader(String headerCondition, HttpServletRequest request) {
        // 类似于参数匹配的逻辑
        if (headerCondition.startsWith("!")) {
            String headerName = headerCondition.substring(1);
            return request.getHeader(headerName) == null;
        } else if (headerCondition.contains("=")) {
            String[] parts = headerCondition.split("=", 2);
            String headerName = parts[0];
            String expectedValue = parts[1];
            String actualValue = request.getHeader(headerName);
            return expectedValue.equals(actualValue);
        } else if (headerCondition.contains("!=")) {
            String[] parts = headerCondition.split("!=", 2);
            String headerName = parts[0];
            String unexpectedValue = parts[1];
            String actualValue = request.getHeader(headerName);
            return !unexpectedValue.equals(actualValue);
        } else {
            return request.getHeader(headerCondition) != null;
        }
    }
    
    // Getters
    public Set<String> getPaths() {
        return paths;
    }
    
    public Set<RequestMethod> getMethods() {
        return methods;
    }
    
    /**
     * 创建RequestMappingInfo的Builder
     */
    public static Builder paths(String... paths) {
        return new Builder().paths(paths);
    }
    
    public static class Builder {
        private Set<String> paths = new HashSet<>();
        private Set<RequestMethod> methods = new HashSet<>();
        private Set<String> params = new HashSet<>();
        private Set<String> headers = new HashSet<>();
        private Set<String> consumes = new HashSet<>();
        private Set<String> produces = new HashSet<>();
        
        public Builder paths(String... paths) {
            this.paths.addAll(Arrays.asList(paths));
            return this;
        }
        
        public Builder methods(RequestMethod... methods) {
            this.methods.addAll(Arrays.asList(methods));
            return this;
        }
        
        public Builder params(String... params) {
            this.params.addAll(Arrays.asList(params));
            return this;
        }
        
        public Builder headers(String... headers) {
            this.headers.addAll(Arrays.asList(headers));
            return this;
        }
        
        public Builder consumes(String... consumes) {
            this.consumes.addAll(Arrays.asList(consumes));
            return this;
        }
        
        public Builder produces(String... produces) {
            this.produces.addAll(Arrays.asList(produces));
            return this;
        }
        
        public RequestMappingInfo build() {
            return new RequestMappingInfo(this);
        }
    }
    
    @Override
    public String toString() {
        return "RequestMappingInfo{" +
                "paths=" + paths +
                ", methods=" + methods +
                ", params=" + params +
                ", headers=" + headers +
                ", consumes=" + consumes +
                ", produces=" + produces +
                '}';
    }
}
