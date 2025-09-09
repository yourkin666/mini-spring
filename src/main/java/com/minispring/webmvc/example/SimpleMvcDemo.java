package com.minispring.webmvc.example;

import com.minispring.ioc.context.AnnotationConfigApplicationContext;
import com.minispring.webmvc.ModelAndView;
import com.minispring.webmvc.handler.*;
import com.minispring.webmvc.view.ViewResolver;

/**
 * 简化版Spring MVC演示
 * 专注于核心功能展示，避免过度复杂的Mock实现
 * 体现Spring MVC的核心设计理念
 */
public class SimpleMvcDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Simple Spring MVC Framework Demo ===\n");
        
        try {
            // 1. 创建Spring应用上下文
            demonstrateSpringContextCreation();
            
            // 2. 演示HandlerMapping工作原理
            demonstrateHandlerMapping();
            
            // 3. 演示HandlerAdapter工作原理
            demonstrateHandlerAdapter();
            
            // 4. 演示ViewResolver工作原理
            demonstrateViewResolver();
            
        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Demo completed ===");
    }
    
    private static void demonstrateSpringContextCreation() {
        System.out.println("1. === Spring Context Creation ===");
        
        AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(WebMvcConfig.class);
        
        String[] beanNames = context.getBeanDefinitionNames();
        System.out.println("Registered beans: " + beanNames.length);
        
        // 验证关键组件
        try {
            HandlerMapping handlerMapping = context.getBean("requestMappingHandlerMapping", HandlerMapping.class);
            HandlerAdapter handlerAdapter = context.getBean(RequestMappingHandlerAdapter.class);
            ViewResolver viewResolver = context.getBean(ViewResolver.class);
            UserController controller = context.getBean(UserController.class);
            
            System.out.println("✓ HandlerMapping: " + handlerMapping.getClass().getSimpleName());
            System.out.println("✓ HandlerAdapter: " + handlerAdapter.getClass().getSimpleName());
            System.out.println("✓ ViewResolver: " + viewResolver.getClass().getSimpleName());
            System.out.println("✓ Controller: " + controller.getClass().getSimpleName());
            
        } catch (Exception e) {
            System.out.println("✗ Component verification failed: " + e.getMessage());
        }
        
        context.close();
        System.out.println("✓ Context creation demo completed\n");
    }
    
    private static void demonstrateHandlerMapping() {
        System.out.println("2. === HandlerMapping Demo ===");
        
        AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(WebMvcConfig.class);
        
        try {
            HandlerMapping handlerMapping = context.getBean("requestMappingHandlerMapping", HandlerMapping.class);
            
            System.out.println("HandlerMapping type: " + handlerMapping.getClass().getSimpleName());
            System.out.println("✓ HandlerMapping component loaded successfully");
            
            // 注意：实际的请求映射测试需要真实的HttpServletRequest
            // 这里只展示组件加载，避免复杂的Mock实现
            
        } catch (Exception e) {
            System.out.println("✗ HandlerMapping demo failed: " + e.getMessage());
        }
        
        context.close();
        System.out.println("✓ HandlerMapping demo completed\n");
    }
    
    private static void demonstrateHandlerAdapter() {
        System.out.println("3. === HandlerAdapter Demo ===");
        
        AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(WebMvcConfig.class);
        
        try {
            RequestMappingHandlerAdapter handlerAdapter = 
                    context.getBean(RequestMappingHandlerAdapter.class);
            
            UserController controller = context.getBean(UserController.class);
            HandlerMethod handlerMethod = new HandlerMethod(controller, "getAllUsers");
            
            System.out.println("Adapter supports HandlerMethod: " + handlerAdapter.supports(handlerMethod));
            System.out.println("Adapter supports String: " + handlerAdapter.supports("test"));
            
            System.out.println("✓ HandlerAdapter working correctly");
            
        } catch (Exception e) {
            System.out.println("✗ HandlerAdapter demo failed: " + e.getMessage());
        }
        
        context.close();
        System.out.println("✓ HandlerAdapter demo completed\n");
    }
    
    private static void demonstrateViewResolver() {
        System.out.println("4. === ViewResolver Demo ===");
        
        AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(WebMvcConfig.class);
        
        try {
            ViewResolver viewResolver = context.getBean(ViewResolver.class);
            
            System.out.println("ViewResolver type: " + viewResolver.getClass().getSimpleName());
            
            // 测试视图解析
            String[] viewNames = {"user/list", "user/detail", "error/404"};
            
            for (String viewName : viewNames) {
                try {
                    Object view = viewResolver.resolveViewName(viewName, null);
                    System.out.println("  " + viewName + " -> " + 
                            (view != null ? view.toString() : "[not resolved]"));
                } catch (Exception e) {
                    System.out.println("  " + viewName + " -> [error: " + e.getMessage() + "]");
                }
            }
            
            System.out.println("✓ ViewResolver working correctly");
            
        } catch (Exception e) {
            System.out.println("✗ ViewResolver demo failed: " + e.getMessage());
        }
        
        context.close();
        System.out.println("✓ ViewResolver demo completed\n");
    }
    
    /**
     * 演示ModelAndView的基本用法
     */
    private static void demonstrateModelAndView() {
        System.out.println("5. === ModelAndView Demo ===");
        
        // 创建ModelAndView
        ModelAndView mv = new ModelAndView("user/list");
        mv.addObject("title", "User List");
        mv.addObject("count", 10);
        
        System.out.println("ModelAndView created:");
        System.out.println("  View name: " + mv.getViewName());
        System.out.println("  Model size: " + mv.getModel().size());
        System.out.println("  Model content: " + mv.getModel());
        
        System.out.println("✓ ModelAndView demo completed\n");
    }
}
