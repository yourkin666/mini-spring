package com.minispring.webmvc.example;

import com.minispring.ioc.context.AnnotationConfigApplicationContext;
import com.minispring.webmvc.ModelAndView;
import com.minispring.webmvc.handler.*;
import com.minispring.webmvc.servlet.DispatcherServlet;
import com.minispring.webmvc.view.ViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring MVC框架演示类
 * 展示完整的MVC工作流程和各种功能特性
 * 体现Spring MVC的设计理念和架构优势
 */
public class SpringMvcDemo {
    
    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("       Mini Spring MVC Framework Demo        ");
        System.out.println("===============================================\n");
        
        try {
            // 1. 演示应用上下文创建和Bean注册
            demonstrateApplicationContextCreation();
            
            // 2. 演示HandlerMapping功能
            demonstrateHandlerMapping();
            
            // 3. 演示HandlerAdapter功能  
            demonstrateHandlerAdapter();
            
            // 4. 演示ViewResolver功能
            demonstrateViewResolver();
            
            // 5. 演示完整的MVC流程
            demonstrateCompleteWorkflow();
            
        } catch (Exception e) {
            System.err.println("演示过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n===============================================");
        System.out.println("       Mini Spring MVC Demo 完成              ");
        System.out.println("===============================================");
    }
    
    /**
     * 演示应用上下文创建和组件注册
     */
    private static void demonstrateApplicationContextCreation() {
        System.out.println("1. === 应用上下文创建演示 ===");
        
        AnnotationConfigApplicationContext context = 
                WebApplicationInitializer.createApplicationContext();
        
        // 验证核心组件是否正确注册
        try {
            HandlerMapping handlerMapping = context.getBean("requestMappingHandlerMapping", HandlerMapping.class);
            HandlerAdapter handlerAdapter = context.getBean("requestMappingHandlerAdapter", HandlerAdapter.class);
            ViewResolver viewResolver = context.getBean("viewResolver", ViewResolver.class);
            UserController userController = context.getBean(UserController.class);
            
            System.out.println("✓ HandlerMapping: " + handlerMapping.getClass().getSimpleName());
            System.out.println("✓ HandlerAdapter: " + handlerAdapter.getClass().getSimpleName());
            System.out.println("✓ ViewResolver: " + viewResolver.getClass().getSimpleName());
            System.out.println("✓ UserController: " + userController.getClass().getSimpleName());
            
        } catch (Exception e) {
            System.err.println("✗ 组件注册验证失败: " + e.getMessage());
        }
        
        System.out.println("✓ 应用上下文创建演示完成\n");
    }
    
    /**
     * 演示HandlerMapping功能
     */
    private static void demonstrateHandlerMapping() {
        System.out.println("2. === HandlerMapping演示 ===");
        
        try {
            AnnotationConfigApplicationContext context = 
                    WebApplicationInitializer.createApplicationContext();
            
            RequestMappingHandlerMapping handlerMapping = 
                    (RequestMappingHandlerMapping) context.getBean("requestMappingHandlerMapping");
            
            // 模拟HTTP请求
            MockHttpServletRequest request1 = new MockHttpServletRequest("GET", "/users");
            MockHttpServletRequest request2 = new MockHttpServletRequest("GET", "/users/123");
            MockHttpServletRequest request3 = new MockHttpServletRequest("GET", "/users/search");
            
            System.out.println("测试请求映射:");
            testHandlerMapping(handlerMapping, request1);
            testHandlerMapping(handlerMapping, request2);
            testHandlerMapping(handlerMapping, request3);
            
        } catch (Exception e) {
            System.err.println("✗ HandlerMapping演示失败: " + e.getMessage());
        }
        
        System.out.println("✓ HandlerMapping演示完成\n");
    }
    
    private static void testHandlerMapping(HandlerMapping handlerMapping, HttpServletRequest request) {
        try {
            HandlerExecutionChain chain = handlerMapping.getHandler(request);
            if (chain != null) {
                HandlerMethod handler = (HandlerMethod) chain.getHandler();
                System.out.println("  " + request.getMethod() + " " + request.getRequestURI() + 
                        " -> " + handler.getShortLogMessage());
            } else {
                System.out.println("  " + request.getMethod() + " " + request.getRequestURI() + 
                        " -> [未找到处理器]");
            }
        } catch (Exception e) {
            System.out.println("  " + request.getMethod() + " " + request.getRequestURI() + 
                    " -> [映射失败: " + e.getMessage() + "]");
        }
    }
    
    /**
     * 演示HandlerAdapter功能
     */
    private static void demonstrateHandlerAdapter() {
        System.out.println("3. === HandlerAdapter演示 ===");
        
        try {
            AnnotationConfigApplicationContext context = 
                    WebApplicationInitializer.createApplicationContext();
            
            RequestMappingHandlerAdapter handlerAdapter = 
                    (RequestMappingHandlerAdapter) context.getBean("requestMappingHandlerAdapter");
            
            UserController userController = context.getBean(UserController.class);
            
            // 创建HandlerMethod
            HandlerMethod handlerMethod = new HandlerMethod(userController, "getAllUsers");
            
            System.out.println("测试HandlerAdapter支持:");
            System.out.println("  supports(HandlerMethod): " + handlerAdapter.supports(handlerMethod));
            System.out.println("  supports(String): " + handlerAdapter.supports("test"));
            
            // 模拟方法调用
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users");
            MockHttpServletResponse response = new MockHttpServletResponse();
            
            Object result = handlerAdapter.handle(request, response, handlerMethod);
            System.out.println("  方法调用结果类型: " + (result != null ? result.getClass().getSimpleName() : "null"));
            
            if (result instanceof ModelAndView) {
                ModelAndView mv = (ModelAndView) result;
                System.out.println("  视图名称: " + mv.getViewName());
                System.out.println("  模型键数量: " + mv.getModel().size());
            }
            
        } catch (Exception e) {
            System.err.println("✗ HandlerAdapter演示失败: " + e.getMessage());
        }
        
        System.out.println("✓ HandlerAdapter演示完成\n");
    }
    
    /**
     * 演示ViewResolver功能
     */
    private static void demonstrateViewResolver() {
        System.out.println("4. === ViewResolver演示 ===");
        
        try {
            AnnotationConfigApplicationContext context = 
                    WebApplicationInitializer.createApplicationContext();
            
            ViewResolver viewResolver = context.getBean("viewResolver", ViewResolver.class);
            
            System.out.println("测试视图解析:");
            
            String[] viewNames = {"user/list", "user/detail", "error/404", "redirect:/users", "forward:/index"};
            
            for (String viewName : viewNames) {
                try {
                    Object view = viewResolver.resolveViewName(viewName, null);
                    System.out.println("  " + viewName + " -> " + 
                            (view != null ? view.toString() : "[无法解析]"));
                } catch (Exception e) {
                    System.out.println("  " + viewName + " -> [解析失败: " + e.getMessage() + "]");
                }
            }
            
        } catch (Exception e) {
            System.err.println("✗ ViewResolver演示失败: " + e.getMessage());
        }
        
        System.out.println("✓ ViewResolver演示完成\n");
    }
    
    /**
     * 演示完整的MVC工作流程
     */
    private static void demonstrateCompleteWorkflow() {
        System.out.println("5. === 完整MVC流程演示 ===");
        
        try {
            AnnotationConfigApplicationContext context = 
                    WebApplicationInitializer.createApplicationContext();
            
            // 将ApplicationContext设置到模拟的ServletContext中
            MockServletContext servletContext = new MockServletContext();
            servletContext.setAttribute("applicationContext", context);
            
            // 创建DispatcherServlet并初始化
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            MockServletConfig servletConfig = new MockServletConfig(servletContext);
            dispatcherServlet.init(servletConfig);
            
            // 模拟不同的HTTP请求
            System.out.println("模拟请求处理:");
            
            // GET /users
            processRequest(dispatcherServlet, "GET", "/users", null);
            
            // GET /users/123  
            processRequest(dispatcherServlet, "GET", "/users/123", null);
            
            // GET /users/stats (JSON)
            processRequest(dispatcherServlet, "GET", "/users/stats", "application/json");
            
            // POST /users (创建用户)
            MockHttpServletRequest postRequest = new MockHttpServletRequest("POST", "/users");
            postRequest.setParameter("name", "测试用户");
            postRequest.setParameter("email", "test@example.com");
            MockHttpServletResponse postResponse = new MockHttpServletResponse();
            
            System.out.println("  POST /users (创建用户):");
            try {
                dispatcherServlet.service(postRequest, postResponse);
                System.out.println("    处理状态: " + postResponse.getStatus());
                System.out.println("    响应类型: " + postResponse.getContentType());
            } catch (Exception e) {
                System.out.println("    处理失败: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("✗ 完整流程演示失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("✓ 完整MVC流程演示完成\n");
    }
    
    private static void processRequest(DispatcherServlet servlet, String method, String uri, String accept) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        if (accept != null) {
            request.addHeader("Accept", accept);
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        System.out.println("  " + method + " " + uri + ":");
        try {
            servlet.service(request, response);
            System.out.println("    状态: " + response.getStatus());
            System.out.println("    内容类型: " + response.getContentType());
            if (response.getContentAsString() != null && !response.getContentAsString().isEmpty()) {
                System.out.println("    响应内容: " + response.getContentAsString().substring(0, 
                        Math.min(100, response.getContentAsString().length())) + "...");
            }
        } catch (Exception e) {
            System.out.println("    处理失败: " + e.getMessage());
        }
    }
    
    // 简化的Mock类，用于测试
    static class MockHttpServletRequest implements HttpServletRequest {
        private String method;
        private String uri;
        private Map<String, String> parameters = new HashMap<>(); 
        private Map<String, String> headers = new HashMap<>();
        
        public MockHttpServletRequest(String method, String uri) {
            this.method = method;
            this.uri = uri;
        }
        
        public void setParameter(String name, String value) {
            parameters.put(name, value);
        }
        
        public void addHeader(String name, String value) {
            headers.put(name, value);
        }
        
        @Override public String getMethod() { return method; }
        @Override public String getRequestURI() { return uri; }
        @Override public String getContextPath() { return ""; }
        @Override public String getParameter(String name) { return parameters.get(name); }
        @Override public String getHeader(String name) { return headers.get(name); }
        @Override public String getContentType() { return headers.get("Content-Type"); }
        @Override public java.util.Locale getLocale() { return java.util.Locale.getDefault(); }
        
        // 其他方法的简化实现...
        @Override public String getAuthType() { return null; }
        @Override public javax.servlet.http.Cookie[] getCookies() { return new javax.servlet.http.Cookie[0]; }
        @Override public long getDateHeader(String name) { return -1; }
        @Override public java.util.Enumeration<String> getHeaders(String name) { return null; }
        @Override public java.util.Enumeration<String> getHeaderNames() { return null; }
        @Override public int getIntHeader(String name) { return -1; }
        @Override public String getPathInfo() { return null; }
        @Override public String getPathTranslated() { return null; }
        @Override public String getQueryString() { return null; }
        @Override public String getRemoteUser() { return null; }
        @Override public boolean isUserInRole(String role) { return false; }
        @Override public java.security.Principal getUserPrincipal() { return null; }
        @Override public String getRequestedSessionId() { return null; }
        @Override public StringBuffer getRequestURL() { return new StringBuffer(uri); }
        @Override public String getServletPath() { return ""; }
        @Override public javax.servlet.http.HttpSession getSession(boolean create) { return null; }
        @Override public javax.servlet.http.HttpSession getSession() { return null; }
        @Override public String changeSessionId() { return null; }
        @Override public boolean isRequestedSessionIdValid() { return false; }
        @Override public boolean isRequestedSessionIdFromCookie() { return false; }
        @Override public boolean isRequestedSessionIdFromURL() { return false; }
        @Override public boolean isRequestedSessionIdFromUrl() { return false; }
        @Override public boolean authenticate(HttpServletResponse response) { return false; }
        @Override public void login(String username, String password) {}
        @Override public void logout() {}
        @Override public java.util.Collection<javax.servlet.http.Part> getParts() { return null; }
        @Override public javax.servlet.http.Part getPart(String name) { return null; }
        @Override public <T extends javax.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> handlerClass) { return null; }
        @Override public Object getAttribute(String name) { return null; }
        @Override public java.util.Enumeration<String> getAttributeNames() { return null; }
        @Override public String getCharacterEncoding() { return "UTF-8"; }
        @Override public void setCharacterEncoding(String env) {}
        @Override public int getContentLength() { return -1; }
        @Override public long getContentLengthLong() { return -1; }
        @Override public javax.servlet.ServletInputStream getInputStream() { return null; }
        @Override public String[] getParameterValues(String name) { return null; }
        @Override public java.util.Enumeration<String> getParameterNames() { return null; }
        @Override public java.util.Map<String, String[]> getParameterMap() { return null; }
        @Override public String getProtocol() { return "HTTP/1.1"; }
        @Override public String getScheme() { return "http"; }
        @Override public String getServerName() { return "localhost"; }
        @Override public int getServerPort() { return 8080; }
        @Override public java.io.BufferedReader getReader() { return null; }
        @Override public String getRemoteAddr() { return "127.0.0.1"; }
        @Override public String getRemoteHost() { return "localhost"; }
        @Override public void setAttribute(String name, Object o) {}
        @Override public void removeAttribute(String name) {}
        @Override public java.util.Enumeration<java.util.Locale> getLocales() { return null; }
        @Override public boolean isSecure() { return false; }
        @Override public javax.servlet.RequestDispatcher getRequestDispatcher(String path) { return null; }
        @Override public String getRealPath(String path) { return null; }
        @Override public int getRemotePort() { return 0; }
        @Override public String getLocalName() { return "localhost"; }
        @Override public String getLocalAddr() { return "127.0.0.1"; }
        @Override public int getLocalPort() { return 8080; }
        @Override public javax.servlet.ServletContext getServletContext() { return null; }
        @Override public javax.servlet.AsyncContext startAsync() { return null; }
        @Override public javax.servlet.AsyncContext startAsync(javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse) { return null; }
        @Override public boolean isAsyncStarted() { return false; }
        @Override public boolean isAsyncSupported() { return false; }
        @Override public javax.servlet.AsyncContext getAsyncContext() { return null; }
        @Override public javax.servlet.DispatcherType getDispatcherType() { return javax.servlet.DispatcherType.REQUEST; }
    }
    
    static class MockHttpServletResponse implements HttpServletResponse {
        private int status = 200;
        private String contentType;
        private StringBuilder content = new StringBuilder();
        private java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.StringWriter()) {
            @Override
            public void write(String s) {
                content.append(s);
            }
            @Override 
            public void write(char[] buf, int off, int len) {
                content.append(buf, off, len);
            }
        };
        
        public String getContentAsString() { return content.toString(); }
        @Override public void setStatus(int sc) { this.status = sc; }
        @Override public int getStatus() { return status; }
        @Override public void setContentType(String type) { this.contentType = type; }
        @Override public String getContentType() { return contentType; }
        @Override public java.io.PrintWriter getWriter() { return writer; }
        @Override public void sendError(int sc) { this.status = sc; }
        
        // 其他方法的简化实现...
        @Override public void addCookie(javax.servlet.http.Cookie cookie) {}
        @Override public boolean containsHeader(String name) { return false; }
        @Override public String encodeURL(String url) { return url; }
        @Override public String encodeRedirectURL(String url) { return url; }
        @Override public String encodeUrl(String url) { return url; }
        @Override public String encodeRedirectUrl(String url) { return url; }
        @Override public void sendError(int sc, String msg) { this.status = sc; }
        @Override public void sendRedirect(String location) {}
        @Override public void setDateHeader(String name, long date) {}
        @Override public void addDateHeader(String name, long date) {}
        @Override public void setHeader(String name, String value) {}
        @Override public void addHeader(String name, String value) {}
        @Override public void setIntHeader(String name, int value) {}
        @Override public void addIntHeader(String name, int value) {}
        @Override public void setStatus(int sc, String sm) { this.status = sc; }
        @Override public String getHeader(String name) { return null; }
        @Override public java.util.Collection<String> getHeaders(String name) { return null; }
        @Override public java.util.Collection<String> getHeaderNames() { return null; }
        @Override public String getCharacterEncoding() { return "UTF-8"; }
        @Override public javax.servlet.ServletOutputStream getOutputStream() { return null; }
        @Override public void setCharacterEncoding(String charset) {}
        @Override public void setContentLength(int len) {}
        @Override public void setContentLengthLong(long len) {}
        @Override public void setBufferSize(int size) {}
        @Override public int getBufferSize() { return 0; }
        @Override public void flushBuffer() {}
        @Override public void resetBuffer() {}
        @Override public boolean isCommitted() { return false; }
        @Override public void reset() {}
        @Override public void setLocale(java.util.Locale loc) {}
        @Override public java.util.Locale getLocale() { return java.util.Locale.getDefault(); }
    }
    
    static class MockServletContext implements javax.servlet.ServletContext {
        private Map<String, Object> attributes = new HashMap<>();
        
        @Override public Object getAttribute(String name) { return attributes.get(name); }
        @Override public void setAttribute(String name, Object object) { attributes.put(name, object); }
        @Override public void removeAttribute(String name) { attributes.remove(name); }
        @Override public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, javax.servlet.Servlet servlet) { return null; }
        
        // 其他方法的简化实现...
        @Override public String getContextPath() { return ""; }
        @Override public javax.servlet.ServletContext getContext(String uripath) { return null; }
        @Override public int getMajorVersion() { return 3; }
        @Override public int getMinorVersion() { return 1; }
        @Override public int getEffectiveMajorVersion() { return 3; }
        @Override public int getEffectiveMinorVersion() { return 1; }
        @Override public String getMimeType(String file) { return null; }
        @Override public java.util.Set<String> getResourcePaths(String path) { return null; }
        @Override public java.net.URL getResource(String path) { return null; }
        @Override public java.io.InputStream getResourceAsStream(String path) { return null; }
        @Override public javax.servlet.RequestDispatcher getRequestDispatcher(String path) { return null; }
        @Override public javax.servlet.RequestDispatcher getNamedDispatcher(String name) { return null; }
        @Override public javax.servlet.Servlet getServlet(String name) { return null; }
        @Override public java.util.Enumeration<javax.servlet.Servlet> getServlets() { return null; }
        @Override public java.util.Enumeration<String> getServletNames() { return null; }
        @Override public void log(String msg) {}
        @Override public void log(Exception exception, String msg) {}
        @Override public void log(String message, Throwable throwable) {}
        @Override public String getRealPath(String path) { return null; }
        @Override public String getServerInfo() { return "Mock/1.0"; }
        @Override public String getInitParameter(String name) { return null; }
        @Override public java.util.Enumeration<String> getInitParameterNames() { return null; }
        @Override public boolean setInitParameter(String name, String value) { return false; }
        @Override public java.util.Enumeration<String> getAttributeNames() { return null; }
        @Override public String getServletContextName() { return "MockContext"; }
        @Override public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, String className) { return null; }
        @Override public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, Class<? extends javax.servlet.Servlet> servletClass) { return null; }
        @Override public <T extends javax.servlet.Servlet> T createServlet(Class<T> clazz) { return null; }
        @Override public javax.servlet.ServletRegistration getServletRegistration(String servletName) { return null; }
        @Override public java.util.Map<String, ? extends javax.servlet.ServletRegistration> getServletRegistrations() { return null; }
        @Override public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, javax.servlet.Filter filter) { return null; }
        @Override public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) { return null; }
        @Override public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends javax.servlet.Filter> filterClass) { return null; }
        @Override public <T extends javax.servlet.Filter> T createFilter(Class<T> clazz) { return null; }
        @Override public javax.servlet.FilterRegistration getFilterRegistration(String filterName) { return null; }
        @Override public java.util.Map<String, ? extends javax.servlet.FilterRegistration> getFilterRegistrations() { return null; }
        @Override public javax.servlet.SessionCookieConfig getSessionCookieConfig() { return null; }
        @Override public void setSessionTrackingModes(java.util.Set<javax.servlet.SessionTrackingMode> sessionTrackingModes) {}
        @Override public java.util.Set<javax.servlet.SessionTrackingMode> getDefaultSessionTrackingModes() { return null; }
        @Override public java.util.Set<javax.servlet.SessionTrackingMode> getEffectiveSessionTrackingModes() { return null; }
        @Override public void addListener(String className) {}
        @Override public <T extends java.util.EventListener> void addListener(T t) {}
        @Override public void addListener(Class<? extends java.util.EventListener> listenerClass) {}
        @Override public <T extends java.util.EventListener> T createListener(Class<T> clazz) { return null; }
        @Override public javax.servlet.descriptor.JspConfigDescriptor getJspConfigDescriptor() { return null; }
        @Override public ClassLoader getClassLoader() { return this.getClass().getClassLoader(); }
        @Override public void declareRoles(String... roleNames) {}
        @Override public String getVirtualServerName() { return null; }
    }
    
    static class MockServletConfig implements javax.servlet.ServletConfig {
        private javax.servlet.ServletContext servletContext;
        
        public MockServletConfig(javax.servlet.ServletContext servletContext) {
            this.servletContext = servletContext;
        }
        
        @Override public String getServletName() { return "mockServlet"; }
        @Override public javax.servlet.ServletContext getServletContext() { return servletContext; }
        @Override public String getInitParameter(String name) { return null; }
        @Override public java.util.Enumeration<String> getInitParameterNames() { return null; }
    }
}
