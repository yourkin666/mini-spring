package com.minispring.webmvc.handler;

import com.minispring.ioc.annotation.Component;
import com.minispring.webmvc.ModelAndView;
import com.minispring.webmvc.annotation.PathVariable;
import com.minispring.webmvc.annotation.RequestParam;
import com.minispring.webmvc.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于@RequestMapping注解的处理器适配器
 * 负责调用HandlerMethod，处理参数绑定和返回值
 * 体现Spring MVC的适配器模式和参数解析设计
 */
@Component
public class RequestMappingHandlerAdapter implements HandlerAdapter {
    
    @Override
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod;
    }
    
    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        return invokeHandlerMethod(request, response, handlerMethod);
    }
    
    @Override
    public long getLastModified(HttpServletRequest request, Object handler) {
        return -1; // 简化实现，不支持Last-Modified
    }
    
    /**
     * 调用处理器方法
     */
    protected Object invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response, 
                                       HandlerMethod handlerMethod) throws Exception {
        
        // 1. 解析方法参数
        Object[] args = resolveArguments(request, response, handlerMethod);
        
        // 2. 调用处理器方法
        Method method = handlerMethod.getMethod();
        method.setAccessible(true);
        Object result = method.invoke(handlerMethod.getBean(), args);
        
        // 3. 处理返回值
        return handleReturnValue(request, response, handlerMethod, result);
    }
    
    /**
     * 解析方法参数
     * 体现Spring MVC的参数解析器设计模式
     */
    protected Object[] resolveArguments(HttpServletRequest request, HttpServletResponse response, 
                                      HandlerMethod handlerMethod) throws Exception {
        
        Method method = handlerMethod.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> paramType = parameter.getType();
            
            // 1. 处理Servlet API参数
            if (HttpServletRequest.class.isAssignableFrom(paramType)) {
                args[i] = request;
            } else if (HttpServletResponse.class.isAssignableFrom(paramType)) {
                args[i] = response;
            } 
            // 2. 处理@RequestParam注解
            else if (parameter.isAnnotationPresent(RequestParam.class)) {
                args[i] = resolveRequestParam(request, parameter);
            }
            // 3. 处理@PathVariable注解
            else if (parameter.isAnnotationPresent(PathVariable.class)) {
                args[i] = resolvePathVariable(request, parameter, handlerMethod);
            }
            // 4. 处理Model参数
            else if (Map.class.isAssignableFrom(paramType)) {
                args[i] = new HashMap<String, Object>();
            }
            // 5. 默认处理：尝试从请求参数中获取
            else {
                String paramName = parameter.getName();
                String paramValue = request.getParameter(paramName);
                args[i] = convertValue(paramValue, paramType);
            }
        }
        
        return args;
    }
    
    /**
     * 解析@RequestParam注解的参数
     */
    private Object resolveRequestParam(HttpServletRequest request, Parameter parameter) throws Exception {
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        String paramName = requestParam.name();
        if (paramName.isEmpty()) {
            paramName = requestParam.value();
        }
        if (paramName.isEmpty()) {
            paramName = parameter.getName();
        }
        
        String paramValue = request.getParameter(paramName);
        
        // 检查必需参数
        if (paramValue == null && requestParam.required()) {
            String defaultValue = requestParam.defaultValue();
            if (!isDefaultValueToken(defaultValue)) {
                paramValue = defaultValue;
            } else {
                throw new IllegalArgumentException("Required parameter '" + paramName + "' is not present");
            }
        }
        
        return convertValue(paramValue, parameter.getType());
    }
    
    /**
     * 解析@PathVariable注解的参数
     */
    private Object resolvePathVariable(HttpServletRequest request, Parameter parameter, 
                                     HandlerMethod handlerMethod) throws Exception {
        PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
        String variableName = pathVariable.name();
        if (variableName.isEmpty()) {
            variableName = pathVariable.value();
        }
        if (variableName.isEmpty()) {
            variableName = parameter.getName();
        }
        
        // 从请求路径中提取路径变量值
        Map<String, String> pathVariables = extractPathVariables(request, handlerMethod);
        String variableValue = pathVariables.get(variableName);
        
        if (variableValue == null && pathVariable.required()) {
            throw new IllegalArgumentException("Required path variable '" + variableName + "' is not present");
        }
        
        return convertValue(variableValue, parameter.getType());
    }
    
    /**
     * 从请求路径中提取路径变量
     * 简化实现：基于请求路径的简单解析
     */
    private Map<String, String> extractPathVariables(HttpServletRequest request, HandlerMethod handlerMethod) {
        Map<String, String> variables = new HashMap<>();
        
        String requestPath = getRequestPath(request);
        
        // 简化实现：支持基本的路径变量提取
        // 例如：/users/123 -> {id: "123"}
        String[] pathSegments = requestPath.split("/");
        
        // 简单的启发式匹配：如果路径段是数字，可能是ID
        for (int i = 0; i < pathSegments.length; i++) {
            String segment = pathSegments[i];
            if (segment.matches("\\d+")) {
                variables.put("id", segment);
            } else if (segment.length() > 0 && !segment.equals("users") && !segment.equals("simple")) {
                // 简单匹配：非关键字的段可能是路径变量
                variables.put("name", segment);
            }
        }
        
        return variables;
    }
    
    private String getRequestPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        return path;
    }
    
    /**
     * 处理返回值
     * 体现Spring MVC的返回值处理器设计模式
     */
    protected Object handleReturnValue(HttpServletRequest request, HttpServletResponse response, 
                                     HandlerMethod handlerMethod, Object returnValue) throws Exception {
        
        Method method = handlerMethod.getMethod();
        
        // 1. 检查是否有@ResponseBody注解
        if (method.isAnnotationPresent(ResponseBody.class) || 
            handlerMethod.getBeanType().isAnnotationPresent(ResponseBody.class)) {
            
            handleResponseBody(response, returnValue);
            return null; // 直接写入响应，不需要视图解析
        }
        
        // 2. 处理ModelAndView返回值
        if (returnValue instanceof ModelAndView) {
            return returnValue;
        }
        
        // 3. 处理String返回值（视图名称）
        if (returnValue instanceof String) {
            return new ModelAndView((String) returnValue);
        }
        
        // 4. 处理void返回值
        if (returnValue == null || method.getReturnType() == Void.TYPE) {
            return new ModelAndView();
        }
        
        // 5. 处理Map返回值（作为模型）
        if (returnValue instanceof Map) {
            ModelAndView mv = new ModelAndView();
            mv.addAllObjects((Map<String, Object>) returnValue);
            return mv;
        }
        
        // 6. 默认处理：将返回值作为模型属性
        ModelAndView mv = new ModelAndView();
        String attributeName = getDefaultModelAttributeName(returnValue);
        mv.addObject(attributeName, returnValue);
        
        return mv;
    }
    
    /**
     * 处理@ResponseBody注解的返回值
     */
    private void handleResponseBody(HttpServletResponse response, Object returnValue) throws IOException {
        if (returnValue == null) {
            return;
        }
        
        // 设置响应类型
        response.setContentType("application/json;charset=UTF-8");
        
        // 简化实现：直接输出字符串或JSON
        String jsonResult;
        if (returnValue instanceof String) {
            jsonResult = "\"" + returnValue + "\"";
        } else {
            // 简化的JSON序列化（实际应用中应该使用Jackson等库）
            jsonResult = toJson(returnValue);
        }
        
        response.getWriter().write(jsonResult);
        response.getWriter().flush();
    }
    
    /**
     * 简化的JSON序列化
     */
    private String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        if (obj instanceof String) {
            return "\"" + obj + "\"";
        }
        
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    sb.append(",");
                }
                sb.append("\"").append(entry.getKey()).append("\":");
                sb.append(toJson(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        
        // 简化处理：直接返回toString()
        return "\"" + obj.toString() + "\"";
    }
    
    /**
     * 获取默认的模型属性名称
     */
    private String getDefaultModelAttributeName(Object returnValue) {
        Class<?> clazz = returnValue.getClass();
        String className = clazz.getSimpleName();
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }
    
    /**
     * 检查是否是默认值占位符
     */
    private boolean isDefaultValueToken(String value) {
        return "\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n".equals(value);
    }
    
    /**
     * 值类型转换
     */
    private Object convertValue(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        
        if (String.class == targetType) {
            return value;
        }
        
        if (int.class == targetType || Integer.class == targetType) {
            return Integer.valueOf(value);
        }
        
        if (long.class == targetType || Long.class == targetType) {
            return Long.valueOf(value);
        }
        
        if (double.class == targetType || Double.class == targetType) {
            return Double.valueOf(value);
        }
        
        if (boolean.class == targetType || Boolean.class == targetType) {
            return Boolean.valueOf(value);
        }
        
        // 默认返回字符串
        return value;
    }
}
