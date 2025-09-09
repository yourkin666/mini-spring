package com.minispring.webmvc.servlet;

import com.minispring.ioc.context.ApplicationContext;
import com.minispring.webmvc.ModelAndView;
import com.minispring.webmvc.handler.HandlerAdapter;
import com.minispring.webmvc.handler.HandlerExecutionChain;
import com.minispring.webmvc.handler.HandlerMapping;
import com.minispring.webmvc.view.View;
import com.minispring.webmvc.view.ViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * DispatcherServlet - Spring MVC的前端控制器
 * 
 * 这是Spring Web MVC框架的中央调度器，用于HTTP请求处理器/控制器。
 * 它集成了Spring的IoC容器，并因此允许您使用Spring的每个功能。
 * 
 * 体现的设计模式：
 * 1. 前端控制器模式 (Front Controller) - 统一的请求入口点
 * 2. 模板方法模式 (Template Method) - doDispatch方法定义了处理流程
 * 3. 策略模式 (Strategy) - 使用HandlerMapping、HandlerAdapter、ViewResolver策略
 */
public class DispatcherServlet extends HttpServlet {
    
    /** 默认策略配置文件的位置 */
    private static final String DEFAULT_STRATEGIES_PATH = "DispatcherServlet.properties";
    
    /** 默认的策略前缀 */
    private static final String DEFAULT_STRATEGIES_PREFIX = "org.springframework.web.servlet";
    
    /** 检测到的HandlerMapping集合 */
    private List<HandlerMapping> handlerMappings;
    
    /** 检测到的HandlerAdapter集合 */
    private List<HandlerAdapter> handlerAdapters;
    
    /** 检测到的ViewResolver集合 */
    private List<ViewResolver> viewResolvers;
    
    /** WebApplicationContext上下文 */
    private ApplicationContext webApplicationContext;
    
    /** 是否检测所有的HandlerMapping */
    private boolean detectAllHandlerMappings = true;
    
    /** 是否检测所有的HandlerAdapter */
    private boolean detectAllHandlerAdapters = true;
    
    /** 是否检测所有的ViewResolver */
    private boolean detectAllViewResolvers = true;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        try {
            // 初始化WebApplicationContext
            initWebApplicationContext();
            
            // 初始化DispatcherServlet的策略对象
            initStrategies();
            
            System.out.println("DispatcherServlet初始化完成");
            
        } catch (Exception e) {
            throw new ServletException("DispatcherServlet初始化失败", e);
        }
    }
    
    /**
     * 初始化WebApplicationContext
     * 这个方法体现了Spring的上下文层次结构设计
     */
    protected void initWebApplicationContext() {
        // 从ServletContext中获取或创建ApplicationContext
        // 简化实现：这里假设已经在servlet context中设置了ApplicationContext
        Object context = getServletContext().getAttribute("applicationContext");
        if (context instanceof ApplicationContext) {
            this.webApplicationContext = (ApplicationContext) context;
        } else {
            System.err.println("警告: 未找到ApplicationContext，某些功能可能不可用");
        }
    }
    
    /**
     * 初始化所有策略对象
     * 这个方法体现了Spring的自动检测和配置设计
     */
    protected void initStrategies() {
        initHandlerMappings();
        initHandlerAdapters();  
        initViewResolvers();
    }
    
    /**
     * 初始化HandlerMapping对象
     * 体现Spring的策略模式设计 - 支持多种映射策略
     */
    private void initHandlerMappings() {
        this.handlerMappings = new ArrayList<>();
        
        if (this.webApplicationContext != null) {
            if (this.detectAllHandlerMappings) {
                // 从ApplicationContext中查找所有HandlerMapping
                try {
                    String[] mappingNames = this.webApplicationContext.getBeanDefinitionNames();
                    for (String name : mappingNames) {
                        Object bean = this.webApplicationContext.getBean(name);
                        if (bean instanceof HandlerMapping) {
                            this.handlerMappings.add((HandlerMapping) bean);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("检测HandlerMapping失败: " + e.getMessage());
                }
            }
        }
        
        // 如果没有找到HandlerMapping，使用默认的
        if (this.handlerMappings.isEmpty()) {
            // 这里应该创建默认的HandlerMapping，暂时留空
            System.out.println("使用默认的HandlerMapping策略");
        }
    }
    
    /**
     * 初始化HandlerAdapter对象
     */
    private void initHandlerAdapters() {
        this.handlerAdapters = new ArrayList<>();
        
        if (this.webApplicationContext != null) {
            if (this.detectAllHandlerAdapters) {
                try {
                    String[] adapterNames = this.webApplicationContext.getBeanDefinitionNames();
                    for (String name : adapterNames) {
                        Object bean = this.webApplicationContext.getBean(name);
                        if (bean instanceof HandlerAdapter) {
                            this.handlerAdapters.add((HandlerAdapter) bean);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("检测HandlerAdapter失败: " + e.getMessage());
                }
            }
        }
        
        if (this.handlerAdapters.isEmpty()) {
            System.out.println("使用默认的HandlerAdapter策略");
        }
    }
    
    /**
     * 初始化ViewResolver对象
     */
    private void initViewResolvers() {
        this.viewResolvers = new ArrayList<>();
        
        if (this.webApplicationContext != null) {
            if (this.detectAllViewResolvers) {
                try {
                    String[] resolverNames = this.webApplicationContext.getBeanDefinitionNames();
                    for (String name : resolverNames) {
                        Object bean = this.webApplicationContext.getBean(name);
                        if (bean instanceof ViewResolver) {
                            this.viewResolvers.add((ViewResolver) bean);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("检测ViewResolver失败: " + e.getMessage());
                }
            }
        }
        
        if (this.viewResolvers.isEmpty()) {
            System.out.println("使用默认的ViewResolver策略");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp);
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp);
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp);
    }
    
    /**
     * 处理实际的调度逻辑
     * 这个方法是Spring MVC的核心，体现了模板方法模式
     * 定义了请求处理的标准流程：映射->适配->处理->视图解析->渲染
     */
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpServletRequest processedRequest = request;
        HandlerExecutionChain mappedHandler = null;
        
        Exception dispatchException = null;
        
        try {
            ModelAndView mv = null;
            
            try {
                // 1. 检查是否是multipart请求
                processedRequest = checkMultipart(request);
                
                // 2. 确定当前请求的处理器 - 策略模式的应用
                mappedHandler = getHandler(processedRequest);
                if (mappedHandler == null) {
                    noHandlerFound(processedRequest, response);
                    return;
                }
                
                // 3. 确定当前请求的处理器适配器 - 适配器模式的应用
                HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
                
                // 4. 处理last-modified头（如果处理器支持）
                String method = request.getMethod();
                boolean isGet = "GET".equals(method);
                if (isGet || "HEAD".equals(method)) {
                    long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
                    if (lastModified != -1 && !new ServletWebRequest(request, response).checkNotModified(lastModified)) {
                        return;
                    }
                }
                
                // 5. 应用拦截器的preHandle方法 - 责任链模式的应用
                if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                    return;
                }
                
                // 6. 实际调用处理器
                mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
                
                // 7. 应用默认视图名称（如果需要）
                if (mv != null && !mv.hasView()) {
                    String defaultViewName = getDefaultViewName(request);
                    if (defaultViewName != null) {
                        mv.setViewName(defaultViewName);
                    }
                }
                
                // 8. 应用拦截器的postHandle方法
                mappedHandler.applyPostHandle(processedRequest, response, mv);
                
            } catch (Exception ex) {
                dispatchException = ex;
            }
            
            // 9. 处理调度结果（包括异常情况）
            processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
            
        } catch (Exception ex) {
            triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
        } finally {
            // 清理multipart资源
            if (processedRequest != request) {
                cleanupMultipart(processedRequest);
            }
        }
    }
    
    /**
     * 检查multipart请求
     * 简化实现，直接返回原请求
     */
    protected HttpServletRequest checkMultipart(HttpServletRequest request) {
        return request;
    }
    
    /**
     * 返回此请求的HandlerExecutionChain
     * 尝试所有已注册的HandlerMapping以便找到匹配项
     * 体现了策略模式的应用
     */
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        if (this.handlerMappings != null) {
            for (HandlerMapping mapping : this.handlerMappings) {
                HandlerExecutionChain handler = mapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }
    
    /**
     * 返回此处理器对象的HandlerAdapter
     * 体现了适配器模式的应用
     */
    protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        if (this.handlerAdapters != null) {
            for (HandlerAdapter adapter : this.handlerAdapters) {
                if (adapter.supports(handler)) {
                    return adapter;
                }
            }
        }
        throw new ServletException("No adapter for handler [" + handler +
                "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
    }
    
    /**
     * 未找到处理器时的处理
     */
    protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.sendError(404);
    }
    
    /**
     * 获取默认视图名称
     */
    protected String getDefaultViewName(HttpServletRequest request) {
        // 简化实现：基于请求路径生成默认视图名称
        String path = request.getRequestURI();
        if (path.startsWith(request.getContextPath())) {
            path = path.substring(request.getContextPath().length());
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }
    
    /**
     * 处理调度结果
     * 包括视图解析和渲染
     */
    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
                                     HandlerExecutionChain mappedHandler, ModelAndView mv,
                                     Exception exception) throws Exception {
        
        boolean errorView = false;
        
        if (exception != null) {
            // 异常处理逻辑
            System.err.println("处理器执行异常: " + exception.getMessage());
            errorView = true;
        }
        
        // 如果有ModelAndView，进行视图渲染
        if (mv != null && !mv.wasCleared()) {
            render(mv, request, response);
            if (errorView) {
                // 清除错误属性
            }
        } else {
            if (System.getProperty("debug") != null) {
                System.out.println("Null ModelAndView returned to DispatcherServlet: assuming HandlerAdapter completed request handling");
            }
        }
        
        if (mappedHandler != null) {
            // 触发拦截器的afterCompletion方法
            mappedHandler.triggerAfterCompletion(request, response, exception);
        }
    }
    
    /**
     * 渲染给定的ModelAndView
     * 体现了视图解析的策略模式应用
     */
    protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置locale用于解析
        Locale locale = request.getLocale();
        
        View view;
        String viewName = mv.getViewName();
        if (viewName != null) {
            // 需要通过ViewResolver解析视图名称
            view = resolveViewName(viewName, mv.getModel(), locale, request);
            if (view == null) {
                throw new ServletException("Could not resolve view with name '" + mv.getViewName() + "'");
            }
        } else {
            // ModelAndView直接包含View实例
            view = (View) mv.getView();
            if (view == null) {
                throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a View object");
            }
        }
        
        // 委托给View进行渲染
        view.render(mv.getModel(), request, response);
    }
    
    /**
     * 通过配置的ViewResolver解析给定的视图名称
     */
    protected View resolveViewName(String viewName, Map<String, Object> model, Locale locale, HttpServletRequest request) throws Exception {
        if (this.viewResolvers != null) {
            for (ViewResolver viewResolver : this.viewResolvers) {
                View view = viewResolver.resolveViewName(viewName, locale);
                if (view != null) {
                    return view;
                }
            }
        }
        return null;
    }
    
    /**
     * 触发afterCompletion
     */
    private void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response,
                                      HandlerExecutionChain mappedHandler, Exception ex) throws Exception {
        if (mappedHandler != null) {
            mappedHandler.triggerAfterCompletion(request, response, ex);
        }
    }
    
    /**
     * 清理multipart资源
     */
    protected void cleanupMultipart(HttpServletRequest request) {
        // 简化实现，暂时不处理
    }
    
    /**
     * 简化的ServletWebRequest实现
     */
    private static class ServletWebRequest {
        private final HttpServletRequest request;
        private final HttpServletResponse response;
        
        public ServletWebRequest(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }
        
        public boolean checkNotModified(long lastModifiedTimestamp) {
            // 简化实现
            return false;
        }
    }
}
