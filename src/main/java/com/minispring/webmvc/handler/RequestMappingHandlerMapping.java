package com.minispring.webmvc.handler;

import com.minispring.ioc.annotation.Component;
import com.minispring.ioc.context.ApplicationContext;
import com.minispring.webmvc.annotation.Controller;
import com.minispring.webmvc.annotation.RequestMapping;
import com.minispring.webmvc.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于@RequestMapping注解的处理器映射实现
 * 负责将HTTP请求映射到标注了@RequestMapping的控制器方法
 * 体现Spring MVC的注解驱动和约定优于配置设计理念
 */
@Component
public class RequestMappingHandlerMapping implements HandlerMapping {
    
    /** 存储路径模式到HandlerMethod的映射 */
    private final Map<RequestMappingInfo, HandlerMethod> handlerMethods = new HashMap<>();
    
    /** ApplicationContext引用 */
    private ApplicationContext applicationContext;
    
    /**
     * 设置ApplicationContext并初始化处理器方法
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        initHandlerMethods();
    }
    
    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        HandlerMethod handlerMethod = getHandlerInternal(request);
        if (handlerMethod == null) {
            return null;
        }
        
        // 创建HandlerExecutionChain
        HandlerExecutionChain executionChain = new HandlerExecutionChain(handlerMethod);
        
        // 这里可以添加拦截器
        // executionChain.addInterceptor(someInterceptor);
        
        return executionChain;
    }
    
    /**
     * 查找给定请求的处理器方法
     */
    protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
        
        try {
            HandlerMethod handlerMethod = lookupHandlerMethod(lookupPath, request);
            if (handlerMethod != null) {
                return handlerMethod;
            }
        } catch (Exception ex) {
            System.err.println("查找处理器方法失败: " + ex.getMessage());
        }
        
        return null;
    }
    
    /**
     * 查找处理器方法
     */
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        List<Match> matches = new ArrayList<>();
        
        // 查找所有匹配的RequestMappingInfo
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : this.handlerMethods.entrySet()) {
            RequestMappingInfo info = entry.getKey();
            if (info.matches(request)) {
                matches.add(new Match(info, entry.getValue()));
            }
        }
        
        if (!matches.isEmpty()) {
            // 简化实现：返回第一个匹配的处理器
            // 实际Spring会进行更复杂的匹配度排序
            Match bestMatch = matches.get(0);
            return bestMatch.handlerMethod;
        }
        
        return null;
    }
    
    /**
     * 初始化处理器方法
     * 扫描所有@Controller类中的@RequestMapping方法
     */
    protected void initHandlerMethods() {
        if (this.applicationContext == null) {
            return;
        }
        
        String[] beanNames = this.applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            try {
                Object bean = this.applicationContext.getBean(beanName);
                Class<?> beanClass = bean.getClass();
                
                // 检查是否是控制器类
                if (isHandler(beanClass)) {
                    detectHandlerMethods(beanName, bean);
                }
            } catch (Exception e) {
                System.err.println("处理控制器 " + beanName + " 时发生错误: " + e.getMessage());
            }
        }
        
        System.out.println("RequestMappingHandlerMapping: 注册了 " + this.handlerMethods.size() + " 个处理器方法");
    }
    
    /**
     * 检查给定的类型是否是处理器
     */
    protected boolean isHandler(Class<?> beanType) {
        return (beanType.isAnnotationPresent(Controller.class) ||
                beanType.isAnnotationPresent(Component.class) && hasRequestMappingMethods(beanType));
    }
    
    /**
     * 检查类是否有@RequestMapping方法
     */
    private boolean hasRequestMappingMethods(Class<?> beanType) {
        Method[] methods = beanType.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检测处理器方法
     */
    protected void detectHandlerMethods(String beanName, Object handler) {
        Class<?> handlerType = handler.getClass();
        
        // 获取类级别的@RequestMapping
        RequestMapping typeMapping = handlerType.getAnnotation(RequestMapping.class);
        
        Method[] methods = handlerType.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
                
                // 合并类级别和方法级别的映射信息
                RequestMappingInfo info = createRequestMappingInfo(typeMapping, methodMapping);
                
                HandlerMethod handlerMethod = new HandlerMethod(handler, method);
                registerHandlerMethod(info, handlerMethod);
                
                System.out.println("映射 \"" + info + "\" 到 " + handlerMethod.getShortLogMessage());
            }
        }
    }
    
    /**
     * 创建RequestMappingInfo
     */
    protected RequestMappingInfo createRequestMappingInfo(RequestMapping typeMapping, RequestMapping methodMapping) {
        RequestMappingInfo.Builder builder = RequestMappingInfo.paths();
        
        // 处理路径
        List<String> paths = new ArrayList<>();
        if (typeMapping != null) {
            addPaths(paths, typeMapping.value());
            addPaths(paths, typeMapping.path());
        }
        
        List<String> methodPaths = new ArrayList<>();
        addPaths(methodPaths, methodMapping.value());
        addPaths(methodPaths, methodMapping.path());
        
        // 合并类级别和方法级别的路径
        if (paths.isEmpty()) {
            builder.paths(methodPaths.toArray(new String[0]));
        } else if (methodPaths.isEmpty()) {
            builder.paths(paths.toArray(new String[0]));
        } else {
            List<String> combinedPaths = new ArrayList<>();
            for (String typePath : paths) {
                for (String methodPath : methodPaths) {
                    combinedPaths.add(combinePath(typePath, methodPath));
                }
            }
            builder.paths(combinedPaths.toArray(new String[0]));
        }
        
        // 处理HTTP方法
        RequestMethod[] methods = methodMapping.method();
        if (methods.length > 0) {
            builder.methods(methods);
        }
        
        // 处理其他条件
        if (methodMapping.params().length > 0) {
            builder.params(methodMapping.params());
        }
        if (methodMapping.headers().length > 0) {
            builder.headers(methodMapping.headers());
        }
        if (methodMapping.consumes().length > 0) {
            builder.consumes(methodMapping.consumes());
        }
        if (methodMapping.produces().length > 0) {
            builder.produces(methodMapping.produces());
        }
        
        return builder.build();
    }
    
    private void addPaths(List<String> paths, String[] values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                paths.add(value.trim());
            }
        }
    }
    
    private String combinePath(String typePath, String methodPath) {
        if (!typePath.endsWith("/") && !methodPath.startsWith("/")) {
            return typePath + "/" + methodPath;
        } else if (typePath.endsWith("/") && methodPath.startsWith("/")) {
            return typePath + methodPath.substring(1);
        } else {
            return typePath + methodPath;
        }
    }
    
    /**
     * 注册处理器方法
     */
    protected void registerHandlerMethod(RequestMappingInfo info, HandlerMethod handlerMethod) {
        this.handlerMethods.put(info, handlerMethod);
    }
    
    /**
     * 获取URL路径辅助器（简化实现）
     */
    protected UrlPathHelper getUrlPathHelper() {
        return new UrlPathHelper();
    }
    
    /**
     * 匹配结果包装类
     */
    private static class Match {
        private final RequestMappingInfo info;
        private final HandlerMethod handlerMethod;
        
        public Match(RequestMappingInfo info, HandlerMethod handlerMethod) {
            this.info = info;
            this.handlerMethod = handlerMethod;
        }
    }
    
    /**
     * 简化的URL路径辅助器
     */
    private static class UrlPathHelper {
        public String getLookupPathForRequest(HttpServletRequest request) {
            String path = request.getRequestURI();
            String contextPath = request.getContextPath();
            if (contextPath != null && path.startsWith(contextPath)) {
                path = path.substring(contextPath.length());
            }
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return path;
        }
    }
}
