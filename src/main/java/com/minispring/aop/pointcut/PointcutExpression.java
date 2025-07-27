package com.minispring.aop.pointcut;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * 切点表达式解析器
 * 支持简化版的AspectJ表达式语法
 */
public class PointcutExpression {
    
    private final String expression;
    private final ExpressionType type;
    private final Pattern pattern;
    
    public PointcutExpression(String expression) {
        this.expression = expression;
        this.type = parseExpressionType(expression);
        this.pattern = compilePattern(expression);
    }
    
    /**
     * 判断方法是否匹配此切点表达式
     */
    public boolean matches(Method method, Class<?> targetClass) {
        switch (type) {
            case EXECUTION:
                return matchesExecution(method, targetClass);
            case WITHIN:
                return matchesWithin(targetClass);
            case ANNOTATION:
                return matchesAnnotation(method);
            default:
                return false;
        }
    }
    
    /**
     * 解析表达式类型
     */
    private ExpressionType parseExpressionType(String expression) {
        if (expression.startsWith("execution(")) {
            return ExpressionType.EXECUTION;
        } else if (expression.startsWith("within(")) {
            return ExpressionType.WITHIN;
        } else if (expression.startsWith("@annotation(")) {
            return ExpressionType.ANNOTATION;
        } else {
            throw new IllegalArgumentException("不支持的切点表达式: " + expression);
        }
    }
    
    /**
     * 编译表达式为正则模式
     */
    private Pattern compilePattern(String expression) {
        String patternStr;
        
        switch (type) {
            case EXECUTION:
                // execution(* com.minispring.example..*(..))
                patternStr = extractExecutionPattern(expression);
                break;
            case WITHIN:
                // within(com.minispring.example..*)
                patternStr = extractWithinPattern(expression);
                break;
            case ANNOTATION:
                // @annotation(com.minispring.annotation.Transactional)
                patternStr = extractAnnotationPattern(expression);
                break;
            default:
                throw new IllegalArgumentException("不支持的表达式类型: " + type);
        }
        
        return Pattern.compile(patternStr);
    }
    
    /**
     * 提取execution表达式的模式
     * execution(修饰符 返回类型 包.类.方法(参数))
     */
    private String extractExecutionPattern(String expression) {
        // execution(* com.minispring.example..*(..))
        String inner = expression.substring(10, expression.length() - 1); // 去掉 execution( 和 )
        
        // 简化处理：支持 * 和 .. 通配符
        String regex = inner
                .replaceAll("\\.", "\\\\.")     // . -> \.
                .replaceAll("\\.\\.", ".*")      // .. -> .*
                .replaceAll("\\*", "[^.]*")      // * -> [^.]*
                .replaceAll("\\(\\.\\.", "\\\\(.*")  // (.. -> \(.*
                .replaceAll("\\.\\.\\)", ".*\\\\)")  // ..) -> .*\)
                .replaceAll("\\(\\)", "\\\\(\\\\)"); // () -> \(\)
        
        return regex;
    }
    
    /**
     * 提取within表达式的模式
     */
    private String extractWithinPattern(String expression) {
        // within(com.minispring.example..*)
        String inner = expression.substring(7, expression.length() - 1); // 去掉 within( 和 )
        
        return inner
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\.\\.", ".*")
                .replaceAll("\\*", "[^.]*");
    }
    
    /**
     * 提取annotation表达式的模式
     */
    private String extractAnnotationPattern(String expression) {
        // @annotation(com.minispring.annotation.Transactional)
        return expression.substring(12, expression.length() - 1); // 去掉 @annotation( 和 )
    }
    
    /**
     * 匹配execution表达式
     */
    private boolean matchesExecution(Method method, Class<?> targetClass) {
        // 构建方法签名字符串用于匹配
        String methodSignature = buildMethodSignature(method, targetClass);
        return pattern.matcher(methodSignature).matches();
    }
    
    /**
     * 匹配within表达式
     */
    private boolean matchesWithin(Class<?> targetClass) {
        String className = targetClass.getName();
        return pattern.matcher(className).matches();
    }
    
    /**
     * 匹配annotation表达式
     */
    private boolean matchesAnnotation(Method method) {
        String annotationName = pattern.pattern();
        try {
            Class<?> annotationClass = Class.forName(annotationName);
            return method.isAnnotationPresent((Class) annotationClass);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * 构建方法签名字符串
     * 格式：修饰符 返回类型 包.类.方法(参数类型...)
     */
    private String buildMethodSignature(Method method, Class<?> targetClass) {
        StringBuilder signature = new StringBuilder();
        
        // 返回类型（简化为*）
        signature.append("* ");
        
        // 类名.方法名
        signature.append(targetClass.getName())
                .append(".")
                .append(method.getName());
        
        // 参数列表
        signature.append("(");
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 0) {
            signature.append(")");
        } else {
            // 简化处理：所有参数用..表示
            signature.append("..)");
        }
        
        return signature.toString();
    }
    
    /**
     * 表达式类型枚举
     */
    private enum ExpressionType {
        EXECUTION,  // execution() 表达式
        WITHIN,     // within() 表达式
        ANNOTATION  // @annotation() 表达式
    }
    
    public String getExpression() {
        return expression;
    }
    
    @Override
    public String toString() {
        return "PointcutExpression{" +
                "expression='" + expression + '\'' +
                ", type=" + type +
                '}';
    }
}