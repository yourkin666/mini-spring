package com.minispring.aop.proxy;

import com.minispring.aop.JoinPoint;
import com.minispring.aop.ProceedingJoinPoint;
import com.minispring.aop.framework.AspectInfo;
import com.minispring.aop.pointcut.PointcutExpression;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于CGLIB的AOP代理实现
 * AOP模块的核心代理机制，使用CGLIB字节码生成技术创建代理对象
 */
public class CglibAopProxy implements MethodInterceptor {
    
    private final Object target;
    private final Class<?> targetClass;
    private final List<AspectInfo> aspects;
    
    public CglibAopProxy(Object target, List<AspectInfo> aspects) {
        this.target = target;
        this.targetClass = target.getClass();
        this.aspects = aspects;
    }
    
    /**
     * 创建代理对象
     */
    public Object getProxy() {
        return getProxy(null);
    }
    
    /**
     * 使用指定的类加载器创建代理对象
     */
    public Object getProxy(ClassLoader classLoader) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(this);
        
        if (classLoader != null) {
            enhancer.setClassLoader(classLoader);
        }
        
        return enhancer.create();
    }
    
    /**
     * CGLIB方法拦截器实现
     * 这是AOP的核心：在方法调用时织入切面逻辑
     */
    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        // 获取匹配的切面信息
        List<AspectInfo> matchedAspects = getMatchedAspects(method);
        
        if (matchedAspects.isEmpty()) {
            // 没有匹配的切面，直接调用原方法
            return methodProxy.invoke(target, args);
        }
        
        // 创建连接点信息
        JoinPointImpl joinPoint = new JoinPointImpl(method, args, target, proxy);
        
        // 执行切面逻辑
        return executeAspects(matchedAspects, joinPoint, () -> methodProxy.invoke(target, args));
    }
    
    /**
     * 获取匹配当前方法的切面
     */
    private List<AspectInfo> getMatchedAspects(Method method) {
        List<AspectInfo> matchedAspects = new ArrayList<>();
        
        for (AspectInfo aspectInfo : aspects) {
            String pointcutExpression = aspectInfo.getPointcutExpression();
            if (pointcutExpression != null && !pointcutExpression.isEmpty()) {
                try {
                    PointcutExpression pointcut = new PointcutExpression(pointcutExpression);
                    if (pointcut.matches(method, targetClass)) {
                        matchedAspects.add(aspectInfo);
                    }
                } catch (Exception e) {
                    System.err.println("切点表达式解析失败: " + pointcutExpression + ", 错误: " + e.getMessage());
                }
            }
        }
        
        return matchedAspects;
    }
    
    /**
     * 执行切面逻辑链
     */
    private Object executeAspects(List<AspectInfo> aspects, JoinPointImpl joinPoint, MethodInvocation invocation) throws Throwable {
        if (aspects.isEmpty()) {
            return invocation.proceed();
        }
        
        // 构建拦截器链并执行
        return new AspectInterceptorChain(aspects, joinPoint, invocation).proceed();
    }
    
    /**
     * 方法调用接口
     */
    @FunctionalInterface
    private interface MethodInvocation {
        Object proceed() throws Throwable;
    }
    
    /**
     * 切面拦截器链实现
     */
    private static class AspectInterceptorChain implements ProceedingJoinPoint {
        private final List<AspectInfo> aspects;
        private final JoinPointImpl joinPoint;
        private final MethodInvocation targetInvocation;
        private int currentIndex = 0;
        
        public AspectInterceptorChain(List<AspectInfo> aspects, JoinPointImpl joinPoint, MethodInvocation targetInvocation) {
            this.aspects = aspects;
            this.joinPoint = joinPoint;
            this.targetInvocation = targetInvocation;
        }
        
        @Override
        public Object proceed() throws Throwable {
            return proceed(joinPoint.getArgs());
        }
        
        @Override
        public Object proceed(Object[] args) throws Throwable {
            if (currentIndex >= aspects.size()) {
                // 所有切面都执行完了，调用目标方法
                return targetInvocation.proceed();
            }
            
            AspectInfo aspectInfo = aspects.get(currentIndex++);
            return aspectInfo.invoke(this, joinPoint);
        }
        
        @Override
        public Method getMethod() {
            return joinPoint.getMethod();
        }
        
        @Override
        public Object[] getArgs() {
            return joinPoint.getArgs();
        }
        
        @Override
        public Object getTarget() {
            return joinPoint.getTarget();
        }
        
        @Override
        public Object getThis() {
            return joinPoint.getThis();
        }
    }
    
    /**
     * 连接点实现
     */
    private static class JoinPointImpl implements JoinPoint {
        private final Method method;
        private final Object[] args;
        private final Object target;
        private final Object proxy;
        
        public JoinPointImpl(Method method, Object[] args, Object target, Object proxy) {
            this.method = method;
            this.args = args;
            this.target = target;
            this.proxy = proxy;
        }
        
        @Override
        public Method getMethod() {
            return method;
        }
        
        @Override
        public Object[] getArgs() {
            return args;
        }
        
        @Override
        public Object getTarget() {
            return target;
        }
        
        @Override
        public Object getThis() {
            return proxy;
        }
        
        @Override
        public String getSignature() {
            return method.toGenericString();
        }
    }
}