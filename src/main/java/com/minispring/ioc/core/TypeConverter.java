package com.minispring.ioc.core;

/**
 * 类型转换器
 * IoC模块用于将字符串值转换为目标类型
 */
public class TypeConverter {
    
    /**
     * 将字符串值转换为指定类型
     */
    public static Object convertValue(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        
        if (targetType == String.class) {
            return value;
        }
        
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        }
        
        if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        }
        
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        
        if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        }
        
        if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(value);
        }
        
        if (targetType == byte.class || targetType == Byte.class) {
            return Byte.parseByte(value);
        }
        
        if (targetType == short.class || targetType == Short.class) {
            return Short.parseShort(value);
        }
        
        if (targetType == char.class || targetType == Character.class) {
            if (value.length() == 1) {
                return value.charAt(0);
            }
            throw new IllegalArgumentException("Cannot convert string '" + value + "' to char");
        }
        
        // 如果不支持的类型，尝试返回字符串
        return value;
    }
}