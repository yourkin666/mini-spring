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

/**
 * 简化版DispatcherServlet - 贴近Spring源码的核心设计
 * 
 * 核心设计理念：
 * 1. 前端控制器模式 - 统一请求入口
 * 2. 模板方法模式 - doDispatch定义标准流程  
 * 3. 策略模式 - 可插拔的HandlerMapping/Adapter/ViewResolver
 */
public class SimpleDispatcherServlet extends HttpServlet {
    
    private ApplicationContext webApplicationContext;
    private List<HandlerMapping> handlerMappings;
    private List<HandlerAdapter> handlerAdapters;
    private List<ViewResolver> viewResolvers;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        // 1. 初始化WebApplicationContext
        this.webApplicationContext = (ApplicationContext) 
                getServletContext().getAttribute("applicationContext");
        
        if (this.webApplicationContext == null) {
            throw new ServletException("No WebApplicationContext found");
        }
        
        // 2. 初始化MVC组件
        initStrategies();
        
        System.out.println("SimpleDispatcherServlet initialized with " + 
                handlerMappings.size() + " HandlerMappings");
    }
    
    /**
     * 初始化MVC策略组件
     */
    private void initStrategies() {
        this.handlerMappings = new ArrayList<>(webApplicationContext.getBeansOfType(HandlerMapping.class).values());
        this.handlerAdapters = new ArrayList<>(webApplicationContext.getBeansOfType(HandlerAdapter.class).values());
        this.viewResolvers = new ArrayList<>(webApplicationContext.getBeansOfType(ViewResolver.class).values());
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        doDispatch(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        doDispatch(req, resp);
    }
    
    /**
     * 核心分发方法 - 体现模板方法模式
     * 定义了Spring MVC的标准处理流程
     */
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HandlerExecutionChain mappedHandler = null;
        Exception dispatchException = null;
        
        try {
            ModelAndView mv = null;
            
            // 1. 获取处理器
            mappedHandler = getHandler(request);
            if (mappedHandler == null) {
                response.sendError(404);
                return;
            }
            
            // 2. 获取处理器适配器
            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
            
            // 3. 执行拦截器preHandle
            if (!mappedHandler.applyPreHandle(request, response)) {
                return;
            }
            
            // 4. 调用处理器
            mv = ha.handle(request, response, mappedHandler.getHandler());
            
            // 5. 执行拦截器postHandle
            mappedHandler.applyPostHandle(request, response, mv);
            
            // 6. 处理结果（渲染视图）
            if (mv != null) {
                render(mv, request, response);
            }
            
        } catch (Exception ex) {
            dispatchException = ex;
            System.err.println("Request processing failed: " + ex.getMessage());
            response.sendError(500);
        } finally {
            // 7. 执行拦截器afterCompletion
            if (mappedHandler != null) {
                mappedHandler.triggerAfterCompletion(request, response, dispatchException);
            }
        }
    }
    
    /**
     * 获取请求处理器 - 体现策略模式
     */
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        for (HandlerMapping mapping : handlerMappings) {
            HandlerExecutionChain handler = mapping.getHandler(request);
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }
    
    /**
     * 获取处理器适配器 - 体现适配器模式
     */
    protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        for (HandlerAdapter adapter : handlerAdapters) {
            if (adapter.supports(handler)) {
                return adapter;
            }
        }
        throw new ServletException("No adapter for handler [" + handler + "]");
    }
    
    /**
     * 渲染视图
     */
    protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        String viewName = mv.getViewName();
        if (viewName == null) return;
        
        // 解析视图
        View view = resolveViewName(viewName, request.getLocale());
        if (view == null) {
            throw new ServletException("Could not resolve view: " + viewName);
        }
        
        // 渲染
        view.render(mv.getModel(), request, response);
    }
    
    /**
     * 解析视图名称
     */
    protected View resolveViewName(String viewName, Locale locale) throws Exception {
        for (ViewResolver resolver : viewResolvers) {
            View view = resolver.resolveViewName(viewName, locale);
            if (view != null) {
                return view;
            }
        }
        return null;
    }
}
