package com.minispring.ioc.context;

import com.minispring.aop.framework.AopBeanPostProcessor;
import com.minispring.ioc.annotation.*;
import com.minispring.ioc.beans.*;
import com.minispring.ioc.core.ReflectionUtils;
import com.minispring.ioc.core.TypeConverter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注解配置应用上下文 - Spring风格的IoC容器实现
 * 支持注解驱动配置、组件扫描、Bean生命周期管理、AOP集成
 */
public class AnnotationConfigApplicationContext implements ApplicationContext, Closeable {
    
    private final DefaultBeanFactory beanFactory;
    private final ComponentScanner componentScanner;
    private final Properties properties;
    private final Set<Class<?>> configurationClasses;
    private final List<DisposableBean> disposableBeans;
    
    private boolean active = false;
    
    public AnnotationConfigApplicationContext() {
        this.beanFactory = new DefaultBeanFactory();
        this.componentScanner = new ComponentScanner();
        this.properties = new Properties();
        this.configurationClasses = new HashSet<>();
        this.disposableBeans = new ArrayList<>();
        
        // 注册默认的BeanPostProcessor
        registerDefaultBeanPostProcessors();
        loadProperties();
    }
    
    public AnnotationConfigApplicationContext(Class<?>... configClasses) {
        this();
        register(configClasses);
        refresh(); 
    }
     
    /**
     * 注册配置类
     */
    public void register(Class<?>... configClasses) {
        for (Class<?> configClass : configClasses) {
            this.configurationClasses.add(configClass);
        }
    }
    
    /**
     * 扫描指定包路径
     */
    public void scan(String... basePackages) {
        for (String basePackage : basePackages) {
            Set<Class<?>> scannedClasses = componentScanner.scan(basePackage);
            for (Class<?> clazz : scannedClasses) {
                registerBean(clazz);
            }
        }
    }
    
    @Override
    public void refresh() {
        try {
            // 1. 处理配置类
            processConfigurationClasses();
            
            // 2. 实例化所有非延迟加载的单例Bean
            preInstantiateSingletons();
            
            // 3. 标记容器为活跃状态
            this.active = true;
            
            System.out.println("Spring容器启动完成，共注册了 " + beanFactory.getBeanDefinitionNames().length + " 个Bean");
            
        } catch (Exception e) {
            throw new RuntimeException("容器刷新失败", e);
        }
    }
    
    /**
     * 处理配置类
     */
    private void processConfigurationClasses() {
        for (Class<?> configClass : configurationClasses) {
            processConfigurationClass(configClass);
        }
    }
    
    /**
     * 处理单个配置类
     */
    private void processConfigurationClass(Class<?> configClass) {
        // 1. 注册配置类本身
        registerBean(configClass);
        
        // 2. 处理@ComponentScan注解
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScan = configClass.getAnnotation(ComponentScan.class);
            String[] basePackages = componentScan.value();
            if (basePackages.length == 0) {
                basePackages = componentScan.basePackages();
            }
            if (basePackages.length == 0) {
                // 默认扫描配置类所在包
                basePackages = new String[]{configClass.getPackage().getName()};
            }
            
            for (String basePackage : basePackages) {
                Set<Class<?>> scannedClasses = componentScanner.scan(basePackage);
                for (Class<?> clazz : scannedClasses) {
                    registerBean(clazz);
                }
            }
        }
    }
    
    /**
     * 注册Bean
     */
    private void registerBean(Class<?> beanClass) {
        String beanName = determineBeanName(beanClass);
        
        if (beanFactory.containsBean(beanName)) {
            return; // 已注册，跳过
        }
        
        BeanDefinition beanDefinition = new BeanDefinition(beanClass);
        
        // 处理作用域
        if (beanClass.isAnnotationPresent(Component.class)) {
            Component component = beanClass.getAnnotation(Component.class);
            // 这里可以扩展处理Scope注解
        }
        
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
        System.out.println("注册Bean: " + beanName + " -> " + beanClass.getSimpleName());
    }
    
    /**
     * 确定Bean名称
     */
    private String determineBeanName(Class<?> beanClass) {
        Component component = beanClass.getAnnotation(Component.class);
        if (component != null && !component.value().isEmpty()) {
            return component.value();
        }
        
        // 默认使用类名首字母小写
        String className = beanClass.getSimpleName();
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }
    
    /**
     * 预实例化所有单例Bean
     */
    private void preInstantiateSingletons() {
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        
        for (String beanName : beanNames) {
            BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
            if (bd != null && bd.isSingleton() && !bd.isLazyInit()) {
                try {
                    Object bean = beanFactory.getBean(beanName);
                    
                    // 收集DisposableBean
                    if (bean instanceof DisposableBean) {
                        disposableBeans.add((DisposableBean) bean);
                    }
                    
                } catch (Exception e) {
                    System.err.println("预实例化Bean失败: " + beanName + ", 错误: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * 注册默认的BeanPostProcessor
     */
    private void registerDefaultBeanPostProcessors() {
        // 注册AOP支持
        AopBeanPostProcessor aopBeanPostProcessor = new AopBeanPostProcessor(beanFactory);
        beanFactory.addBeanPostProcessor(aopBeanPostProcessor);
    }
    
    /**
     * 加载属性文件
     */
    private void loadProperties() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                properties.load(is);
                System.out.println("加载配置文件: application.properties");
            }
        } catch (IOException e) {
            System.out.println("未找到application.properties配置文件，使用默认配置");
        }
    }
    
    // BeanFactory接口实现
    @Override
    public Object getBean(String name) throws BeansException {
        assertActive();
        return beanFactory.getBean(name);
    }
    
    @Override
    public <T> T getBean(Class<T> type) throws BeansException {
        assertActive();
        return beanFactory.getBean(type);
    }
    
    @Override
    public <T> T getBean(String name, Class<T> type) throws BeansException {
        assertActive();
        return beanFactory.getBean(name, type);
    }
    
    @Override
    public boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }
    
    @Override
    public boolean isSingleton(String name) {
        return beanFactory.isSingleton(name);
    }
    
    @Override
    public Class<?> getType(String name) {
        return beanFactory.getType(name);
    }
    
    // ApplicationContext接口实现
    @Override
    public String[] getBeanDefinitionNames() {
        return beanFactory.getBeanDefinitionNames();
    }
    
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> result = new HashMap<>();
        String[] beanNames = beanFactory.getBeanNamesForType(type);
        
        for (String beanName : beanNames) {
            try {
                T bean = beanFactory.getBean(beanName, type);
                result.put(beanName, bean);
            } catch (Exception e) {
                System.err.println("获取类型为 " + type.getName() + " 的Bean失败: " + beanName);
            }
        }
        
        return result;
    }
    
    @Override
    public void close() {
        if (!active) {
            return;
        }
        
        System.out.println("正在关闭Spring容器...");
        
        // 调用所有DisposableBean的destroy方法
        for (DisposableBean disposableBean : disposableBeans) {
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                System.err.println("调用DisposableBean.destroy()失败: " + e.getMessage());
            }
        }
        
        // 调用所有Bean的@PreDestroy方法
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            try {
                Object bean = beanFactory.getBean(beanName);
                invokeDestroyMethods(bean);
            } catch (Exception e) {
                System.err.println("调用Bean销毁方法失败: " + beanName + ", 错误: " + e.getMessage());
            }
        }
        
        this.active = false;
        System.out.println("Spring容器已关闭");
    }
    
    /**
     * 调用Bean的销毁方法
     */
    private void invokeDestroyMethods(Object bean) throws Exception {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(PreDestroy.class)) {
                method.setAccessible(true);
                method.invoke(bean);
            }
        }
    }
    
    private void assertActive() {
        if (!active) {
            throw new IllegalStateException("ApplicationContext is not active - call refresh() before accessing beans");
        }
    }
    
    /**
     * 获取属性值
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * 获取属性值（带默认值）
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * 检查容器是否活跃
     */
    public boolean isActive() {
        return active;
    }
}