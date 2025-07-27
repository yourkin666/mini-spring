# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a simplified Spring Framework implementation for learning purposes. It includes core Spring features: IoC container, dependency injection, annotation-driven configuration, bean lifecycle management, and **CGLIB-based AOP (Aspect-Oriented Programming)**.

## Build and Development Commands

```bash
# Compile the project
mvn compile

# Run tests
mvn test

# Run the main demo
mvn compile exec:java -Dexec.mainClass="com.minispring.example.MiniSpringDemo"

# Run the enhanced demo with lifecycle methods
mvn compile exec:java -Dexec.mainClass="com.minispring.example.EnhancedMiniSpringDemo"

# Run the IoC demo (独立IoC功能)
mvn compile exec:java -Dexec.mainClass="com.minispring.example.ioc.IocDemo"

# Run the AOP demo (独立AOP功能)  
mvn compile exec:java -Dexec.mainClass="com.minispring.example.aop.AopDemo"

# Run the integrated demo (IoC+AOP集成)
mvn compile exec:java -Dexec.mainClass="com.minispring.example.integration.IntegratedDemo"

# Clean and compile
mvn clean compile
```

## Project Structure

```
mini-spring/
├── src/
│   ├── main/java/com/minispring/
│   │   ├── ioc/                     # IoC容器模块（独立）
│   │   │   ├── annotation/          # IoC注解定义
│   │   │   │   ├── Component.java   # 组件标识注解
│   │   │   │   ├── Autowired.java   # 依赖注入注解
│   │   │   │   ├── Value.java       # 属性值注入注解
│   │   │   │   ├── ComponentScan.java # 组件扫描注解
│   │   │   │   ├── PostConstruct.java # 初始化回调注解
│   │   │   │   └── PreDestroy.java  # 销毁回调注解
│   │   │   ├── beans/               # Bean工厂核心层
│   │   │   │   ├── BeanFactory.java # Bean工厂接口
│   │   │   │   ├── DefaultBeanFactory.java # 默认Bean工厂实现(三级缓存)
│   │   │   │   ├── AnnotationBeanFactory.java # 注解驱动Bean工厂
│   │   │   │   ├── BeanDefinition.java # Bean定义元数据
│   │   │   │   ├── ConstructorArgument.java # 构造参数
│   │   │   │   ├── PropertyValue.java # 属性值
│   │   │   │   ├── InitializingBean.java # 初始化接口
│   │   │   │   ├── DisposableBean.java # 销毁接口
│   │   │   │   └── *Exception.java  # 异常类族
│   │   │   ├── context/             # 应用上下文层
│   │   │   │   ├── ApplicationContext.java # 应用上下文接口
│   │   │   │   ├── AnnotationConfigApplicationContext.java # 注解配置上下文
│   │   │   │   └── ComponentScanner.java # 组件扫描器
│   │   │   └── core/                # 核心工具层
│   │   │       ├── ReflectionUtils.java # 反射工具类
│   │   │       └── TypeConverter.java # 类型转换器
│   │   │
│   │   ├── aop/                     # AOP模块（独立）
│   │   │   ├── annotation/          # AOP注解定义
│   │   │   │   ├── Aspect.java      # 切面注解
│   │   │   │   ├── Before.java      # 前置通知
│   │   │   │   ├── After.java       # 后置通知
│   │   │   │   ├── Around.java      # 环绕通知
│   │   │   │   ├── AfterReturning.java # 返回后通知
│   │   │   │   ├── AfterThrowing.java # 异常后通知
│   │   │   │   └── Pointcut.java    # 切点定义
│   │   │   ├── framework/           # AOP框架核心
│   │   │   │   ├── AspectInfo.java  # 切面信息封装
│   │   │   │   ├── AspectParser.java # 切面解析器
│   │   │   │   └── AopBeanPostProcessor.java # AOP后置处理器
│   │   │   ├── proxy/               # 代理机制
│   │   │   │   └── CglibAopProxy.java # CGLIB代理实现
│   │   │   ├── pointcut/            # 切点表达式
│   │   │   │   └── PointcutExpression.java # 切点表达式解析
│   │   │   ├── JoinPoint.java       # 连接点接口
│   │   │   └── ProceedingJoinPoint.java # 环绕通知连接点
│   │   │
│   │   ├── integration/             # 集成模块
│   │   │   └── AopIocIntegration.java # AOP与IoC的集成桥梁
│   │   │
│   │   └── example/                 # 示例演示层
│   │       ├── ioc/                 # IoC独立示例
│   │       │   ├── IocDemo.java     # 纯IoC功能演示
│   │       │   └── IocConfig.java   # IoC配置
│   │       ├── aop/                 # AOP独立示例
│   │       │   ├── LoggingAspect.java # 日志切面
│   │       │   ├── BusinessService.java # 业务服务
│   │       │   └── AopConfig.java   # AOP配置
│   │       └── integration/         # 集成示例
│   │           ├── IntegratedDemo.java # IoC+AOP集成演示
│   │           └── IntegratedConfig.java # 集成配置
│   │
│   ├── test/java/com/minispring/    # 测试代码
│   │   ├── MiniSpringTest.java      # 基础功能测试
│   │   ├── EnhancedMiniSpringTest.java # 高级功能测试
│   │   ├── AopTest.java             # AOP功能测试
│   │   └── example/                 # 测试示例
│   │       ├── AppConfig.java       # 配置类
│   │       ├── UserService.java     # 业务服务
│   │       ├── UserRepository.java  # 数据访问
│   │       └── DatabaseService.java # 数据库服务
│   │
│   └── main/resources/
│       └── application.properties   # 应用配置文件
│
├── pom.xml                         # Maven构建配置
└── README.md                       # 项目说明文档
```

## Architecture Overview

### 🏗️ 双核心模块设计

这个项目采用**模块化分离**设计，将Spring的两大核心特性完全分开：

### 1. **IoC容器模块** (`com.minispring.ioc`) - 独立可用
- **注解层**: `@Component`, `@Autowired`, `@Value`, `@ComponentScan`, `@PostConstruct`, `@PreDestroy`
- **Bean工厂层**: `DefaultBeanFactory`(三级缓存), `AnnotationBeanFactory`(注解驱动)
- **应用上下文层**: `AnnotationConfigApplicationContext`(高级容器管理)
- **核心工具层**: `ReflectionUtils`(反射工具), `TypeConverter`(类型转换)

### 2. **AOP模块** (`com.minispring.aop`) - 独立可用
- **注解层**: `@Aspect`, `@Before`, `@After`, `@Around`, `@AfterReturning`, `@AfterThrowing`, `@Pointcut`
- **框架核心层**: `AspectInfo`(切面封装), `AspectParser`(切面解析), `AopBeanPostProcessor`(后置处理)
- **代理层**: `CglibAopProxy`(CGLIB统一代理)
- **切点层**: `PointcutExpression`(表达式解析), `JoinPoint`/`ProceedingJoinPoint`(连接点)

### 3. **集成模块** (`com.minispring.integration`)
- **AopIocIntegration**: 提供AOP与IoC的集成桥梁，实现两个模块的无缝协作

### 🎯 模块特点

- **独立性**: IoC和AOP模块可以单独使用，互不依赖
- **可组合性**: 通过集成模块实现功能组合
- **清晰边界**: 每个模块职责明确，便于理解和维护
- **扩展性**: 易于添加新的模块或功能

### Bean Lifecycle and Circular Dependencies

The framework implements Spring's three-level cache mechanism:
- **Level 1**: Complete singleton beans (`singletonObjects`)
- **Level 2**: Early singleton beans (`earlySingletonObjects`) 
- **Level 3**: Singleton factories (`singletonFactories`)

Bean creation flow in `DefaultBeanFactory:208-230`:
1. Instance creation
2. Early exposure for circular dependency resolution
3. Property injection (dependency injection)
4. Bean initialization (`@PostConstruct`, `InitializingBean`)

### Configuration and Properties

- Default properties loaded from `src/main/resources/application.properties`
- `@Value` supports placeholder expressions: `${property.name:defaultValue}`
- Type conversion handled by `TypeConverter` for basic types

## Key Implementation Details

- **Reflection utilities**: `ReflectionUtils` provides annotation and field/method access helpers
- **Exception hierarchy**: Custom exceptions extend `BeansException` for different failure scenarios  
- **Bean scopes**: Supports singleton (default) and prototype scopes
- **Component scanning**: Uses Reflections library to discover annotated classes
- **Lifecycle management**: `DisposableBean` interface and `@PreDestroy` for cleanup
- **CGLIB AOP**: 统一的代理机制，使用CGLIB字节码增强技术支持所有类型的Bean
- **Pointcut expressions**: Supports execution(), within(), and @annotation() patterns
- **Aspect integration**: Seamless integration with IoC through `AopBeanPostProcessor`

## Testing

Test classes are in `src/test/java/com/minispring/`:
- `MiniSpringTest.java`: Basic functionality tests
- `EnhancedMiniSpringTest.java`: Advanced features including lifecycle methods
- `AopTest.java`: Comprehensive AOP functionality tests
- Example services in `src/test/java/com/minispring/example/` demonstrate usage patterns

## Development Notes

- Java 8+ required (configured in pom.xml)
- Uses JUnit 5 for testing  
- Maven Surefire plugin for test execution
- External dependencies: Reflections (package scanning), CGLIB (AOP proxy generation)

## Development Guidelines

- Always respond in Chinese when working with this codebase
- **AOP (面向切面编程) 是本项目的重点实现目标**
- 注重模仿Spring AOP的核心机制和设计理念