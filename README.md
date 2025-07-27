# Mini Spring Framework

一个用于学习目的的简化版Spring框架实现，包含了Spring的核心功能：IoC容器、依赖注入、AOP切面编程、注解驱动配置等。

## 功能特性

### 核心功能
- ✅ **IoC容器** - 控制反转容器，管理Bean的创建和生命周期
- ✅ **依赖注入** - 通过注解自动注入依赖关系
- ✅ **AOP切面编程** - 支持面向切面编程，包含前置、后置、环绕、异常通知
- ✅ **Bean工厂** - 提供Bean的创建、获取和管理功能
- ✅ **注解驱动** - 支持丰富的注解配置
- ✅ **组件扫描** - 自动扫描和注册带@Component注解的类
- ✅ **应用上下文** - 提供高级的容器管理和配置加载功能
- ✅ **代理机制** - 支持CGLIB动态代理

### 支持的注解

#### IoC注解
- `@Component` - 标识组件类，自动注册为Bean
- `@Autowired` - 自动装配依赖，支持字段和方法注入
- `@Value` - 注入配置值，支持占位符和默认值
- `@ComponentScan` - 指定组件扫描的包路径
- `@PostConstruct` - Bean初始化后回调
- `@PreDestroy` - Bean销毁前回调

#### AOP注解
- `@Aspect` - 标识切面类
- `@Pointcut` - 定义切点表达式
- `@Before` - 前置通知
- `@After` - 后置通知
- `@AfterReturning` - 返回后通知
- `@AfterThrowing` - 异常通知
- `@Around` - 环绕通知

## 项目结构

```
mini-spring/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/minispring/
│   │   │       ├── aop/                     # AOP切面编程
│   │   │       │   ├── annotation/          # AOP注解
│   │   │       │   │   ├── Aspect.java
│   │   │       │   │   ├── Pointcut.java
│   │   │       │   │   ├── Before.java
│   │   │       │   │   ├── After.java
│   │   │       │   │   ├── AfterReturning.java
│   │   │       │   │   ├── AfterThrowing.java
│   │   │       │   │   └── Around.java
│   │   │       │   ├── framework/           # AOP框架核心
│   │   │       │   │   ├── AopBeanPostProcessor.java
│   │   │       │   │   ├── AspectInfo.java
│   │   │       │   │   ├── AspectParser.java
│   │   │       │   │   └── ProxyFactory.java
│   │   │       │   ├── pointcut/            # 切点表达式
│   │   │       │   │   └── PointcutExpression.java
│   │   │       │   ├── proxy/               # 代理实现
│   │   │       │   │   └── CglibAopProxy.java
│   │   │       │   ├── JoinPoint.java       # 连接点
│   │   │       │   └── ProceedingJoinPoint.java
│   │   │       ├── ioc/                     # IoC容器
│   │   │       │   ├── annotation/          # IoC注解
│   │   │       │   │   ├── Component.java
│   │   │       │   │   ├── Autowired.java
│   │   │       │   │   ├── Value.java
│   │   │       │   │   ├── ComponentScan.java
│   │   │       │   │   ├── PostConstruct.java
│   │   │       │   │   └── PreDestroy.java
│   │   │       │   ├── beans/               # Bean相关类
│   │   │       │   │   ├── BeanDefinition.java
│   │   │       │   │   ├── BeanFactory.java
│   │   │       │   │   ├── DefaultBeanFactory.java
│   │   │       │   │   ├── BeanPostProcessor.java
│   │   │       │   │   ├── InitializingBean.java
│   │   │       │   │   ├── DisposableBean.java
│   │   │       │   │   └── ...
│   │   │       │   ├── context/             # 应用上下文
│   │   │       │   │   ├── ApplicationContext.java
│   │   │       │   │   ├── AnnotationConfigApplicationContext.java
│   │   │       │   │   └── ComponentScanner.java
│   │   │       │   └── core/                # 核心工具
│   │   │       │       ├── ReflectionUtils.java
│   │   │       │       └── TypeConverter.java
│   │   │       ├── integration/             # IoC与AOP集成
│   │   │       │   └── AopIocIntegration.java
│   │   │       └── example/                 # 示例代码
│   │   │           ├── aop/                 # AOP示例
│   │   │           ├── ioc/                 # IoC示例
│   │   │           ├── integration/         # 集成示例
│   │   │           ├── AopDemo.java
│   │   │           ├── CglibProxyDemo.java
│   │   │           ├── EnhancedMiniSpringDemo.java
│   │   │           └── MiniSpringDemo.java
│   │   └── resources/
│   │       └── application.properties       # 配置文件
│   └── test/
│       └── java/
│           └── com/minispring/
│               ├── AopTest.java             # AOP测试
│               ├── EnhancedMiniSpringTest.java
│               ├── MiniSpringTest.java      # IoC测试
│               └── example/                 # 测试示例
└── pom.xml
```

## 快速开始

### 1. IoC容器使用示例

#### 定义服务类
```java
@Component
public class UserRepository {
    @Value("${db.url:jdbc:h2:mem:test}")
    private String databaseUrl;
    
    public String save(String username) {
        return "User '" + username + "' saved to " + databaseUrl;
    }
}

@Component
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Value("${app.name:Mini Spring}")
    private String appName;
    
    public String createUser(String username) {
        return userRepository.save(username);
    }
}
```

#### 创建配置类
```java
@ComponentScan(basePackages = "com.example")
public class AppConfig {
}
```

#### 使用应用上下文
```java
public class Application {
    public static void main(String[] args) {
        // 创建应用上下文
        AnnotationConfigApplicationContext context = 
            new AnnotationConfigApplicationContext(AppConfig.class);
        
        // 获取Bean
        UserService userService = context.getBean(UserService.class);
        
        // 使用服务
        String result = userService.createUser("张三");
        System.out.println(result);
        
        // 关闭上下文
        context.close();
    }
}
```

### 2. AOP切面编程示例

#### 定义切面类
```java
@Aspect
@Component
public class LoggingAspect {
    
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceMethods() {}
    
    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("执行方法: " + joinPoint.getMethodName());
    }
    
    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("方法返回: " + result);
    }
    
    @Around("serviceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();
        System.out.println("方法执行耗时: " + (end - start) + "ms");
        return result;
    }
}
```

#### 业务服务类
```java
@Component
public class BusinessService {
    public String processData(String data) {
        // 模拟业务处理
        return "处理结果: " + data.toUpperCase();
    }
}
```

### 3. 集成使用示例

```java
@ComponentScan(basePackages = "com.example")
public class IntegratedConfig {
}

public class IntegratedDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = 
            new AnnotationConfigApplicationContext(IntegratedConfig.class);
        
        BusinessService service = context.getBean(BusinessService.class);
        String result = service.processData("hello world");
        System.out.println(result);
        
        context.close();
    }
}
```

## 运行示例

### IoC容器示例
```bash
mvn compile exec:java -Dexec.mainClass="com.minispring.example.ioc.IocDemo"
```

### AOP切面示例
```bash
mvn compile exec:java -Dexec.mainClass="com.minispring.example.AopDemo"
```

### 集成示例
```bash
mvn compile exec:java -Dexec.mainClass="com.minispring.example.integration.IntegratedDemo"
```

### 增强版示例
```bash
mvn compile exec:java -Dexec.mainClass="com.minispring.example.EnhancedMiniSpringDemo"
```

### 运行测试
```bash
mvn test
```

## 核心原理

### IoC容器
- 使用`DefaultBeanFactory`作为基础容器实现
- 支持单例和原型两种Bean作用域
- 提供Bean的注册、创建、获取功能
- 支持Bean生命周期回调

### 依赖注入
- 通过反射扫描@Autowired注解
- 支持字段注入和setter方法注入
- 自动解析Bean之间的依赖关系
- 支持循环依赖检测

### AOP切面编程
- 基于CGLIB实现动态代理
- 支持方法级别的切点表达式
- 提供完整的通知类型（前置、后置、环绕、异常）
- 通过BeanPostProcessor集成到IoC容器

### 组件扫描
- 使用Reflections库扫描包路径
- 自动发现@Component和@Aspect注解的类
- 将组件类注册为BeanDefinition

### 配置管理
- 支持properties文件配置
- @Value注解支持占位符表达式
- 自动类型转换（String转基本类型）

## 与Spring的对比

| 功能 | Mini Spring | Spring Framework |
|------|-------------|------------------|
| IoC容器 | ✅ 完整实现 | ✅ 完整实现 |
| 依赖注入 | ✅ 注解驱动 | ✅ 注解+XML |
| 组件扫描 | ✅ 基础扫描 | ✅ 高级过滤 |
| 配置管理 | ✅ Properties | ✅ 多种格式 |
| AOP | ✅ 基础实现 | ✅ 完整支持 |
| 生命周期 | ✅ 基础支持 | ✅ 完整支持 |
| 代理机制 | ✅ CGLIB | ✅ JDK+CGLIB |
| 事务管理 | ❌ 未实现 | ✅ 声明式事务 |
| Web支持 | ❌ 未实现 | ✅ Spring MVC |

## 学习价值

通过实现这个Mini Spring框架，你可以深入理解：

1. **IoC原理** - 控制反转如何解耦对象创建和依赖关系
2. **依赖注入机制** - 如何通过反射实现自动装配
3. **AOP原理** - 面向切面编程的实现机制和代理模式
4. **注解处理** - 如何解析和处理自定义注解
5. **Bean生命周期** - Bean从创建到销毁的完整过程
6. **容器设计模式** - 如何设计一个可扩展的IoC容器
7. **代理模式** - 动态代理在框架中的应用

## 扩展建议

可以继续添加以下功能来增强框架：

1. **JDK动态代理** - 支持接口代理
2. **事务管理** - 实现声明式事务
3. **事件机制** - 应用事件的发布和监听
4. **条件注入** - @Conditional注解支持
5. **配置类** - @Configuration和@Bean注解
6. **Profile支持** - 环境配置管理
7. **Web支持** - 实现简单的MVC框架
8. **更丰富的切点表达式** - 支持更复杂的表达式语法

## 注意事项

这是一个学习用的简化实现，不建议在生产环境中使用。真实项目请使用官方的Spring Framework。

## 开发环境

- JDK 8+
- Maven 3.6+
- 依赖库：cglib、reflections等