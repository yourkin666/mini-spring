package com.minispring.ioc.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean定义元数据
 * 包含创建Bean实例所需的所有信息
 */
public class BeanDefinition {
    
    private Class<?> beanClass;
    private String scope = "singleton";
    private boolean lazyInit = false;
    private List<ConstructorArgument> constructorArguments = new ArrayList<>();
    private List<PropertyValue> propertyValues = new ArrayList<>();
    private String initMethodName;
    private String destroyMethodName;
    
    public BeanDefinition() {
    }
    
    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }
    
    public Class<?> getBeanClass() {
        return beanClass;
    }
    
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }
    
    public String getScope() {
        return scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    public boolean isLazyInit() {
        return lazyInit;
    }
    
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    
    public List<ConstructorArgument> getConstructorArguments() {
        return constructorArguments;
    }
    
    public void setConstructorArguments(List<ConstructorArgument> constructorArguments) {
        this.constructorArguments = constructorArguments;
    }
    
    public void addConstructorArgument(ConstructorArgument constructorArgument) {
        this.constructorArguments.add(constructorArgument);
    }
    
    public List<PropertyValue> getPropertyValues() {
        return propertyValues;
    }
    
    public void setPropertyValues(List<PropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }
    
    public void addPropertyValue(PropertyValue propertyValue) {
        this.propertyValues.add(propertyValue);
    }
    
    public String getInitMethodName() {
        return initMethodName;
    }
    
    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }
    
    public String getDestroyMethodName() {
        return destroyMethodName;
    }
    
    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }
    
    public boolean isSingleton() {
        return "singleton".equals(scope);
    }
    
    public boolean isPrototype() {
        return "prototype".equals(scope);
    }
}