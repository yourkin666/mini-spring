package com.minispring.webmvc.handler;

import com.minispring.ioc.annotation.Component;
import com.minispring.ioc.context.ApplicationContext;
import com.minispring.ioc.context.ApplicationContextAware;
import com.minispring.webmvc.annotation.Controller;
import com.minispring.webmvc.annotation.RequestMapping;
import com.minispring.webmvc.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 简化版RequestMappingHandlerMapping
 * 体现Spring MVC的核心设计，避免过度复杂
 */
@Component("requestMappingHandlerMapping")
public class SimpleRequestMappingHandlerMapping implements HandlerMapping, ApplicationContextAware {
    
    private final Map<SimpleRequestMappingInfo, HandlerMethod> handlerMethods = new HashMap<>();
    private ApplicationContext applicationContext;
    
    @Override
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
        
        return new HandlerExecutionChain(handlerMethod);
    }
    
    protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        for (Map.Entry<SimpleRequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            if (entry.getKey().matches(request)) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    /**
     * 初始化处理器方法
     */
    protected void initHandlerMethods() {
        if (applicationContext == null) return;
        
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            try {
                Object bean = applicationContext.getBean(beanName);
                if (isHandler(bean.getClass())) {
                    detectHandlerMethods(bean);
                }
            } catch (Exception e) {
                System.err.println("Error processing controller: " + beanName);
            }
        }
        
        System.out.println("SimpleRequestMappingHandlerMapping: registered " + 
                handlerMethods.size() + " handler methods");
    }
    
    protected boolean isHandler(Class<?> beanType) {
        return beanType.isAnnotationPresent(Controller.class);
    }
    
    protected void detectHandlerMethods(Object handler) {
        Class<?> handlerType = handler.getClass();
        
        // 获取类级别的@RequestMapping
        RequestMapping typeMapping = handlerType.getAnnotation(RequestMapping.class);
        String[] typePaths = typeMapping != null ? typeMapping.value() : new String[]{""};
        
        Method[] methods = handlerType.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
                
                // 合并路径
                String[] paths = combinePaths(typePaths, methodMapping.value());
                RequestMethod[] httpMethods = methodMapping.method();
                
                SimpleRequestMappingInfo info = new SimpleRequestMappingInfo(paths, httpMethods);
                HandlerMethod handlerMethod = new HandlerMethod(handler, method);
                
                handlerMethods.put(info, handlerMethod);
                
                System.out.println("Mapped \"" + info + "\" onto " + handlerMethod.getShortLogMessage());
            }
        }
    }
    
    private String[] combinePaths(String[] typePaths, String[] methodPaths) {
        if (methodPaths.length == 0) return typePaths;
        if (typePaths.length == 0) return methodPaths;
        
        String[] result = new String[typePaths.length * methodPaths.length];
        int index = 0;
        for (String typePath : typePaths) {
            for (String methodPath : methodPaths) {
                result[index++] = combinePath(typePath, methodPath);
            }
        }
        return result;
    }
    
    private String combinePath(String typePath, String methodPath) {
        if (typePath.isEmpty()) return methodPath;
        if (methodPath.isEmpty()) return typePath;
        
        if (!typePath.endsWith("/") && !methodPath.startsWith("/")) {
            return typePath + "/" + methodPath;
        } else if (typePath.endsWith("/") && methodPath.startsWith("/")) {
            return typePath + methodPath.substring(1);
        } else {
            return typePath + methodPath;
        }
    }
}
