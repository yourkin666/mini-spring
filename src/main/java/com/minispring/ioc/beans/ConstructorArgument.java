package com.minispring.ioc.beans;

/**
 * 构造函数参数封装类
 */
public class ConstructorArgument {
    
    private final Object value;
    private final Class<?> type;
    private final int index;
    
    public ConstructorArgument(int index, Object value) {
        this.index = index;
        this.value = value;
        this.type = value != null ? value.getClass() : null;
    }
    
    public ConstructorArgument(int index, Object value, Class<?> type) {
        this.index = index;
        this.value = value;
        this.type = type;
    }
    
    public Object getValue() {
        return value;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public int getIndex() {
        return index;
    }
}