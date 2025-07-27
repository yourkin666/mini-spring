package com.minispring.ioc.beans;

import com.minispring.ioc.annotation.*;
import com.minispring.ioc.core.ReflectionUtils;
import com.minispring.ioc.core.TypeConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认Bean工厂实现 - Spring风格的IoC容器核心
 * 实现三级缓存解决循环依赖，完整的Bean生命周期管理
 */
public class DefaultBeanFactory implements BeanFactory {
    
    // 三级缓存 - Spring循环依赖解决方案
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(); // 一级缓存：完成的单例
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(); // 二级缓存：早期单例
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>(); // 三级缓存：单例工厂
    
    // Bean定义注册表
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private final List<String> beanDefinitionNames = new ArrayList<>();
    
    // Bean后置处理器
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    
    // 正在创建的Bean集合（用于检测循环依赖）
    private final Set<String> singletonsCurrentlyInCreation = Collections.synchronizedSet(new HashSet<>());
    
    
    @Override
    public Object getBean(String name) throws BeansException {
        return doGetBean(name, null);
    }
    
    @Override
    public <T> T getBean(Class<T> type) throws BeansException {
        String[] beanNames = getBeanNamesForType(type);
        if (beanNames.length == 0) {
            throw new BeansException("No bean of type " + type.getName() + " found");
        }
        if (beanNames.length > 1) {
            throw new BeansException("Multiple beans of type " + type.getName() + " found: " + Arrays.toString(beanNames));
        }
        return (T) getBean(beanNames[0]);
    }
    
    @Override
    public <T> T getBean(String name, Class<T> type) throws BeansException {
        Object bean = getBean(name);
        if (!type.isInstance(bean)) {
            throw new BeansException("Bean '" + name + "' is not of type " + type.getName());
        }
        return (T) bean;
    }
    
    /**
     * 核心获取Bean方法 - 实现三级缓存
     */
    protected Object doGetBean(String name, Class<?> requiredType) throws BeansException {
        // 1. 尝试从三级缓存获取
        Object singleton = getSingleton(name);
        if (singleton != null) {
            return singleton;
        }
        
        // 2. 获取Bean定义
        BeanDefinition beanDefinition = getBeanDefinition(name);
        if (beanDefinition == null) {
            throw new BeansException("No bean named '" + name + "' is defined");
        }
        
        // 3. 创建Bean实例
        if (beanDefinition.isSingleton()) {
            singleton = getSingleton(name, () -> createBean(name, beanDefinition));
            return singleton;
        } else {
            return createBean(name, beanDefinition);
        }
    }
    
    /**
     * 三级缓存获取单例Bean
     */
    protected Object getSingleton(String beanName) {
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            singletonObject = earlySingletonObjects.get(beanName);
            if (singletonObject == null) {
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    singletonObject = singletonFactory.getObject();
                    earlySingletonObjects.put(beanName, singletonObject);
                    singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }
    
    /**
     * 获取单例Bean（带工厂方法）
     */
    protected Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (singletonObjects) {
            Object singletonObject = singletonObjects.get(beanName);
            if (singletonObject == null) {
                beforeSingletonCreation(beanName);
                try {
                    singletonObject = singletonFactory.getObject();
                    addSingleton(beanName, singletonObject);
                } finally {
                    afterSingletonCreation(beanName);
                }
            }
            return singletonObject;
        }
    }
    
    /**
     * 创建Bean实例 - 完整的生命周期管理
     */
    protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
        try {
            // 1. 创建Bean实例
            Object bean = createBeanInstance(beanName, beanDefinition);
            
            // 2. 早期暴露Bean（解决循环依赖）
            if (beanDefinition.isSingleton() && isSingletonCurrentlyInCreation(beanName)) {
                addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, beanDefinition, bean));
            }
            
            // 3. 属性注入
            populateBean(beanName, beanDefinition, bean);
            
            // 4. 初始化Bean
            Object exposedObject = initializeBean(beanName, bean, beanDefinition);
            
            return exposedObject;
        } catch (Exception e) {
            throw new BeanCreationException(beanName, "Bean creation failed", e);
        }
    }
    
    /**
     * 创建Bean实例
     */
    protected Object createBeanInstance(String beanName, BeanDefinition beanDefinition) throws Exception {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        
        // 寻找带@Autowired注解的构造函数
        Constructor<?> targetConstructor = null;
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                targetConstructor = constructor;
                break;
            }
        }
        
        if (targetConstructor == null) {
            targetConstructor = beanClass.getDeclaredConstructor();
        }
        
        targetConstructor.setAccessible(true);
        
        // 解析构造函数参数
        Object[] args = resolveConstructorArgs(targetConstructor, beanName);
        return targetConstructor.newInstance(args);
    }
    
    /**
     * 属性注入
     */
    protected void populateBean(String beanName, BeanDefinition beanDefinition, Object bean) throws Exception {
        Class<?> beanClass = bean.getClass();
        
        // 处理@Autowired字段注入
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                Object dependentBean = resolveDependency(field.getType(), field.getName());
                field.set(bean, dependentBean);
            } else if (field.isAnnotationPresent(Value.class)) {
                field.setAccessible(true);
                Value valueAnnotation = field.getAnnotation(Value.class);
                String value = resolveValue(valueAnnotation.value());
                Object convertedValue = TypeConverter.convertValue(value, field.getType());
                field.set(bean, convertedValue);
            }
        }
        
        // 处理@Autowired方法注入
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Autowired.class) && method.getName().startsWith("set")) {
                method.setAccessible(true);
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 1) {
                    Object dependentBean = resolveDependency(paramTypes[0], method.getName());
                    method.invoke(bean, dependentBean);
                }
            }
        }
    }
    
    /**
     * 初始化Bean
     */
    protected Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
        // 1. 调用BeanPostProcessor前置处理
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);
        
        // 2. 调用初始化方法
        invokeInitMethods(beanName, wrappedBean, beanDefinition);
        
        // 3. 调用BeanPostProcessor后置处理
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        
        return wrappedBean;
    }
    
    /**
     * 调用初始化方法
     */
    protected void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
        // 1. 调用@PostConstruct方法
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.setAccessible(true);
                method.invoke(bean);
            }
        }
        
        // 2. 调用InitializingBean.afterPropertiesSet()
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }
    }
    
    // Bean后置处理器相关方法
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.add(beanPostProcessor);
    }
    
    protected Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : beanPostProcessors) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }
    
    protected Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : beanPostProcessors) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }
    
    // Bean定义管理
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
        beanDefinitionNames.add(beanName);
    }
    
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitionMap.get(beanName);
    }
    
    @Override
    public boolean containsBean(String name) {
        return beanDefinitionMap.containsKey(name) || singletonObjects.containsKey(name);
    }
    
    @Override
    public boolean isSingleton(String name) {
        BeanDefinition bd = getBeanDefinition(name);
        return bd != null && bd.isSingleton();
    }
    
    @Override
    public Class<?> getType(String name) {
        BeanDefinition bd = getBeanDefinition(name);
        return bd != null ? bd.getBeanClass() : null;
    }
    
    public String[] getBeanDefinitionNames() {
        return beanDefinitionNames.toArray(new String[0]);
    }
    
    public String[] getBeanNamesForType(Class<?> type) {
        List<String> result = new ArrayList<>();
        for (String beanName : beanDefinitionNames) {
            BeanDefinition bd = getBeanDefinition(beanName);
            if (bd != null && type.isAssignableFrom(bd.getBeanClass())) {
                result.add(beanName);
            }
        }
        return result.toArray(new String[0]);
    }
    
    // 循环依赖管理
    protected void beforeSingletonCreation(String beanName) {
        singletonsCurrentlyInCreation.add(beanName);
    }
    
    protected void afterSingletonCreation(String beanName) {
        singletonsCurrentlyInCreation.remove(beanName);
    }
    
    protected boolean isSingletonCurrentlyInCreation(String beanName) {
        return singletonsCurrentlyInCreation.contains(beanName);
    }
    
    protected void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
        singletonFactories.remove(beanName);
        earlySingletonObjects.remove(beanName);
    }
    
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        if (!singletonObjects.containsKey(beanName)) {
            singletonFactories.put(beanName, singletonFactory);
            earlySingletonObjects.remove(beanName);
        }
    }
    
    protected Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) {
        Object exposedObject = bean;
        for (BeanPostProcessor processor : beanPostProcessors) {
            if (processor instanceof SmartInstantiationAwareBeanPostProcessor) {
                exposedObject = ((SmartInstantiationAwareBeanPostProcessor) processor)
                    .getEarlyBeanReference(exposedObject, beanName);
            }
        }
        return exposedObject;
    }
    
    // 辅助方法
    protected Object[] resolveConstructorArgs(Constructor<?> constructor, String beanName) throws Exception {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        
        for (int i = 0; i < paramTypes.length; i++) {
            args[i] = resolveDependency(paramTypes[i], "arg" + i);
        }
        
        return args;
    }
    
    protected Object resolveDependency(Class<?> type, String name) throws Exception {
        String[] beanNames = getBeanNamesForType(type);
        if (beanNames.length == 0) {
            throw new BeansException("No bean of type " + type.getName() + " found for dependency injection");
        }
        if (beanNames.length == 1) {
            return getBean(beanNames[0]);
        }
        
        // 多个候选bean，尝试按名称匹配
        for (String beanName : beanNames) {
            if (beanName.equals(name)) {
                return getBean(beanName);
            }
        }
        
        throw new BeansException("Multiple beans of type " + type.getName() + " found, unable to determine which one to inject");
    }
    
    protected String resolveValue(String value) {
        // 简单的占位符解析实现
        if (value.startsWith("${") && value.endsWith("}")) {
            String key = value.substring(2, value.length() - 1);
            String[] parts = key.split(":");
            String propertyKey = parts[0];
            String defaultValue = parts.length > 1 ? parts[1] : null;
            
            String resolved = System.getProperty(propertyKey);
            return resolved != null ? resolved : defaultValue;
        }
        return value;
    }
    
    // 对象工厂接口
    @FunctionalInterface
    public interface ObjectFactory<T> {
        T getObject() throws BeansException;
    }
    
    // 智能实例化感知Bean后置处理器
    public interface SmartInstantiationAwareBeanPostProcessor extends BeanPostProcessor {
        default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }
}